package com.ql.util.express.parse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ql.util.express.exception.QLCompileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.ExpressUtil;
import com.ql.util.express.IExpressResourceLoader;
import com.ql.util.express.match.QLMatchResult;
import com.ql.util.express.match.QLPattern;

public class ExpressParse {

	private static final Log log = LogFactory.getLog(ExpressParse.class);
	NodeTypeManager nodeTypeManager;
	IExpressResourceLoader expressResourceLoader;
    
    /**
     * 是否忽略charset类型的数据，而识别为string，比如'a' -> "a"
     * 在计算比如 '1'+'2'=='12'
     */
	private boolean ignoreConstChar = false;
	/**
	 * 是否需要高精度计算
	 */
	private boolean isPrecise = false;
    
    public boolean isIgnoreConstChar() {
        return ignoreConstChar;
    }
    
    public void setIgnoreConstChar(boolean ignoreConstChar) {
        this.ignoreConstChar = ignoreConstChar;
    }
    
    public ExpressParse(NodeTypeManager aNodeTypeManager, IExpressResourceLoader aLoader, boolean aIsPrecise){
		this.nodeTypeManager = aNodeTypeManager;
		this.expressResourceLoader = aLoader;
		this.isPrecise = aIsPrecise;
	}
	protected Word[] getExpressByName(String expressFileName) throws Exception{
		String express = this.expressResourceLoader.loadExpress(expressFileName);
		return WordSplit.parse(nodeTypeManager.splitWord, express);
	}
	protected  Word[] dealInclude(Word[] wordObjects) throws Exception{
	    boolean isInclude = false;
	    StringBuffer includeFileName = new StringBuffer();
	    int point = 0;
	    List<Word> result = new ArrayList<Word>();
	    while(point <wordObjects.length ){
	      if(wordObjects[point].word.equals("include") ==true){
	    	  isInclude = true;
	    	  includeFileName.setLength(0);
	      }else if(isInclude == true && wordObjects[point].word.equals(";") ==true) {
	    	  isInclude = false;
	    	  Word[] childExpressWord = this.getExpressByName(includeFileName.toString());
	    	  childExpressWord = this.dealInclude(childExpressWord);
	    	  for(int i=0;i< childExpressWord.length;i++){
	    		  result.add(childExpressWord[i]);
	    	  }
	      }else if(isInclude == true){
	    	  includeFileName.append(wordObjects[point].word);
	      }else{
	    	  result.add(wordObjects[point]);
	      }
	      point = point + 1;
	    }
	    return result.toArray(new Word[0]);
	}
    
