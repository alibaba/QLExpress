package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ql.util.express.match.INodeType;
import com.ql.util.express.match.QLPattern;
import com.ql.util.express.match.QLPatternNode;

enum NodeTypeKind {
	KEYWORD, BLOCK, EXPRESS, OPERATOR, WORDDEF, GROUP, STATEMENT
}

public class NodeType implements INodeType {
	NodeTypeManager manager;
	private String name;
	private String defineStr;
	private NodeTypeKind kind;
	private NodeType realNodeType;
	private String instructionFactory;
	/**
	 * Pattern matching
	 */
	private QLPatternNode qlPatternNode;

	protected NodeType(NodeTypeManager aManager, String aName, String aDefineStr) {
		this.manager = aManager;
		this.defineStr = aDefineStr;
		this.name = aName;
	}

	public static String[][] splitProperties(String str) {
		Pattern p = Pattern.compile("(,|:)\\s*(([A-Z]|-|_)*)\\s*=");
		Matcher matcher = p.matcher(str);
		List<String[]> list = new ArrayList<String[]>();
		int endIndex = 0;
		while (matcher.find()) {
			if (list.size() > 0) {
				list.get(list.size() - 1)[1] = str.substring(endIndex,
						matcher.start()).trim();
			}
			list.add(new String[2]);
			list.get(list.size() - 1)[0] = str.substring(matcher.start() + 1,
					matcher.end() - 1).trim();
			endIndex = matcher.end();
		}
		if (list.size() > 0) {
			list.get(list.size() - 1)[1] = str.substring(endIndex).trim();
		}
		return (String[][]) list.toArray(new String[0][2]);
	}

	public void initial() {
		try {
			int index = this.defineStr.indexOf(":", 1);
			String[][] properties = splitProperties(this.defineStr.substring(index));
			for (String[] tempList : properties) {
				if (tempList[0].equalsIgnoreCase("type")) {
					this.setKind(NodeTypeKind.valueOf(tempList[1]));
				} else if (tempList[0].equalsIgnoreCase("real")) {
					this.realNodeType = manager.findNodeType(tempList[1]);
				} else if (tempList[0].equalsIgnoreCase("factory")) {
					this.instructionFactory = tempList[1];
				} else if (tempList[0].equalsIgnoreCase("define")) {
					this.qlPatternNode = QLPattern.createPattern(this.manager,
							this.name, tempList[1]);
				} else {
					throw new RuntimeException("Not recognized\"" + this.name
							+ "\"Attribute type：" + tempList[0] + " definition："
							+ this.defineStr);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Node type\"" + this.name + "\"Initialization failed, definition："
					+ this.defineStr, e);
		}
	}
	public boolean isEqualsOrChild(String parent) {
		return this.manager.findNodeType(parent).isContainerChild(this);
	}

	public boolean isContainerChild(NodeType child) {
		if (this.equals(child)) {
			return true;
		}
		if (this.qlPatternNode == null) {
			return false;
		}
		if (this.qlPatternNode.isDetailMode()) {
			return ((NodeType) this.qlPatternNode.getNodeType())
					.isContainerChild(child);
		}
		// It is of type and, cannot add child nodes or judge
		if (this.qlPatternNode.isAndMode()
				&& this.qlPatternNode.getChildren().size() > 0) {
			return false;
		}
		for (QLPatternNode node : this.qlPatternNode.getChildren()) {
			if (node.getNodeType() != null
					&& ((NodeType) node.getNodeType()).isContainerChild(child)) {
				return true;
			}
		}
		return false;
	}

	public void addChild(NodeType child) throws Exception {
		String str = child.name;
		if (this.qlPatternNode != null) {
			str = this.qlPatternNode.toString() + "|" + str;
		}
		this.qlPatternNode = QLPattern.createPattern(this.manager, this.name,str);
	}

	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(name + ":TYPE=" + this.kind);
		if (this.instructionFactory != null) {
			result.append(",FACTORY=" + this.instructionFactory);
		}
		if (this.qlPatternNode != null) {
			result.append(",DEFINE=").append(this.qlPatternNode);
		}
		return result.toString();
	}

	public NodeType getRealNodeType() {
		return realNodeType;
	}

	public NodeTypeKind getKind() {
		return kind;
	}

	public String getInstructionFactory() {
		return instructionFactory;
	}

	public void setInstructionFactory(String instructionFactory) {
		this.instructionFactory = instructionFactory;
	}

	public NodeTypeManager getManager() {
		return manager;
	}

	public String getDefineStr() {
		return defineStr;
	}
	public void setKind(NodeTypeKind kind) {
		this.kind = kind;
	}

	public String getName() {
		return name;
	}


	public QLPatternNode getPatternNode() {
		return this.qlPatternNode;
	}

}
