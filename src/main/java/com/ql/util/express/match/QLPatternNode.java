package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.exception.QLCompileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

enum MatchMode{
	AND,OR,DETAIL,NULL
}
public class QLPatternNode{
	private static final Log log = LogFactory.getLog(QLPatternNode.class);
	INodeTypeManager nodeTypeManager;
	
	String name;
	
	/**
	 * Original string
	 */
	String orgiContent;
	/**
	 * Match mode
	 */
	MatchMode matchMode =MatchMode.NULL ;
	/**
	 * Is a submatch pattern
	 */
	boolean isChildMode = false;
	/**
	 * level
	 */
	int level =0;
	/**
	 * Whether the root node, for example: if^
	 */
	protected boolean isTreeRoot =false;
	
	/**
	 * Minimum number of matches, 0..n
	 */
	protected int minMatchNum =1;
	
	/**
	 * Maximum number of matches
	 */
	protected int maxMatchNum =1;
	
	
	/**
	 * Match type, such as ID,if,SELECT
	 */
	protected INodeType nodeType;
	
	/**
	 * The type that the matched node needs to be converted into, such as ID-"CONST_STRING
	 */
	protected INodeType targetNodeType;
	
	/**
	 * The virtual type to be converted to, for example:(ID$(,$ID)*)#COL_LIST
	 */
	protected INodeType rootNodeType;
	
	/**
	 * Whether the match is successful, but ignored in the output, expressed with "~"
	 * CONST$(,~$CONST)*
	 */
	protected boolean isSkip =false;
	
	/**
	 * Negative, for example: +@, matches all characters that are not +
	 */
	protected boolean blame = false;

	public boolean canMergeDetail(){
		if (QLPattern.optimizeStackDepth && this.matchMode == MatchMode.DETAIL && this.name.equals("ANONY_PATTERN")
				&& this.nodeType.getPatternNode() != null
				&& this.isSkip == false
				&& this.blame ==false
				&& this.isChildMode == false
				&& this.isTreeRoot == false
				&& this.minMatchNum ==1
				&& this.maxMatchNum == 1){
			return true;
		}
		return  false;
	}
	
	/**
	 * Submatch
	 */
	List<QLPatternNode> children = new ArrayList<QLPatternNode>();
	