    /**
     * 进行单词类型分析
     * @param aRootExpressPackage
     * @param wordObjects
     * @param selfClassDefine
     * @param dealJavaClass
     * @return
     * @throws Exception
     */
	public List<ExpressNode> transferWord2ExpressNode(ExpressPackage aRootExpressPackage,Word[] wordObjects,Map<String,String> selfClassDefine,boolean dealJavaClass) throws Exception{
		List<ExpressNode> result = new ArrayList<ExpressNode>();
		String tempWord;
		NodeType tempType;
	    int point = 0;
		ExpressPackage  tmpImportPackage = null;
		if(dealJavaClass==true){
			tmpImportPackage = new ExpressPackage(aRootExpressPackage);  
		    //先处理import，import必须放在文件的最开始，必须以;结束
		    boolean isImport = false;
		    StringBuffer importName = new StringBuffer();
		    while(point <wordObjects.length ){
		      if(wordObjects[point].word.equals("import") ==true){
		    	  isImport = true;
		    	  importName.setLength(0);
		      }else if(wordObjects[point].word.equals(";") ==true) {
		    	  isImport = false;
		    	  tmpImportPackage.addPackage(importName.toString());
		      }else if(isImport == true){
		    	  importName.append(wordObjects[point].word);
		      }else{
		    	  break;
		      }
		      point = point + 1;
		    }			
		}

		String orgiValue = null;
		Object objectValue = null;
		NodeType treeNodeType = null;
		Word tmpWordObject = null;
		while(point <wordObjects.length){
		  tmpWordObject = wordObjects[point];
		  tempWord = wordObjects[point].word;
		  
		  char firstChar = tempWord.charAt(0);
		  char lastChar = tempWord.substring(tempWord.length() - 1).toLowerCase().charAt(0);		  
		  if(firstChar >='0' && firstChar<='9'){
			  if(result.size() >0){//对 负号进行特殊处理
				  if(result.get(result.size() -1).getValue().equals("-")){
					  if(result.size() == 1 
						 || result.size() >=2 
						    && (   result.get(result.size() - 2).isTypeEqualsOrChild("OP_LIST")
						        || result.get(result.size() - 2).isTypeEqualsOrChild(",")
                                || result.get(result.size() - 2).isTypeEqualsOrChild("return")
                                || result.get(result.size() - 2).isTypeEqualsOrChild("?")
                                || result.get(result.size() - 2).isTypeEqualsOrChild(":")
						        ) 
						    && result.get(result.size() - 2).isTypeEqualsOrChild(")")==false
						    && result.get(result.size() - 2).isTypeEqualsOrChild("]")==false 
						    ){
						  result.remove(result.size() -1);
						  tempWord = "-" + tempWord;
					  }
				  }
			  }
			  if(lastChar =='d'){
				  tempType = nodeTypeManager.findNodeType("CONST_DOUBLE");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
				      objectValue = Double.valueOf(tempWord);
				  }
			  }else if(lastChar =='f'){
				  tempType = nodeTypeManager.findNodeType("CONST_FLOAT");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
				      objectValue = Float.valueOf(tempWord);
				  }
			  }else if(tempWord.indexOf(".") >=0){
				  tempType = nodeTypeManager.findNodeType("CONST_DOUBLE");
				  if(this.isPrecise == true){
					  objectValue = new BigDecimal(tempWord);
				  }else{
					  objectValue = Double.valueOf(tempWord);
				  }
			  }else if(lastChar =='l'){
				  tempType = nodeTypeManager.findNodeType("CONST_LONG");
				  tempWord = tempWord.substring(0,tempWord.length() -1);
				  objectValue = Long.valueOf(tempWord);
			  }else{
				  long tempLong = Long.parseLong(tempWord);
				  if(tempLong <= Integer.MAX_VALUE && tempLong >= Integer.MIN_VALUE){
					  tempType = nodeTypeManager.findNodeType("CONST_INTEGER");
					  objectValue = Integer.valueOf((int)tempLong);
				  }else{
					  tempType = nodeTypeManager.findNodeType("CONST_LONG");
					  objectValue = Long.valueOf(tempLong);
				  }
			  }
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  point = point + 1;
		  }else if(firstChar =='"'){
			  if(lastChar !='"' || tempWord.length() <2){
				  throw new QLCompileException("没有关闭的字符串：" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  tempType =nodeTypeManager.findNodeType("CONST_STRING");
			  objectValue = tempWord;
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  point = point + 1;
		  }else if(firstChar =='\''){
			  if(lastChar !='\'' || tempWord.length() <2){
				  throw new QLCompileException("没有关闭的字符：" + tempWord);
			  }
			  tempWord = tempWord.substring(1,tempWord.length() -1);
			  
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  if(tempWord.length() == 1 && !ignoreConstChar){ //转换为字符串
				  tempType =nodeTypeManager.findNodeType("CONST_CHAR");
				  objectValue = tempWord.charAt(0);
			  }else{
				  tempType =nodeTypeManager.findNodeType("CONST_STRING");
				  objectValue = tempWord;
			  }
			  
			  point = point + 1;
		  }else if(tempWord.equals("true") || tempWord.equals("false")){
			  tempType = nodeTypeManager.findNodeType("CONST_BOOLEAN");
			  treeNodeType = nodeTypeManager.findNodeType("CONST");
			  objectValue = Boolean.valueOf(tempWord);
			  point = point + 1;
		  }else {
				tempType = nodeTypeManager.isExistNodeTypeDefine(tempWord);
				if(tempType != null && tempType.getKind() != NodeTypeKind.KEYWORD){
					//不是关键字
					tempType = null;
				}
				if (tempType == null) {
					boolean isClass = false;
					String tmpStr = "";
					Class<?> tmpClass = null;
					if (dealJavaClass == true) {
						int j = point;
						while (j < wordObjects.length) {
							tmpStr = tmpStr + wordObjects[j].word;
							tmpClass = tmpImportPackage.getClass(tmpStr);
							if (tmpClass != null) {
								point = j + 1;
								isClass = true;
								break;
							}
							if (j < wordObjects.length - 1
									&& wordObjects[j + 1].word.equals(".") == true) {
								tmpStr = tmpStr + wordObjects[j + 1].word;
								j = j + 2;
								continue;
							} else {
								break;
							}
						}
					}
					if (isClass == true){
						tempWord = ExpressUtil.getClassName(tmpClass);
						orgiValue = tmpStr;
						tempType = nodeTypeManager.findNodeType("CONST_CLASS");
						objectValue = tmpClass;
					}else if(this.nodeTypeManager.isFunction(tempWord)){
						tempType = nodeTypeManager.findNodeType("FUNCTION_NAME");
						point = point + 1;
					}else if(selfClassDefine != null && selfClassDefine.containsKey(tempWord)){
						tempType = nodeTypeManager.findNodeType("VClass");
						point = point + 1;
				    }else{
						tempType = nodeTypeManager.findNodeType("ID");
						point = point + 1;
					}
				}else{
					point = point + 1;
				}
		  }	  
		  result.add(new ExpressNode(tempType,tempWord,orgiValue,objectValue,treeNodeType,tmpWordObject.line,tmpWordObject.col,tmpWordObject.index));
		  treeNodeType = null;
		  objectValue = null;
		  orgiValue = null;
		}
		return result;
	}

