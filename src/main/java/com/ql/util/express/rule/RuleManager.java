package com.ql.util.express.rule;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.Word;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tianqiao on 16/12/8.
 */
public class RuleManager {
    
    private static final Log log = LogFactory.getLog(RuleManager.class);
    
    public static RuleResult executeRule(ExpressRunner runner,Rule rule, IExpressContext<String,Object> context, boolean isCache, boolean isTrace)
    {
        RuleResult result = new RuleResult();
        result.setRule(rule);
        Map<String, Boolean> traceMap = new LinkedHashMap<String, Boolean>();
        result.setTraceMap(traceMap);
        Object actionResult = null;
        for(RuleCase ruleCase : rule.getRuleCases())
        {
            Condition root = ruleCase.getCondition();
            Boolean conditionResult = calculateCondition(runner,context,root,traceMap,isCache,isTrace,result);
            if(conditionResult==true){
                for(Action action :ruleCase.getActions()){
                    try {
                        traceMap.put(action.getNodeId()+"",true);
                        actionResult = runner.execute(action.getText(),context,null,isCache,isTrace);
                    } catch (Exception e) {
                        result.setHasException(true);
                        log.error("执行action出错:action=\n"+action.getText(),e);
                        actionResult = null;
                    }
                }
                break;
            }
        }
        result.setResult(actionResult);
        return result;
    }

    private static Boolean calculateCondition(ExpressRunner runner, IExpressContext<String, Object> context, Condition root, Map<String, Boolean> traceMap, boolean isCache, boolean isTrace, RuleResult result)
    {
        boolean isShortCircuit = runner.isShortCircuit();
        String key = root.getNodeId()+"";
        if(root.getType()==ConditionType.Leaf){
            String text = root.getText();
            try {
                Boolean r = (Boolean) runner.execute(text,context,null,isCache,isTrace);
                traceMap.put(key,r);
                return r;
            } catch (Exception e) {
                result.setHasException(true);
                log.error("计算condition出错:condition=\n"+text,e);
                traceMap.put(key,false);
                return false;
            }
        }


        Boolean unionLogicResult = null;
        ConditionType rootType = root.getType();
        if(root.getChildren()!=null) {
            for (Condition sub : root.getChildren()) {
                Boolean subResult = calculateCondition(runner,context,sub,traceMap,isCache,isTrace, result);
                if(unionLogicResult==null){
                    unionLogicResult = subResult;
                }else{
                    if (rootType == ConditionType.And) {
                        unionLogicResult = unionLogicResult && subResult;
                    }else if (rootType == ConditionType.Or) {
                        unionLogicResult = unionLogicResult || subResult;
                    }
                }
                if(isShortCircuit) {
                    if (rootType == ConditionType.And) {
                        if(unionLogicResult==false){
                            break;
                        }
                    }
                    if (rootType == ConditionType.Or) {
                        if(unionLogicResult==true){
                            break;
                        }
                    }
                }
            }
        }
        traceMap.put(key,unionLogicResult);
        return unionLogicResult;
    }

    public static Rule createRule(ExpressNode root, Word[] words) {
        ExpressNode ifNode = getIfRootNode(root);
        if (ifNode != null) {
            Rule rule = new Rule();
            addRuleCaseByExpress(ifNode, rule, words);
            tagRuleConitionId(rule);
            return rule;
        }
        return null;
    }

    private static Integer tagRuleConitionId(Rule rule) {
        Integer nodeId = 0;
        Integer level = 0;
        rule.setNodeId(nodeId++);
        rule.setLevel(level++);
        for(RuleCase ruleCase: rule.getRuleCases()){
            ruleCase.setNodeId(nodeId++);
            ruleCase.setLevel(level);
            Condition root = ruleCase.getCondition();
            nodeId = tagConditionNode(root,nodeId,level+1);
            List<Action>actions = ruleCase.getActions();
            for(Action action:actions){
                action.setLevel(level+1);
                action.setNodeId(nodeId++);
            }
        }
        return nodeId;
    }

    private static Integer tagConditionNode(Condition root,Integer nodeId,Integer level) {
        root.setLevel(level);
        root.setNodeId(nodeId++);
        if(root.getChildren()!=null) {
            for (Condition sub : root.getChildren()) {
                nodeId = tagConditionNode(sub, nodeId,level+1);
            }
        }
        return nodeId;
    }

    private static void addRuleCaseByExpress(ExpressNode ifNode, Rule rule, Word[] words) {
        ExpressNode[] children = ifNode.getChildren();
        ExpressNode condtion = null;
        ExpressNode action = null;
        ExpressNode nextCase = null;

        //[0]ConditionNode
        int point = 0;
        ExpressNode first = children[point];
        if (isNodeType(first, "CHILD_EXPRESS")) {
            condtion = first.getChildren()[0];
        } else {
            condtion = first;
        }
        point++;

        //[1]"then"
        if (isNodeType(children[point], "then")) {
            point++;
        }

        //[2]ActionNode
        action = children[point];
        point++;

        //[3]"else"
        if (point < children.length && isNodeType(children[point], "else")) {
            point++;

            //[4]IfNode
            if (point < children.length && isNodeType(children[point], "if")) {
                nextCase = children[point];
            }
        }
        rule.addRuleCases(createRuleCase(condtion, action, words));
        if (nextCase != null) {
            addRuleCaseByExpress(nextCase, rule, words);
        }
    }

