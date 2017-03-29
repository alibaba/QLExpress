package com.ql.util.express.match;

import java.util.List;

public class QLMatchResult {
		protected List<QLMatchResultTree> matchs;
		protected int matchLastIndex;
		public  INodeType statementNodeType;
		public QLMatchResult(List<QLMatchResultTree> aList,int aIndex){
			this.matchLastIndex = aIndex;
			this.matchs = aList;
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

		public void setMatchs(List<QLMatchResultTree> matchs) {
			this.matchs = matchs;
		}
		public int getMatchLastIndex() {
			return matchLastIndex;
		}

		public void setMatchLastIndex(int matchLastIndex) {
			this.matchLastIndex = matchLastIndex;
		}
	}