    public static void printTreeNode(StringBuilder builder,ExpressNode node, int level){
		builder.append(level+":" );
		
		for (int i = 0; i < level; i++) {
			builder.append("   ");
		}
		builder.append(node);
		if(builder.length() <100){
			for (int i = 0; i <100 - builder.length(); i++) {
				builder.append("   ");
			}
		}
		builder.append("\t"+ node.getTreeType().getName()).append("\n");
		
		List<ExpressNode> leftChildren = node.getLeftChildren();
		if (leftChildren != null && leftChildren.size() > 0) {
			for (ExpressNode item : leftChildren) {
				printTreeNode(builder,item, level + 1);
			}
		}
		List<ExpressNode> rightChildren = node.getRightChildren();
		if (rightChildren != null && rightChildren.size() > 0) {
			for (ExpressNode item : rightChildren) {
				printTreeNode(builder,item, level + 1);
			}
		}    	
    }
	public static void printTreeNode(ExpressNode node, int level) {
		StringBuilder builder = new StringBuilder();
		printTreeNode(builder,node,level);
		System.out.println(builder.toString());
	}
	
    public static void resetParent(ExpressNode node,ExpressNode parent){
		node.setParent(parent);
		List<ExpressNode> leftChildren = node.getLeftChildren();
		if (leftChildren != null && leftChildren.size() > 0) {
			for (ExpressNode item : leftChildren) {
				resetParent(item,node);
			}
		}
		List<ExpressNode> rightChildren = node.getRightChildren();
		if (rightChildren != null && rightChildren.size() > 0) {
			for (ExpressNode item : rightChildren) {
				resetParent(item,node);
			}
		}    	
    }
    /**
     * 提取自定义的Class
     * @param words
     */
	public static void fetchSelfDefineClass(Word[] words,Map<String,String> selfDefineClass){
		for(int i=0;i<words.length -1;i++){
			if("class".equals(words[i].word)){
				selfDefineClass.put(words[i+1].word, words[i+1].word);
			}
		}
	}
	public ExpressNode parse(ExpressPackage rootExpressPackage,String express,boolean isTrace,Map<String,String> selfDefineClass) throws Exception{
		Word[] words = splitWords(rootExpressPackage,express,isTrace,selfDefineClass);
		return parse(rootExpressPackage,words,express,isTrace,selfDefineClass);
	}