    private static ExpressNode getIfRootNode(ExpressNode parent) {
        if (isNodeType(parent, "if")) {
            return parent;
        }
        ExpressNode[] children = parent.getChildren();
        if (children != null && children.length > 0) {
            for (ExpressNode child : children) {
                if (getIfRootNode(child) != null) {
                    return child;
                }
            }
        }
        return null;
    }

    private static RuleCase createRuleCase(ExpressNode condition, ExpressNode action, Word[] words) {

        Condition ruleCondition = new Condition();
        transferCondition(condition, ruleCondition, words);
        List<Action> actions = new ArrayList<Action>();
        if (isNodeType(action, "STAT_BLOCK")) {
            ExpressNode[] children = action.getChildren();
            for (ExpressNode actionChild : children) {
                actions.add(new Action(makeActionString(actionChild,words)));
            }
        } else {
            actions.add(new Action(makeActionString(action,words)));
        }
        RuleCase ruleCase = new RuleCase(ruleCondition, actions);
        return ruleCase;
    }

    private static void transferCondition(ExpressNode express, Condition condition, Word[] words) {
        if (isNodeType(express, "&&")) {
            condition.setType(ConditionType.And);
            condition.setText("and");
            ExpressNode[] children = express.getChildren();
            for (ExpressNode child : children) {
                Condition subCondition = new Condition();
                condition.addChild(subCondition);
                transferCondition(child, subCondition, words);
            }

        } else if (isNodeType(express, "||")) {
            condition.setType(ConditionType.Or);
            condition.setText("or");
            ExpressNode[] children = express.getChildren();
            for (ExpressNode child : children) {
                Condition subCondition = new Condition();
                condition.addChild(subCondition);
                transferCondition(child, subCondition, words);
            }
        } else {
            if (isNodeType(express, "CHILD_EXPRESS")||isNodeType(express, "STAT_BLOCK")||isNodeType(express, "STAT_SEMICOLON")) {            //注意括号的情况
                ExpressNode realExpress = express.getChildren()[0];
                condition.setPrior(true);
                transferCondition(realExpress, condition, words);
            } else {
                condition.setType(ConditionType.Leaf);
                condition.setText(makeCondtionString(express, words));

            }
        }
    }

    private static boolean isNodeType(ExpressNode node, String type) {
        return node.getNodeType().getName().equals(type);

    }

    private static String makeActionString(ExpressNode express, Word[] words)
    {
        int min = getMinNode(express);
        int max = getMaxNode(express);
        //最后需要匹配一个）括号的问题,另外还有是无参数的情况 function()
        while(max+1<words.length && (words[max+1].word.equals(")")||words[max+1].word.equals("("))){
            max++;
        }
        if(min<0) min = 0;
        if(max>=words.length) max = words.length-1;
        StringBuilder result = new StringBuilder();
        int balance = 0;//小括号的相互匹配数量
        for(int i=min;i<=max;i++)
        {
            if(words[i].word.equals("(")){
                balance++;
            }else if(words[i].word.equals(")")){
                balance--;
            }
            if(balance<0){
                balance++;//当前字符不合并，恢复成0，用于最终的判断
                break;
            }
            result.append(words[i].word);
            if(words[i].word.equals("return")){
                result.append(" ");
            }
        }
        if(balance!=0){
            System.out.println(result);
            throw new RuntimeException("括号匹配异常");
        }
        return result.toString();
    }

    private static String makeCondtionString(ExpressNode express,Word[] words)
    {
        int min = getMinNode(express);
        int max = getMaxNode(express);
        //最后需要匹配一个括号的问题
        while(max+1<words.length && (words[max+1].word.equals(")")||words[max+1].word.equals("("))){
            max++;
        }
        if(min<0) min = 0;
        if(max>=words.length) max = words.length-1;
        List<String> result = new ArrayList<>();
        int balance = 0;//小括号的相互匹配数量
        for(int i=min;i<=max;i++)
        {
            if(words[i].word.equals("(")){
                balance++;
            }else if(words[i].word.equals(")")){
                balance--;
            }
            if(balance<0){
                balance++;//当前字符不合并，恢复成0，用于最终的判断
                break;
            }
            result.add(words[i].word);
        }
        if(balance!=0){
            throw new RuntimeException("括号匹配异常");
        }
        return String.join(" ", result);
    }

    private static int getMinNode(ExpressNode express)
    {
        if(express.getChildren()==null||express.getChildren().length==0){
            return express.getWordIndex();
        }
        int wordIndex = express.getWordIndex();
        if(express.getChildren()!=null){
            for(ExpressNode child : express.getChildren()){
                int childIndex = getMinNode(child);
                if(wordIndex==-1 || childIndex<wordIndex){
                    wordIndex = childIndex;
                }
            }
        }
        return wordIndex;
    }

    private static int getMaxNode(ExpressNode express)
    {
        if(express.getChildren()==null||express.getChildren().length==0){
            return express.getWordIndex();
        }
        int wordIndex = express.getWordIndex();
        if(express.getChildren()!=null){
            for(ExpressNode child : express.getChildren()){
                int childIndex = getMaxNode(child);
                if(childIndex>wordIndex){
                    wordIndex = childIndex;
                }
            }
        }
        return wordIndex;
    }
    
    
    public static Condition createCondition(ExpressNode condition, Word[] words) {
        Condition ruleCondition = new Condition();
        transferCondition(condition, ruleCondition, words);
        return ruleCondition;
    }
    
}
