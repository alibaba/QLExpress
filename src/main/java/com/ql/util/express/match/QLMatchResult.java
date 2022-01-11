package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

public class QLMatchResult {
    private final List<QLMatchResultTree> matches = new ArrayList<>();
    private int matchLastIndex;

    public void clear() {
        this.matchLastIndex = 0;
        this.matches.clear();
    }

    public List<QLMatchResultTree> getMatches() {
        return matches;
    }

    public QLMatchResult addQLMatchResultTree(QLMatchResultTree tree) {
        this.matches.add(tree);
        return this;
    }

    public QLMatchResult addQLMatchResultTreeList(List<QLMatchResultTree> qlMatchResultTreeList) {
        this.matches.addAll(qlMatchResultTreeList);
        return this;
    }

    public int getMatchSize() {
        return this.matches.size();
    }

    public int getMatchLastIndex() {
        return matchLastIndex;
    }

    public QLMatchResult setMatchLastIndex(int index) {
        this.matchLastIndex = index;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (QLMatchResultTree item : matches) {
            item.printNode(builder, 1);
        }
        return builder.toString();
    }
}
