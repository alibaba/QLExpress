package com.ql.util.express.rule;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianqiao on 16/12/8.
 */
public class Rule extends Node{


    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private List<RuleCase> ruleCases = new ArrayList<RuleCase>();

    public List<RuleCase> getRuleCases() {
        return ruleCases;
    }

    public void addRuleCases(RuleCase ruleCase) {
        this.ruleCases.add(ruleCase);
    }

    public String toQl()
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(RuleCase oneCase : ruleCases){
            if(first){
                sb.append(this.ruleCaseToScript("ql",oneCase));
                first = false;
            }else{
                sb.append(" else ").append(this.ruleCaseToScript("ql", oneCase));
            }
        }
        return sb.toString();
    }

    public String toSkylight()
    {
        StringBuilder sb = new StringBuilder();
        if(code!=null && name!=null){
            sb.append(String.format("rule '%s'\nname '%s'\n",code,name));
        }
        boolean first = true;
        for(RuleCase oneCase : ruleCases){
            if(first){
                sb.append(this.ruleCaseToScript("skylight",oneCase));
                first = false;
            }else{
                sb.append(this.ruleCaseToScript("skylight",oneCase));
            }
        }
        return sb.toString();
    }

    public String toTree()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Rule:%s(%s),%d\n",code,name,getNodeId()));
        for(RuleCase ruleCase : ruleCases){
            printLevel(ruleCase.getLevel(),sb);
            sb.append(String.format("Case:%s,%d\n","case",ruleCase.getNodeId()));
            printConitionTree(ruleCase.getCondition(),sb);
            for(Action action:ruleCase.getActions()){
                printLevel(action.getLevel(),sb);
                sb.append(String.format("Action:%s,%d\n",action.getText(),action.getNodeId()));
            }
        }
        return sb.toString();
    }

    public void printLevel(int level,StringBuilder sb)
    {
        for(int i=0;i<level;i++){
            sb.append("======");
        }
    }

    private void printConitionTree(Condition root,StringBuilder sb)
    {
        printLevel(root.getLevel(),sb);
        sb.append(String.format("Condition:%s,%d\n",root.getText(),root.getNodeId()));
        if(root.getChildren()!=null) {
            for (Condition sub : root.getChildren()) {
                printConitionTree(sub,sb);
            }
        }
    }

    private String ruleCaseToScript(String scriptType, RuleCase ruleCase)
    {
        StringBuilder sb = new StringBuilder();

        if(scriptType.equals("ql")) {
            for(Action action : ruleCase.getActions()){
                sb.append(action.getText()).append(";\n");
            }
            if (ruleCase.getCondition() instanceof EmptyCondition) {
                return String.format("{\n%s}", sb);
            } else {
                return String.format("if(%s)\n{\n%s}", ruleCase.getCondition().toString(), sb);
            }
        }else if(scriptType.equals("skylight")){
            for(Action action : ruleCase.getActions()){
                sb.append(action.getText()).append("\n");
            }
            return String.format("when %s\nthen %s", ruleCase.getCondition().toString(), sb);
        }
        return null;
    }
}
