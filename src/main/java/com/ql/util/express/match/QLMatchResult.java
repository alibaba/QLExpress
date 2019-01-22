package com.ql.util.express.match;

import java.util.ArrayList;
import java.util.List;

public class QLMatchResult {
		private List<QLMatchResultTree> matchs = new ArrayList<QLMatchResultTree>();
		private int matchLastIndex;


		public void clear(){
			this.matchLastIndex =0;
			this.matchs.clear();
		}

		public String toString(){
			StringBuilder builder = new StringBuilder();
			for(QLMatchResultTree item:matchs){
			   item.printNode(builder,1);
			}
			return builder.toString();
		}
		public List<QLMatchResultTree> getMatchs() {
			return matchs;
		}

		public QLMatchResult addQLMatchResultTree(QLMatchResultTree tree){
			this.matchs.add(tree);
			return this;
		}
	public QLMatchResult addQLMatchResultTreeList(List<QLMatchResultTree> aList){
		this.matchs.addAll(aList);
		return this;
	}

	public int getMatchSize(){
		return this.matchs.size();
	}
		public int getMatchLastIndex() {
			return matchLastIndex;
		}
	public QLMatchResult setMatchLastIndex(int index){
		this.matchLastIndex = index;
		return this;
	}
}
