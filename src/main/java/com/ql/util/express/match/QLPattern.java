package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QLPattern {

	private static final Log log = LogFactory.getLog(QLPattern.class);
	
	public static QLPatternNode createPattern(INodeTypeManager nodeTypeManager,String name,String pattern) throws Exception{
		return new QLPatternNode(nodeTypeManager,name,pattern);		
	}
	public static QLMatchResult findMatchStatement(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		AtomicLong maxMatchPoint = new AtomicLong();
		QLMatchResult result = findMatchStatementWithAddRoot(aManager,pattern,nodes,point,true,maxMatchPoint);
		if(result == null || result.matchs.size() == 0){
			throw new Exception("程序错误，不满足语法规范，没有匹配到合适的语法,最大匹配致[0:" + (maxMatchPoint.longValue()-1) +"]");
		}else if(result != null && result.matchs.size() != 1){
			throw new Exception("程序错误，不满足语法规范，必须有一个根节点：" + pattern + ",最大匹配致[0:" + (maxMatchPoint.longValue()-1) +"]");
		}
		return result;
	}
	private  static QLMatchResult findMatchStatementWithAddRoot(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point,boolean isRoot,AtomicLong maxMatchPoint) throws Exception{
		QLMatchResult result = null;
		List<QLMatchResultTree> tempList = null;
		int count = 0;
		int lastPoint = point;
		while(true){
			QLMatchResult tempResult = null;
			if (pattern.matchMode == MatchMode.DETAIL) {
				tempResult = matchDetailOneTime(aManager,pattern,nodes, lastPoint,maxMatchPoint);
			}else if (pattern.matchMode == MatchMode.AND) {
				tempResult = matchAndOneTime(aManager,pattern,nodes, lastPoint,maxMatchPoint);
			}else if (pattern.matchMode == MatchMode.OR) {
				tempResult = matchOrOneTime(aManager,pattern,nodes, lastPoint,maxMatchPoint);
			}else{
				throw new Exception("不正确的类型：" + pattern.matchMode.toString());
			}
			if(tempResult == null){
				if(count >= pattern.minMatchNum && count <=pattern.maxMatchNum){
					//正确匹配
					if(tempList == null){
						 tempList = new ArrayList<QLMatchResultTree>();
					}
					result = new QLMatchResult(tempList,lastPoint);
				}else{
					result = null;
				}
				break;
			}else{
				if(tempList == null){
					 tempList = new ArrayList<QLMatchResultTree>();
				}
				lastPoint = tempResult.matchLastIndex;
				if(pattern.isTreeRoot == true){
					if(tempResult.matchs.size() > 1){
						throw new Exception("根节点的数量必须是1");
					}
					if(tempList.size() == 0){
						tempList.addAll(tempResult.matchs);
					}else{	
						tempResult.matchs.get(0).addLeftAll(tempList);
						tempList = tempResult.matchs;
					}
				}else{
				   tempList.addAll(tempResult.matchs);
				}
			}
			count = count + 1;			
			if(count == pattern.maxMatchNum){
				result = new QLMatchResult(tempList,lastPoint);
				break;
			}
		}
		if(result != null && pattern.isSkip == true){
			//忽略跳过所有匹配到的节点
			result.matchs.clear();
		}

		if(result != null && result.matchs.size() >0 && pattern.rootNodeType != null){
			QLMatchResultTree tempTree = new QLMatchResultTree(pattern.rootNodeType,nodes.get(0).createExpressNode(pattern.rootNodeType,null));
			tempTree.addLeftAll(result.matchs);
			result.matchs.clear();
			result.matchs.add(tempTree);
		}
		return result;
	}
	private  static QLMatchResult matchDetailOneTime(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point,AtomicLong maxMatchPoint) throws Exception{
		QLMatchResult result = null;
			if(pattern.nodeType == aManager.findNodeType("EOF") && point == nodes.size()){
				result = new QLMatchResult(new ArrayList<QLMatchResultTree>(), point + 1);
			}else if(pattern.nodeType == aManager.findNodeType("EOF") && point < nodes.size() && nodes.get(point).getValue().equals("}") ){
				result = new QLMatchResult(new ArrayList<QLMatchResultTree>(), point);
			}else if(point == nodes.size() && pattern.nodeType.getPatternNode() != null){
				result = findMatchStatementWithAddRoot(aManager,pattern.nodeType.getPatternNode(),nodes,point,false,maxMatchPoint);
			}else if( point < nodes.size()){
				INodeType tempNodeType = nodes.get(point).getTreeType();
				
				if(tempNodeType == null){
					tempNodeType = nodes.get(point).getNodeType();
				}
				if(tempNodeType != null && tempNodeType.equals(pattern.nodeType)==false){
					tempNodeType = null;
				}

				if(tempNodeType != null){
					List<QLMatchResultTree> tempList = new ArrayList<QLMatchResultTree>();
					tempList.add(new QLMatchResultTree(tempNodeType,nodes.get(point),pattern.targetNodeType));
					point = point + 1;
					result = new QLMatchResult(tempList, point);
					traceLog(pattern,result,nodes,point - 1,1);
				}else if(pattern.nodeType.getPatternNode() != null){
					result = findMatchStatementWithAddRoot(aManager,pattern.nodeType.getPatternNode(),nodes,point,false,maxMatchPoint);
					if(pattern.targetNodeType != null && result != null && result.matchs.size() >0){
						if(result.matchs.size() > 1){
							throw new Exception("设置了类型转换的语法，只能有一个根节点");
						}
						result.matchs.get(0).targetNodeType = pattern.targetNodeType;
					}
				}
				if(pattern.blame == true){//取返处理
					if( result == null){
						List<QLMatchResultTree> tempList = new ArrayList<QLMatchResultTree>();
						tempList.add(new QLMatchResultTree(tempNodeType,nodes.get(point),null));
						point = point + 1;
						result = new QLMatchResult(tempList, point);
					}else{
						result = null;
					}
				}
			}
		if(result != null && result.matchLastIndex > maxMatchPoint.longValue()){
			maxMatchPoint.set(result.matchLastIndex);
		}
		return result;

	}	

	private static QLMatchResult matchOrOneTime(INodeTypeManager aManager,
			QLPatternNode pattern, List<? extends IDataNode> nodes, int point,
			AtomicLong maxMatchPoint) throws Exception {
		QLMatchResult result = null;
		for (QLPatternNode item : pattern.children) {
			QLMatchResult tempResult = findMatchStatementWithAddRoot(aManager,item, nodes, point, false, maxMatchPoint);
			if (tempResult != null) {
				return tempResult;
			}
		}
		return result;
	}
	private  static QLMatchResult matchAndOneTime(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point,AtomicLong maxMatchPoint) throws Exception{
		int orgiPoint = point;
			QLMatchResultTree root = null;
			int matchCount =0;//用于调试日志的输出
    		List<QLMatchResultTree> tempList =null;
			for (QLPatternNode item : pattern.children) {
				if(point > nodes.size()){
					return null;
				}
				QLMatchResult tempResult = findMatchStatementWithAddRoot(aManager,item,nodes,
						point,false,maxMatchPoint);
				if (tempResult != null) {
					if(tempResult.matchs.size() > 0){
						matchCount = matchCount + 1;
					}
					if(tempList == null){
					   tempList = new ArrayList<QLMatchResultTree>();
					}
					point = tempResult.matchLastIndex;
					if (item.isTreeRoot == true && tempResult.matchs.size() >0) {
						if (tempResult.matchs.size() > 1)
							throw new Exception("根节点的数量必须是1");
						if (root == null) {
							QLMatchResultTree tempTree = tempResult.matchs.get(0);
							while(tempTree.getLeft()!= null && tempTree.getLeft().size()>0){
								tempTree = tempTree.getLeft().get(0);
							}
							tempTree.addLeftAll(tempList);
							tempList.clear();
						} else {
							tempResult.matchs.get(0).addLeft(root);
						}
						root = tempResult.matchs.get(0);
					} else if (root != null) {
						root.addRightAll(tempResult.matchs);
					} else {
						tempList.addAll(tempResult.matchs);
					}
				}else{
					return null;
				}
			}	
			if(root != null){
				tempList.add(root);
			}
			QLMatchResult result = new QLMatchResult(tempList,point);
			traceLog(pattern,result,nodes,orgiPoint,matchCount);
			return result;
	}

	public static void traceLog(QLPatternNode pattern, QLMatchResult result,
			List<? extends IDataNode> nodes, int point,int matchCount) {
		if (log.isTraceEnabled() && (pattern.matchMode ==MatchMode.DETAIL || pattern.matchMode == MatchMode.AND && matchCount > 1 && pattern.name.equals("ANONY_PATTERN") == false )) {
			log.trace("匹配--" + pattern.name +"[" + point  + ":" + (result.matchLastIndex -1)+ "]:" + pattern);
		}
	}
}


