package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.ql.util.express.exception.QLCompileException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QLPattern {
    private static final Log log = LogFactory.getLog(QLPattern.class);

    /**
     * 优化栈的递归深度
     * TODO public field
     */
    public static boolean optimizeStackDepth = true;

    /**
     * 打印栈的最大深度
     * TODO public field
     */
    public static boolean printStackDepth = false;

    public static QLPatternNode createPattern(INodeTypeManager nodeTypeManager, String name, String pattern)
        throws Exception {
        return new QLPatternNode(nodeTypeManager, name, pattern);
    }

    public static QLMatchResult findMatchStatement(INodeTypeManager iNodeTypeManager, QLPatternNode pattern,
        List<? extends IDataNode> nodes, int point) throws Exception {
        AtomicLong maxMatchPoint = new AtomicLong();
        AtomicLong maxDeep = new AtomicLong(1);
        QLMatchResultCache resultCache = new QLMatchResultCache(5);
        ArrayListCache<QLMatchResultTree> arrayListCache = new ArrayListCache<>(50);
        MatchParamsPack staticParams = new MatchParamsPack(iNodeTypeManager, nodes, maxDeep, maxMatchPoint, resultCache,
            arrayListCache);
        QLMatchResult result = findMatchStatementWithAddRootOptimizeStack(staticParams, pattern, point, true, 1);
        if (printStackDepth) {
            log.warn("递归堆栈深度:" + maxDeep.longValue() + "  重用QLMatchResult次数:" + resultCache.fetchCount
                + "  新建QLMatchResult次数:" + resultCache.newCount + "  新建ArrayList数量:" + arrayListCache.newCount);
        }
        if (result == null || result.getMatchSize() == 0) {
            throw new QLCompileException("程序错误，不满足语法规范，没有匹配到合适的语法,最大匹配致[0:" + (maxMatchPoint.longValue() - 1) + "]");
        } else if (result != null && result.getMatchSize() != 1) {
            throw new QLCompileException(
                "程序错误，不满足语法规范，必须有一个根节点：" + pattern + ",最大匹配致[0:" + (maxMatchPoint.longValue() - 1) + "]");
        }
        return result;
    }

    private static QLMatchResult findMatchStatementWithAddRootOptimizeStack(MatchParamsPack staticParams,
        QLPatternNode pattern, int point, boolean isRoot, int deep) throws Exception {

        INodeTypeManager iNodeTypeManager = staticParams.iNodeTypeManager;
        List<? extends IDataNode> nodes = staticParams.nodes;
        AtomicLong maxMatchPoint = staticParams.maxMatchPoint;
        AtomicLong maxDeep = staticParams.maxDeep;

        //mark maxDeep
        deep++;
        if (deep > maxDeep.longValue()) {
            maxDeep.set(deep);
        }

        QLMatchResult result;
        List<QLMatchResultTree> tempList = null;
        int count = 0;
        int lastPoint = point;
        while (true) {
            QLMatchResult tempResult = null;
            if (pattern.matchMode == MatchMode.DETAIL) {
                //tempResult = matchDetailOneTime(iNodeTypeManager,pattern,nodes, lastPoint,maxMatchPoint,deep,maxDeep);

                int pointDetail = lastPoint;
                QLMatchResult resultDetail = null;
                if (pattern.nodeType == iNodeTypeManager.findNodeType("EOF") && pointDetail == nodes.size()) {
                    resultDetail = staticParams.resultCache.fetch().setMatchLastIndex(pointDetail + 1);
                } else if (pattern.nodeType == iNodeTypeManager.findNodeType("EOF") && pointDetail < nodes.size() && "}".equals(
                    nodes.get(
                        pointDetail).getValue())) {
                    resultDetail = staticParams.resultCache.fetch().setMatchLastIndex(pointDetail);
                } else if (pointDetail == nodes.size() && pattern.nodeType.getPatternNode() != null) {
                    resultDetail = findMatchStatementWithAddRootOptimizeStack(staticParams,
                        pattern.nodeType.getPatternNode(), pointDetail, false, deep);
                } else if (pointDetail < nodes.size()) {
                    INodeType tempNodeType = null;
                    if (pattern.nodeType.equals(nodes.get(pointDetail).getTreeType())) {
                        tempNodeType = nodes.get(pointDetail).getTreeType();
                    } else if (pattern.nodeType.equals(nodes.get(pointDetail).getNodeType())) {
                        tempNodeType = nodes.get(pointDetail).getNodeType();
                    }

                    if (tempNodeType != null) {
                        resultDetail = staticParams.resultCache.fetch();
                        resultDetail.addQLMatchResultTree(
                            new QLMatchResultTree(tempNodeType, nodes.get(pointDetail), pattern.targetNodeType));
                        pointDetail = pointDetail + 1;
                        resultDetail.setMatchLastIndex(pointDetail);

                        traceLog(pattern, resultDetail, nodes, pointDetail - 1, 1);
                    } else if (pattern.nodeType.getPatternNode() != null) {
                        resultDetail = findMatchStatementWithAddRootOptimizeStack(staticParams,
                            pattern.nodeType.getPatternNode(), pointDetail, false, deep);
                        if (pattern.targetNodeType != null && resultDetail != null && resultDetail.getMatchSize() > 0) {
                            if (resultDetail.getMatchSize() > 1) {
                                throw new QLCompileException("设置了类型转换的语法，只能有一个根节点");
                            }
                            resultDetail.getMatches().get(0).setTargetNodeType(pattern.targetNodeType);
                        }
                    }

                    //取返处理
                    if (pattern.blame) {
                        if (resultDetail == null) {
                            resultDetail = staticParams.resultCache.fetch();
                            resultDetail.addQLMatchResultTree(
                                new QLMatchResultTree(tempNodeType, nodes.get(pointDetail), null));
                            pointDetail = pointDetail + 1;
                            resultDetail.setMatchLastIndex(pointDetail);
                        } else {
                            resultDetail = null;
                        }
                    }
                }
                if (resultDetail != null && resultDetail.getMatchLastIndex() > maxMatchPoint.longValue()) {
                    maxMatchPoint.set(resultDetail.getMatchLastIndex());
                }

                tempResult = resultDetail;

            } else if (pattern.matchMode == MatchMode.AND) {
                //tempResult = matchAndOneTime(iNodeTypeManager,pattern,nodes, lastPoint,maxMatchPoint,deep,maxDeep);

                int pointAnd = lastPoint;

                QLMatchResultTree root = null;
                //用于调试日志的输出
                int matchCount = 0;
                List<QLMatchResultTree> tempListAnd = null;
                boolean isBreak = false;
                for (QLPatternNode item : pattern.getChildren()) {
                    if (pointAnd > nodes.size()) {
                        isBreak = true;
                        break;
                    }
                    QLMatchResult tempResultAnd = findMatchStatementWithAddRootOptimizeStack(staticParams, item,
                        pointAnd, false, deep);
                    if (tempResultAnd != null) {
                        if (tempResultAnd.getMatchSize() > 0) {
                            matchCount = matchCount + 1;
                        }
                        if (tempListAnd == null) {
                            tempListAnd = staticParams.arrayListCache.fetch();
                        }
                        pointAnd = tempResultAnd.getMatchLastIndex();
                        if (item.isTreeRoot && tempResultAnd.getMatchSize() > 0) {
                            if (tempResultAnd.getMatchSize() > 1) {
                                throw new QLCompileException("根节点的数量必须是1");
                            }
                            if (root == null) {
                                QLMatchResultTree tempTree = tempResultAnd.getMatches().get(0);
                                while (tempTree.getLeft() != null && tempTree.getLeft().size() > 0) {
                                    tempTree = tempTree.getLeft().get(0);
                                }
                                tempTree.addLeftAll(tempListAnd);
                                tempListAnd.clear();
                            } else {
                                tempResultAnd.getMatches().get(0).addLeft(root);
                            }
                            root = tempResultAnd.getMatches().get(0);
                        } else if (root != null) {
                            root.addRightAll(tempResultAnd.getMatches());
                        } else {
                            tempListAnd.addAll(tempResultAnd.getMatches());
                        }
                        //归还QLMatchResult对象到对象池
                        if (tempResultAnd != null) {
                            staticParams.resultCache.sendBack(tempResultAnd);
                        }
                    } else {
                        isBreak = true;
                        break;
                    }
                }
                if (root != null) {
                    tempListAnd.add(root);
                }

                if (!isBreak) {
                    tempResult = staticParams.resultCache.fetch().addQLMatchResultTreeList(tempListAnd);
                    tempResult.setMatchLastIndex(pointAnd);
                    traceLog(pattern, tempResult, nodes, lastPoint, matchCount);
                } else {
                    tempResult = null;
                }

                if (tempListAnd != null) {
                    staticParams.arrayListCache.sendBack(tempListAnd);
                }
            } else if (pattern.matchMode == MatchMode.OR) {
                //tempResult = matchOrOneTime(iNodeTypeManager,pattern,nodes, lastPoint,maxMatchPoint,deep,maxDeep);

                for (QLPatternNode item : pattern.getChildren()) {
                    tempResult = findMatchStatementWithAddRootOptimizeStack(staticParams, item, lastPoint, false, deep);
                    if (tempResult != null) {
                        break;
                    }
                }
            } else {
                throw new QLCompileException("不正确的类型：" + pattern.matchMode.toString());
            }

            if (tempResult == null) {
                if (count >= pattern.minMatchNum && count <= pattern.maxMatchNum) {
                    //正确匹配
                    result = staticParams.resultCache.fetch();
                    if (tempList != null) {
                        result.addQLMatchResultTreeList(tempList);
                    }
                    result.setMatchLastIndex(lastPoint);
                } else {
                    result = null;
                }
                break;
            } else {
                if (tempList == null) {
                    tempList = staticParams.arrayListCache.fetch();
                }
                lastPoint = tempResult.getMatchLastIndex();
                if (pattern.isTreeRoot) {
                    if (tempResult.getMatchSize() > 1) {
                        throw new QLCompileException("根节点的数量必须是1");
                    }
                    if (tempList.size() == 0) {
                        tempList.addAll(tempResult.getMatches());
                    } else {
                        tempResult.getMatches().get(0).addLeftAll(tempList);
                        //为了能回收QLMatchResult对象,这个地方必须进行数组拷贝
                        tempList = staticParams.arrayListCache.fetch();
                        tempList.addAll(tempResult.getMatches());
                    }
                } else {
                    tempList.addAll(tempResult.getMatches());
                }
            }

            // 归还QLMatchResult
            if (tempResult != null) {
                staticParams.resultCache.sendBack(tempResult);
            }

            count = count + 1;
            if (count == pattern.maxMatchNum) {
                result = staticParams.resultCache.fetch();
                if (tempList != null) {
                    result.addQLMatchResultTreeList(tempList);
                }
                result.setMatchLastIndex(lastPoint);
                break;
            }
        }
        if (result != null && pattern.isSkip) {
            //忽略跳过所有匹配到的节点
            result.getMatches().clear();
        }

        if (result != null && result.getMatchSize() > 0 && pattern.rootNodeType != null) {
            QLMatchResultTree tempTree = new QLMatchResultTree(pattern.rootNodeType,
                nodes.get(0).createExpressNode(pattern.rootNodeType, null));
            tempTree.addLeftAll(result.getMatches());
            result.getMatches().clear();
            result.getMatches().add(tempTree);
        }
        if (tempList != null) {
            staticParams.arrayListCache.sendBack(tempList);
        }
        return result;
    }

    public static void traceLog(QLPatternNode pattern, QLMatchResult result,
        List<? extends IDataNode> nodes, int point, int matchCount) {
        if (log.isTraceEnabled() && (pattern.matchMode == MatchMode.DETAIL
            || pattern.matchMode == MatchMode.AND && matchCount > 1 && !"ANONY_PATTERN".equals(pattern.name))) {
            log.trace("匹配--" + pattern.name + "[" + point + ":" + (result.getMatchLastIndex() - 1) + "]:" + pattern);
        }
    }

    public static class MatchParamsPack {
        final INodeTypeManager iNodeTypeManager;
        final List<? extends IDataNode> nodes;
        final AtomicLong maxDeep;
        final AtomicLong maxMatchPoint;
        final QLMatchResultCache resultCache;
        final ArrayListCache arrayListCache;

        public MatchParamsPack(INodeTypeManager iNodeTypeManager, List<? extends IDataNode> nodes, AtomicLong maxDeep,
            AtomicLong maxMatchPoint, QLMatchResultCache qlMatchResultCache, ArrayListCache arrayListCache) {
            this.iNodeTypeManager = iNodeTypeManager;
            this.nodes = nodes;
            this.maxDeep = maxDeep;
            this.maxMatchPoint = maxMatchPoint;
            this.resultCache = qlMatchResultCache;
            this.arrayListCache = arrayListCache;
        }
    }

    public static class QLMatchResultCache {
        public int newCount = 0;
        public int fetchCount = 0;

        private final QLMatchResult[] cache;
        private final int length;
        private int point;

        public QLMatchResultCache(int length) {
            this.length = length;
            this.point = this.length - 1;
            cache = new QLMatchResult[this.length];
            for (int i = 0; i < this.length; i++) {
                cache[i] = new QLMatchResult();
            }
        }

        public QLMatchResult fetch() {
            QLMatchResult result;
            if (point >= 0) {
                result = cache[point];
                cache[point] = null;
                point = point - 1;
                fetchCount++;
            } else {
                result = new QLMatchResult();
                newCount++;
            }
            return result;
        }

        public void sendBack(QLMatchResult result) {
            if (this.point < this.length - 1) {
                this.point = this.point + 1;
                cache[this.point] = result;
                cache[this.point].clear();
            }
        }
    }

    public static class ArrayListCache<T> {

        public int newCount = 0;
        public int fetchCount = 0;

        private final List<T>[] cache;
        private final int len;
        private int point;

        public ArrayListCache(int length) {
            this.len = length;
            this.point = this.len - 1;
            cache = new List[this.len];
            for (int i = 0; i < this.len; i++) {
                cache[i] = new ArrayList<>();
            }
        }

        public List<T> fetch() {
            List<T> result;
            if (point >= 0) {
                result = cache[point];
                cache[point] = null;
                point = point - 1;
                fetchCount++;
            } else {
                result = new ArrayList<>();
                newCount++;
            }
            return result;
        }

        public void sendBack(List<T> result) {
            if (this.point < this.len - 1) {
                this.point = this.point + 1;
                cache[this.point] = result;
                cache[this.point].clear();
            }
        }
    }
}