	public Word[]  splitWords(ExpressPackage rootExpressPackage,String express,boolean isTrace,Map<String,String> selfDefineClass) throws Exception{
		Word[] words = WordSplit.parse(this.nodeTypeManager.splitWord,express);
		if(isTrace == true && log.isDebugEnabled()){
			log.debug("执行的表达式:" + express);	
			log.debug("单词分解结果:" + WordSplit.getPrintInfo(words,","));  
		}
		words = this.dealInclude(words);
		if(isTrace == true && log.isDebugEnabled()){
			log.debug("预处理后结果:" + WordSplit.getPrintInfo(words,","));  
		}
		
		//提取自定义Class
		if(selfDefineClass == null){
			selfDefineClass = new HashMap<String,String>();
		}
		fetchSelfDefineClass(words,selfDefineClass);
		for(int i=0;i<words.length;i++){
		    words[i].index=i;
        }
		return words;
	}
    
    public ExpressNode parse(ExpressPackage rootExpressPackage,Word[] words ,String express,boolean isTrace,Map<String,String> selfDefineClass) throws Exception{
	    return parse(rootExpressPackage,words,express,isTrace,selfDefineClass,false);
    }
	public ExpressNode parse(ExpressPackage rootExpressPackage,Word[] words ,String express,boolean isTrace,Map<String,String> selfDefineClass,boolean mockRemoteJavaClass) throws Exception{

		
    	List<ExpressNode> tempList = this.transferWord2ExpressNode(rootExpressPackage,words,selfDefineClass,true);
        if(isTrace == true && log.isDebugEnabled()){
            log.debug("单词分析结果:" + printInfo(tempList,","));
        }
        //比如用在远程配置脚本，本地jvm并不包含这个java类，可以
        if(mockRemoteJavaClass){
            List<ExpressNode> tempList2 = new ArrayList<ExpressNode>();
            for(int i=0;i<tempList.size();i++){
                ExpressNode node = tempList.get(i);
                if(node.getValue().equals("new") && node.getNodeType().getKind() == NodeTypeKind.KEYWORD && i+1<tempList.size() && !"CONST_CLASS".equals(tempList.get(i+1).getNodeType().getName())){
                    tempList2.add(node);
                    //取出 ( 前面的类路径作为configClass名称
                    int end = i+1;
                    String configClass = tempList.get(end).getValue();
                    end++;
                    while (!tempList.get(end).getValue().equals("(")) {
                        configClass = configClass+tempList.get(end).getValue();
                        end++;
                    }
                    NodeType nodeType = nodeTypeManager.findNodeType("VClass");
                    ExpressNode vClassNode = new ExpressNode(nodeType,configClass);
                    tempList2.add(vClassNode);
                    i = end-1;//因为循环之后，i++，所以i=end-1
                }else {
                    tempList2.add(node);
                }
            }
            tempList = tempList2;
            if(isTrace == true && log.isDebugEnabled()){
                log.debug("修正后单词分析结果:" + printInfo(tempList,","));
            }
        }

		QLMatchResult result = QLPattern.findMatchStatement(this.nodeTypeManager, this.nodeTypeManager
						.findNodeType("PROGRAM").getPatternNode(), tempList,0);
		if(result == null){
			throw new QLCompileException("语法匹配失败");
		}
		if(result.getMatchLastIndex() < tempList.size()){
			int maxPoint = result.getMatchLastIndex();
			ExpressNode tempNode = tempList.get(maxPoint);
			throw new QLCompileException("还有单词没有完成语法匹配：" + result.getMatchLastIndex() +"["+ tempNode.getValue() + ":line=" + tempNode.getLine() + ",col=" + tempNode.getCol() +"] 之后的单词 \n" + express);
		}
		result.getMatchs().get(0).buildExpressNodeTree();
		ExpressNode root =(ExpressNode)result.getMatchs().get(0).getRef();
		
		//为了生成代码时候进行判断，需要设置每个节点的父亲
    	resetParent(root,null);
    	
    	if(isTrace == true && log.isDebugEnabled()){
    		log.debug("最后的语法树:" );
    		printTreeNode(root,1);
    	}
		return root;
	}

	   public static String printInfo(List<ExpressNode> list,String splitOp){
		  	StringBuffer buffer = new StringBuffer();
			for(int i=0;i<list.size();i++){
				if(i > 0){buffer.append(splitOp);}
				buffer.append(list.get(i));
			}
			return buffer.toString();
		  }
}

