package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicLong;

import com.ql.util.express.exception.QLCompileException;
import com.ql.util.express.parse.ExpressNode;
import com.ql.util.express.parse.NodeType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QLPattern {

	private static final Log log = LogFactory.getLog(QLPattern.class);
    
    public static boolean optimizeStackDepth = true;//优化栈的递归深度
	public static boolean printStackDepth = false;//打印栈的最大深度

	public static QLPatternNode createPattern(INodeTypeManager nodeTypeManager,String name,String pattern) throws Exception{
		return new QLPatternNode(nodeTypeManager,name,pattern);		
	}
	public static QLMatchResult findMatchStatement(INodeTypeManager aManager,QLPatternNode pattern ,List<? extends IDataNode> nodes,int point) throws Exception{
		AtomicLong maxMatchPoint = new AtomicLong();
		QLMatchResultCache resultCache =new QLMatchResultCache(5);
		ArrayListCache arrayListCache = new ArrayListCache(50);
        MatchParamsPack staticParams = new MatchParamsPack(aManager, nodes, maxMatchPoint,resultCache,arrayListCache);
		QLMatchResult result  = findMatchStatementWithTailRecursive(staticParams, pattern, null, point, 0, null, null, null);
		if(printStackDepth) {
            log.warn("重用QLMatchResult次数:" + resultCache.fetchCount
					+ "  新建QLMatchResult次数:" + resultCache.newCount + "  新建ArrayList数量:" + arrayListCache.newCount);

        }
		if(result == null || result.getMatchSize() == 0){
			throw new QLCompileException("程序错误，不满足语法规范，没有匹配到合适的语法,最大匹配致[0:" + (maxMatchPoint.longValue()-1) +"]");
		}else if(result != null && result.getMatchSize() != 1){
			throw new QLCompileException("程序错误，不满足语法规范，必须有一个根节点：" + pattern + ",最大匹配致[0:" + (maxMatchPoint.longValue()-1) +"]");
		}
		return result;
	}
	private  static QLMatchResult findMatchStatementWithTailRecursive(MatchParamsPack staticParams, QLPatternNode pattern, QLPatternNode parentPattern,
																	  int point, int count, List<QLMatchResultTree> tempList, QLMatchResult tempResult, ReservedVariable reservedVariable) throws Exception{
        
        INodeTypeManager aManager = staticParams.aManager;
        List<? extends IDataNode> nodes = staticParams.nodes;

		tailRecursive:
		// 尾递归优化
		while (true) {
			if (reservedVariable != null) {
				pattern = reservedVariable.pattern;
				parentPattern = reservedVariable.parentPattern;
				point = reservedVariable.point;
				count = reservedVariable.count+1;
				tempList = reservedVariable.tempList;
				if (reservedVariable instanceof OrReservedVariable) {
					OrReservedVariable orReservedVariable = (OrReservedVariable)
							reservedVariable;
					if (tempResult == null && orReservedVariable.children.hasNext()) {
						QLPatternNode child = orReservedVariable.children.next();
						staticParams.recursiveStack.push(orReservedVariable);
						QLPatternNode oldPattern = pattern;

						pattern = child;
						parentPattern = oldPattern;
						count = 0;
						tempList = null;
						tempResult = null;
						reservedVariable = null;
						continue tailRecursive;
					}
				} else if (reservedVariable instanceof AndReservedVariable) {
					AndReservedVariable andReservedVariable = (AndReservedVariable)
							reservedVariable;
					int pointAnd = andReservedVariable.pointAnd;
					QLMatchResultTree root = andReservedVariable.root;
					List<QLMatchResultTree> tempListAnd = andReservedVariable
							.tempListAnd;
					QLMatchResult tempResultAnd = tempResult;
					Iterator<QLPatternNode> children = andReservedVariable.children;
					QLPatternNode lastChild = andReservedVariable.child;

					boolean notMatch = tempResultAnd == null ||
							(children.hasNext() && pointAnd > nodes.size());
					if (!notMatch) {
						if(tempListAnd == null){
							tempListAnd = staticParams.arrayListCache.fetch();
						}
						pointAnd = tempResultAnd.getMatchLastIndex();
						if (lastChild.isTreeRoot && tempResultAnd.getMatchSize() > 0) {
							if (tempResultAnd.getMatchSize() > 1) {
								throw new QLCompileException("根节点的数量必须是1");
							}
							if (root == null) {
								QLMatchResultTree tempTree = tempResultAnd.getMatchs().get(0);
								while(tempTree.getLeft()!= null && tempTree.getLeft().size()>0){
									tempTree = tempTree.getLeft().get(0);
								}
								tempTree.addLeftAll(tempListAnd);
								tempListAnd.clear();
							} else {
								tempResultAnd.getMatchs().get(0).addLeft(root);
							}
							root = tempResultAnd.getMatchs().get(0);
						} else if (root != null) {
							root.addRightAll(tempResultAnd.getMatchs());
						} else {
							tempListAnd.addAll(tempResultAnd.getMatchs());
						}
						//归还QLMatchResult对象到对象池
						staticParams.resultCache.sendBack(tempResultAnd);
						if (children.hasNext()) {
							QLPatternNode child = children.next();
							andReservedVariable.child = child;
							andReservedVariable.pointAnd = pointAnd;
							andReservedVariable.root = root;
							andReservedVariable.tempListAnd = tempListAnd;
							staticParams.recursiveStack.push(andReservedVariable);
							QLPatternNode oldPattern = pattern;

							// 继续递归
							pattern = child;
							parentPattern = oldPattern;
							point = pointAnd;
							count = 0;
							tempList = null;
							tempResult = null;
							reservedVariable = null;
							continue tailRecursive;
						}
					}

					// 聚合 and 匹配的结果
					if(root != null){
						tempListAnd.add(root);
					}
					if (notMatch) {
						tempResult = null;
					} else {
						tempResult = staticParams.resultCache.fetch()
								.addQLMatchResultTreeList(tempListAnd);
						tempResult.setMatchLastIndex(pointAnd);
						traceLog(pattern,tempResult,nodes,point);
					}

					if (tempListAnd != null){
						staticParams.arrayListCache.sendBack(tempListAnd);
					}
				}
			}

			if (count > 0 && tempResult != null) {
				if(tempList == null){
					tempList = staticParams.arrayListCache.fetch();
				}
				point = tempResult.getMatchLastIndex();
				if(pattern.isTreeRoot){
					if (tempResult.getMatchSize() > 1){
						throw new QLCompileException("根节点的数量必须是1");
					}
					if (tempList.size() > 0) {
						tempResult.getMatchs().get(0).addLeftAll(tempList);
						//为了能回收QLMatchResult对象,这个地方必须进行数组拷贝
						tempList = staticParams.arrayListCache.fetch();
					}
				}

				// 为了能回收QLMatchResult对象,这个地方必须进行数组拷贝
				// 在这之前 tempList 一定是个空列表
				tempList.addAll(tempResult.getMatchs());

				/*  归还QLMatchResult  */
				staticParams.resultCache.sendBack(tempResult);
			}

			boolean lastUnmatch = count > 0 && tempResult == null;
			int parentPoint = point;
			if (count < pattern.maxMatchNum && !lastUnmatch) {

				tempResult = null;
				if (pattern.matchMode == MatchMode.DETAIL) {
					QLMatchResult resultDetail = null;
					if(pattern.nodeType == aManager.findNodeType("EOF") && point == nodes.size()){
						resultDetail = staticParams.resultCache.fetch().setMatchLastIndex(point + 1);
					}else if(pattern.nodeType == aManager.findNodeType("EOF") && point < nodes.size() && nodes.get(point).getValue().equals("}") ){
						resultDetail = staticParams.resultCache.fetch().setMatchLastIndex(point);
					}else if(point == nodes.size() && pattern.nodeType.getPatternNode() != null){
						QLPatternNode oldPattern = pattern;

						pushToStackDetail(staticParams.recursiveStack, pattern, parentPattern, point, count, tempList);
						pattern = pattern.nodeType.getPatternNode();
						parentPattern = oldPattern;
						count = 0;
						tempList = null;
						tempResult = null;
						reservedVariable = null;
						continue tailRecursive;
					} else if(point < nodes.size()) {
						INodeType tempNodeType = null;
						if(pattern.nodeType.equals(nodes.get(point).getTreeType())){
							tempNodeType = nodes.get(point).getTreeType();
						}else if(pattern.nodeType.equals(nodes.get(point).getNodeType())){
							tempNodeType = nodes.get(point).getNodeType();
						}

						if(tempNodeType != null){
							resultDetail = staticParams.resultCache.fetch();
							resultDetail.addQLMatchResultTree(new QLMatchResultTree(tempNodeType,nodes.get(point),pattern.targetNodeType));
							resultDetail.setMatchLastIndex(point+1);

							traceLog(pattern,resultDetail,nodes,point);
						}else if(pattern.nodeType.getPatternNode() != null){
							QLPatternNode oldPattern = pattern;

							pushToStackDetail(staticParams.recursiveStack, pattern, parentPattern, point, count, tempList);
							pattern = pattern.nodeType.getPatternNode();
							parentPattern = oldPattern;
							count = 0;
							tempList = null;
							tempResult = null;
							reservedVariable = null;
							continue tailRecursive;
						}
					}

					tempResult = resultDetail;
				}else if (pattern.matchMode == MatchMode.AND) {

					Iterator<QLPatternNode> children = pattern.children.iterator();
					if (children.hasNext()) {
						QLPatternNode item = children.next();
						QLPatternNode oldPattern = pattern;

						pushToStackAnd(staticParams.recursiveStack, pattern, parentPattern, point,
								count, tempList, children, item,
								point, null, null);
						pattern = item;
						parentPattern = oldPattern;
						count = 0;
						tempList = null;
						tempResult = null;
						reservedVariable = null;
						continue tailRecursive;
					}

				} else if (pattern.matchMode == MatchMode.OR) {

					Iterator<QLPatternNode> children = pattern.children.iterator();
					if (children.hasNext()) {
						QLPatternNode oldPattern = pattern;

						pushToStackOr(staticParams.recursiveStack, pattern, parentPattern, point, count, tempList, children);
						pattern = children.next();
						parentPattern = oldPattern;
						count = 0;
						tempList = null;
						tempResult = null;
						reservedVariable = null;
						continue tailRecursive;
					}

				} else {
					throw new QLCompileException("不正确的类型：" + pattern.matchMode.toString());
				}

				count = count + 1;
				reservedVariable = null;
				continue tailRecursive;
			}
			count = lastUnmatch? count - 1: count;
			QLMatchResult result;
			if (count >= pattern.minMatchNum && count <= pattern.maxMatchNum) {
				//正确匹配
				result = staticParams.resultCache.fetch();
				if (tempList != null) {
					result.addQLMatchResultTreeList(tempList);
				}
				result.setMatchLastIndex(point);
			} else {
				result = null;
			}

			if (tempList != null) {
				staticParams.arrayListCache.sendBack(tempList);
			}

			result = handleResult(staticParams, result, pattern, parentPattern, parentPoint);
			if (staticParams.recursiveStack.isEmpty()) {
				return result;
			}

			reservedVariable = staticParams.recursiveStack.pop();
			tempResult = result;
			continue tailRecursive;
		}
	}

	private static QLMatchResult handleResultNoParent(MatchParamsPack staticParams, QLMatchResult originResult, QLPatternNode pattern) throws Exception {
		if(originResult != null && pattern.isSkip){
			//忽略跳过所有匹配到的节点
			originResult.getMatchs().clear();
		}

		if(originResult != null && originResult.getMatchSize() >0 && pattern.rootNodeType != null){
			QLMatchResultTree tempTree = new QLMatchResultTree(pattern.rootNodeType,
					new ExpressNode((NodeType) pattern.rootNodeType,null));
			tempTree.addLeftAll(originResult.getMatchs());
			originResult.getMatchs().clear();
			originResult.getMatchs().add(tempTree);
		}

		return originResult;
	}

	private static QLMatchResult handleResult(MatchParamsPack staticParams, QLMatchResult originResult, QLPatternNode pattern, QLPatternNode parentPattern, int parentPoint) throws Exception {
		originResult = handleResultNoParent(staticParams, originResult, pattern);

		if (parentPattern != null) {
			if (parentPattern.matchMode == MatchMode.DETAIL) {
				if (parentPoint < staticParams.nodes.size()) {
					if(parentPattern.targetNodeType != null && originResult != null && originResult.getMatchSize() >0){
						if(originResult.getMatchSize() > 1){
							throw new QLCompileException("设置了类型转换的语法，只能有一个根节点");
						}
						originResult.getMatchs().get(0).targetNodeType = parentPattern.targetNodeType;
					}
					if(parentPattern.blame){//取反处理
						if(originResult == null){
							originResult = staticParams.resultCache.fetch();
							originResult.addQLMatchResultTree(new QLMatchResultTree(null,staticParams.nodes.get(parentPoint),null));
							originResult.setMatchLastIndex(parentPoint + 1);
						}else{
							originResult = null;
						}
					}
				}
				if(originResult != null && originResult.getMatchLastIndex() > staticParams.maxMatchPoint.longValue()){
					staticParams.maxMatchPoint.set(originResult.getMatchLastIndex());
				}
			}
		}

		return originResult;
	}

	public static void traceLog(QLPatternNode pattern, QLMatchResult result,
			List<? extends IDataNode> nodes, int point) {
		if (log.isTraceEnabled() && (pattern.matchMode ==MatchMode.DETAIL || pattern.matchMode == MatchMode.AND && !pattern.name.equals("ANONY_PATTERN") )) {
			log.trace("匹配--" + pattern.name +"[" + point  + ":" + (result.getMatchLastIndex() -1)+ "]:" + pattern);
		}
	}

	private static void pushToStackDetail(Stack<ReservedVariable> recursiveStack, QLPatternNode pattern, QLPatternNode parentPattern, int point, int count, List<QLMatchResultTree> tempList) {
		DetailReservedVariable detailReservedVariable = new DetailReservedVariable();
		detailReservedVariable.pattern = pattern;
		detailReservedVariable.parentPattern = parentPattern;
		detailReservedVariable.point = point;
		detailReservedVariable.count = count;
		detailReservedVariable.tempList = tempList;
		recursiveStack.push(detailReservedVariable);
	}

	private static void pushToStackOr(Stack<ReservedVariable> recursiveStack, QLPatternNode pattern, QLPatternNode parentPattern, int point, int count, List<QLMatchResultTree> tempList, Iterator<QLPatternNode> children) {
		OrReservedVariable orReservedVariable = new OrReservedVariable();
		orReservedVariable.pattern = pattern;
		orReservedVariable.parentPattern = parentPattern;
		orReservedVariable.point = point;
		orReservedVariable.count = count;
		orReservedVariable.tempList = tempList;
		orReservedVariable.children = children;
		recursiveStack.push(orReservedVariable);
	}

	private static void pushToStackAnd(Stack<ReservedVariable> recursiveStack, QLPatternNode pattern, QLPatternNode parentPattern, int point, int count, List<QLMatchResultTree> tempList, Iterator<QLPatternNode> children, QLPatternNode child, int pointAnd, QLMatchResultTree root, List<QLMatchResultTree> tempListAnd) {
		AndReservedVariable andReservedVariable = new AndReservedVariable();
		andReservedVariable.pattern = pattern;
		andReservedVariable.parentPattern = parentPattern;
		andReservedVariable.point = point;
		andReservedVariable.count = count;
		andReservedVariable.tempList = tempList;
		andReservedVariable.children = children;
		andReservedVariable.child = child;
		andReservedVariable.pointAnd = pointAnd;
		andReservedVariable.root = root;
		andReservedVariable.tempListAnd = tempListAnd;
		recursiveStack.push(andReservedVariable);
	}

	private static class ReservedVariable {
		QLPatternNode pattern;
		QLPatternNode parentPattern;
		int point;
		int count;
		List<QLMatchResultTree> tempList;
	}

	private static class DetailReservedVariable extends ReservedVariable {
	}

	private static class OrReservedVariable extends ReservedVariable {
		Iterator<QLPatternNode> children;
	}

	private static class AndReservedVariable extends ReservedVariable {
		Iterator<QLPatternNode> children;
		QLPatternNode child;
		int pointAnd;
		QLMatchResultTree root;
		List<QLMatchResultTree> tempListAnd;
	}

	public static class MatchParamsPack
    {
        INodeTypeManager aManager;
        List<? extends IDataNode> nodes;
        AtomicLong maxMatchPoint;
		QLMatchResultCache resultCache;
		ArrayListCache arrayListCache;
		Stack<ReservedVariable> recursiveStack = new Stack<ReservedVariable>();

        public MatchParamsPack(INodeTypeManager aManager, List<? extends IDataNode> nodes, AtomicLong maxMatchPoint,QLMatchResultCache aResultCache,ArrayListCache aArrayListCache) {
            this.aManager = aManager;
            this.nodes = nodes;
            this.maxMatchPoint = maxMatchPoint;
			this.resultCache = aResultCache;
			this.arrayListCache = aArrayListCache;
        }
    }

    public static abstract class BaseCache<T> {
		public  int newCount =0;
		public  int fetchCount =0;

		private T[] cache ;
		private int len;
		private int point;

		public BaseCache(int aLen) {
			this.len = aLen;
			this.point = this.len -1;
			cache = newCacheArray(this.len);
			for (int i = 0; i < this.len; i++) {
				cache[i] = newInstance();
			}
		}

		public T fetch() {
			T result = null;
			if (point >=0) {
				result = cache[point];
				cache[point] = null;
				point = point - 1;
				fetchCount++;
			} else {
				result = newInstance();
				newCount++;
			}
			return result;
		}

		public void sendBack(T result){
			if (this.point <this.len -1){
				this.point = this.point + 1;
				cache[this.point] = result;
				clear(cache[this.point]);
			}
		}

		abstract T[] newCacheArray(int num);
		abstract T newInstance();
		abstract void clear(T sendBackObj);
	}

	public static class QLMatchResultCache extends BaseCache<QLMatchResult> {

		public QLMatchResultCache(int aLen) {
			super(aLen);
		}

		@Override
		QLMatchResult[] newCacheArray(int num) {
			return new QLMatchResult[num];
		}

		@Override
		QLMatchResult newInstance() {
			return new QLMatchResult();
		}

		@Override
		void clear(QLMatchResult sendBackObj) {
			sendBackObj.clear();
		}
	}

	public static class ArrayListCache extends BaseCache<List> {

		public ArrayListCache(int aLen) {
			super(aLen);
		}

		@Override
		List[] newCacheArray(int num) {
			return new List[num];
		}

		@Override
		List newInstance() {
			return new ArrayList();
		}

		@Override
		void clear(List sendBackObj) {
			sendBackObj.clear();
		}
	}
}