	protected QLPatternNode(INodeTypeManager aManager,String aName,String aOrgiContent) throws Exception{
		this(aManager,aName,aOrgiContent,false,1);
//		if(this.toString().equals(aOrgiContent)==false){
				//throw new QLCompileException("The parsed result of the grammar definition is inconsistent with the original value, original value: "+ aOrgiContent + "parsed result:" + this.toString());
			//log.error(("The parsed result of the grammar definition is inconsistent with the original value, original value: "+ aOrgiContent + "parsed result:" + this.toString()));
//		}
	}
	protected QLPatternNode(INodeTypeManager aManager,String aName,String aOrgiContent,boolean aIsChildMode,int aLevel) throws Exception{
		this.nodeTypeManager = aManager;
		this.name = aName;
		this.orgiContent = aOrgiContent;
		this.isChildMode = aIsChildMode;
		this.level = aLevel;
		this.splitChild();
		for (int i=0;i< children.size();i++){
			QLPatternNode t = children.get(i);
			if(t.canMergeDetail()) {
				this.children.set(i,t.getNodeType().getPatternNode());
				if(t.getNodeType().getPatternNode().getNodeType() == null){
					t.getNodeType().getPatternNode().nodeType = t.getNodeType();
				}
			}
		}

	}
	public void splitChild() throws Exception{
		if(log.isTraceEnabled()){
			String str ="";
			for(int i=0;i<this.level;i++){
				str = str + "  ";
			}
			//log.trace("Resolve matching mode [LEVEL="+ this.level +"]START:" + str + this.orgiContent);
		}
		String orgStr = this.orgiContent;
		if(orgStr.equals("(") || orgStr.equals(")") || orgStr.equals("|")||orgStr.equals("||")||orgStr.equals("/**") || orgStr.equals("**/")||orgStr.equals("*")){
			this.matchMode = MatchMode.DETAIL;
			this.nodeType = this.nodeTypeManager.findNodeType(orgStr);
			return ;
		}
		
		String tempStr ="";
		int count =0;
		for(int i=0;i<orgStr.length();i++){
			if (orgStr.charAt(i) == '(') {
				tempStr = tempStr + orgStr.charAt(i);
				count = count + 1;
			}else if(orgStr.charAt(i) == ')'){
				tempStr = tempStr + orgStr.charAt(i);
				count = count - 1;
			}else if(count > 0){
				tempStr = tempStr + orgStr.charAt(i);
			}else if(orgStr.charAt(i) == '$'){
				if (this.matchMode != MatchMode.NULL
						&& this.matchMode != MatchMode.AND) {
					throw new QLCompileException("Incorrect pattern string, can not coexist | and $ in a matching pattern, please use string pattern:"
							+ orgStr);
				}
				children.add(new QLPatternNode(this.nodeTypeManager,"ANONY_PATTERN",tempStr, false,this.level + 1));
				this.matchMode = MatchMode.AND;
				tempStr = "";
			}else if(orgStr.charAt(i) == '|'){
					if (this.matchMode != MatchMode.NULL
							&& this.matchMode != MatchMode.OR) {
						throw new QLCompileException("Incorrect pattern string, can not coexist | and $ in a matching pattern, please use string pattern:"
								+ orgStr);
					}
					children.add(new QLPatternNode(this.nodeTypeManager,"ANONY_PATTERN",tempStr, false,this.level + 1));
					this.matchMode = MatchMode.OR;
					tempStr = "";
			}else if(orgStr.charAt(i) == '#'){
					this.rootNodeType = this.nodeTypeManager.findNodeType(orgStr.substring(i+1));
					break;
			}else {
				tempStr = tempStr + orgStr.charAt(i);
			}
		}
		// Process content without ()
		if (count > 0) {
			throw new QLCompileException("Incorrect pattern string, (the corresponding one was not found):" + orgStr);
		}
        
		if(this.children.size() > 0){
			children.add(new QLPatternNode(this.nodeTypeManager,"ANONY_PATTERN",tempStr, false,this.level + 1));
			tempStr ="";
		}
		
		//Need to eliminate multiplication *
		if(tempStr.endsWith("*") && tempStr.length() >1){
	    	this.minMatchNum = 0;
	    	this.maxMatchNum = Integer.MAX_VALUE;
	    	tempStr = tempStr.substring(0,tempStr.length() -1);
		}
		
    	if(tempStr.endsWith("}")){
    		int index = tempStr.lastIndexOf("{");
    		if(index > 0){
				String numStr = tempStr.substring(index + 1,tempStr.length() - 1);
				int index2 = numStr.indexOf(':');
				if (index2 > 0) {
					this.minMatchNum = Integer.parseInt(numStr.substring(0, index2));
					this.maxMatchNum = Integer.parseInt(numStr.substring(index2 + 1));
				} else {
					this.minMatchNum = Integer.parseInt(numStr);
					this.maxMatchNum = Integer.parseInt(numStr);
				}
				tempStr = tempStr.substring(0,index);
    		}
    	}
    	if(tempStr.endsWith("^")==true && tempStr.length() > 1){
			this.isTreeRoot = true;
			tempStr = tempStr.substring(0,tempStr.length() -1);
    	}


		if(tempStr.endsWith("~") && tempStr.length() >1){
	    	this.isSkip = true;
	    	tempStr = tempStr.substring(0,tempStr.length() -1);
		}
		if(tempStr.endsWith("@") && tempStr.length() >1){
	    	this.blame = true;
	    	tempStr = tempStr.substring(0,tempStr.length() -1);
		}
    	
    	//Processing (ABC|bcd) mode
    	if(tempStr.length() > 2 && tempStr.charAt(0)=='(' && tempStr.charAt(tempStr.length() - 1) ==')'){
    		this.isChildMode = true;
    		this.children.add(new QLPatternNode(this.nodeTypeManager,"ANONY_PATTERN",tempStr.substring(1, tempStr.length() - 1), false,this.level + 1));
    		this.matchMode = MatchMode.AND;
    		tempStr = "";
    		
    	}
    	
		int index = tempStr.indexOf("->");
		if (index > 0) {
			this.targetNodeType = this.nodeTypeManager.findNodeType(tempStr.substring(index + 2));
			tempStr = tempStr.substring(0, index);
		}
		if (tempStr.length() > 0) {
			this.matchMode = MatchMode.DETAIL;
			this.nodeType = this.nodeTypeManager.findNodeType(tempStr);
		}
	}	
    public List<QLPatternNode> getChildren(){
    	return this.children;
    }
    public INodeType getNodeType(){
    	return this.nodeType;
    }
	
	public boolean  isDetailMode(){
		return this.matchMode == MatchMode.DETAIL;
	}
	public boolean  isAndMode(){
		return this.matchMode == MatchMode.AND;
	}
	public String toString(){
		String result ="";
		if(this.matchMode == MatchMode.AND){
			result = this.joinStringList(this.children,"$");
		}else if(this.matchMode ==MatchMode.OR){
			result = this.joinStringList(this.children,"|");
		}else{
			result = this.nodeType.getName();
		}
		if(this.targetNodeType != null){
			result = result +"->" + this.targetNodeType.getName();
		}
		if(this.isChildMode == true){
			result ="("+ result + ")";
		}		
		if(this.isSkip){
			result = result +'~';	
		}
		if(this.blame){
			result = result +'@';	
		}
		if(this.isTreeRoot){
			result = result +'^';	
		}
		if(this.minMatchNum == 0 && this.maxMatchNum == Integer.MAX_VALUE){
			result = result +'*';
		}else if(this.minMatchNum == this.maxMatchNum && this.maxMatchNum > 1) {
			result = result + "{" + this.maxMatchNum +"}";
		}else if(this.minMatchNum != this.maxMatchNum){
			result = result + "{" + this.minMatchNum +":" + this.maxMatchNum +"}";
		}
		
		if(this.rootNodeType != null){
			result = result + '#'+ this.rootNodeType.getName();
		}
		return result;
	}
	public String joinStringList(List<QLPatternNode> list,String splitChar){
		StringBuffer buffer = new StringBuffer();
		for(int i=0;i<list.size();i++){
			if(i>0){buffer.append(splitChar);}
			buffer.append(list.get(i));
		}
		return buffer.toString();
	}
}

