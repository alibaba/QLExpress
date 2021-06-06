package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.match.INodeTypeManager;

public class NodeTypeManager implements INodeTypeManager {
	private static final Log log = LogFactory.getLog(NodeTypeManager.class);
	
		public String[] splitWord;
		private String[] keyWords;
		private String[] nodeTypeDefines;
		protected String[][] instructionFacotryMapping;
	    protected Map<String,NodeType> nodeTypes = new HashMap<String,NodeType>();	
	    
	    //All function definitions
	    protected Map<String,String> functions = new HashMap<String,String>();
	    
	    public NodeTypeManager() {
	    	this(new KeyWordDefine4Java());
	    }
	    public NodeTypeManager(KeyWordDefine4Java keyWorkdDefine){
	    	this.splitWord = keyWorkdDefine.splitWord;
	    	com.ql.util.express.parse.WordSplit.sortSplitWord(this.splitWord);
			this.keyWords = keyWorkdDefine.keyWords;
			this.nodeTypeDefines = keyWorkdDefine.nodeTypeDefines;
			this.instructionFacotryMapping = keyWorkdDefine.instructionFacotryMapping;
			this.initial();
			this.addOperatorWithRealNodeType("and","&&");
			this.addOperatorWithRealNodeType("or","||");

	    }
	    
		public void initial() {
			//Create all keywords
			NodeType[] tempKeyWordNodeTypes = new NodeType[splitWord.length + keyWords.length];
			for (int i = 0; i < splitWord.length; i++) {
				tempKeyWordNodeTypes[i] = this.createNodeType(splitWord[i] + ":TYPE=KEYWORD");
			}
			for (int i = 0 ; i < keyWords.length; i++) {
				tempKeyWordNodeTypes[i + splitWord.length] = this.createNodeType(keyWords[i] + ":TYPE=KEYWORD");
			}
			// Initialize all type information,
			for (int i = 0; i < tempKeyWordNodeTypes.length; i++) {
				tempKeyWordNodeTypes[i].initial();
			}
			
			// Create all type information, but cannot initialize
			NodeType[] nodeTypes = new NodeType[nodeTypeDefines.length];
			for (int i = 0; i < nodeTypeDefines.length; i++) {
				nodeTypes[i] = this.createNodeType(nodeTypeDefines[i]);
			}
			// Initialize all type information,
			for (int i = 0; i < nodeTypes.length; i++) {
				nodeTypes[i].initial();
			}
			
			//Initialization instruction Factory
		if (this.instructionFacotryMapping != null) {
			for (String[] list : this.instructionFacotryMapping) {
				for (String s : list[0].split(",")) {
					this.findNodeType(s).setInstructionFactory(list[1]);
				}
			}
		}
	}
	    
	/**
	 * When creating a node type, it should be noted that it cannot be initialized, and the initialization method must be called after all types are created.
	 * @param aDefineStr
	 * @return
	 */
	public NodeType createNodeType(String aDefineStr){		
		int index = aDefineStr.indexOf(":",1);//避免对操作符号":"的错误处理
		String name = aDefineStr.substring(0,index).trim();
		NodeType define = nodeTypes.get(name);
		if(define != null ){
			log.warn("Duplicate node type definition:"+name+" Definition 1="+define.getDefineStr() + " Definition 2=" + aDefineStr);
			throw new RuntimeException("Duplicate node type definition:"+name+" Definition 1="+define.getDefineStr() + " Definition 2=" + aDefineStr);
		}
		define = new NodeType(this,name,aDefineStr);
		nodeTypes.put(name, define);
		return define;
	}
	/**
	 * Find node type based on type name
	 * @param name
	 * @return
	 */
	@Override
	public NodeType findNodeType(String name){		
		NodeType result = nodeTypes.get(name);
		if(result == null){
			throw new RuntimeException("Undefined node type：" + name);
		}
		while(result.getRealNodeType() != null){
			result = result.getRealNodeType();
		}
		return result;
	}
	
	/**
	 * Add keywords, but replace them with actual types, for example: "if"-"if"
	 * @param keyWordName
	 * @param realName
	 */
	public void addOperatorWithRealNodeType(String keyWordName, String realName){
		NodeType target =  this.createNodeType(keyWordName + ":TYPE=KEYWORD,REAL=" + realName);
		target.initial();
	}
	
	/**
	 * Add new operation symbols, their priority levels, and grammatical relations are consistent with the reference operation symbols
	 * @param operName
	 * @param refOperName
	 * @throws Exception 
	 */
	public void addOperatorWithLevelOfReference(String operName, String refOperName) throws Exception{
		NodeType target =  this.createNodeType(operName + ":TYPE=KEYWORD");
		target.initial();
		NodeType[] list = this.getNodeTypesByKind(NodeTypeKind.OPERATOR);
		NodeType refNodeType = this.findNodeType(refOperName);
		target.setInstructionFactory(refNodeType.getInstructionFactory());
		for(NodeType item:list){
			if(item.isContainerChild(refNodeType)){
				item.addChild(target);
				return;
			}
		}		
	}
	
	/**
	 * Determine whether there is a node type definition
	 * @param name
	 * @return
	 */
	public NodeType isExistNodeTypeDefine(String name){
		NodeType result = nodeTypes.get(name);
		if(result != null && result.getRealNodeType() != null){				
		  result = result.getRealNodeType();
		}
		return result;
	}

	public NodeType[] getNodeTypesByKind(NodeTypeKind aKind){
		List<NodeType> result  = new ArrayList<NodeType>();
		for(NodeType item :this.nodeTypes.values()){
			if(item.getKind() == aKind){
				result.add(item);
			}
		}
		return result.toArray(new NodeType[0]);
	}
	public boolean isFunction(String name){
		return this.functions.containsKey(name);
	}
	public void addFunctionName(String name){
		this.functions.put(name, name);
	}
}
