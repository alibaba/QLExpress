package com.ql.util.express;

import java.io.Serializable;

/**
 * 简单的缓存对象
 * @author tianqiao
 *
 */
public class CacheObject implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -145121001676214513L;

	private String expressName;
	
	private String text;
	
	private InstructionSet instructionSet;

	public String getExpressName() {
		return expressName;
	}

	public void setExpressName(String name) {
		this.expressName = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public InstructionSet getInstructionSet() {
		return instructionSet;
	}

	public void setInstructionSet(InstructionSet instructionSet) {
		this.instructionSet = instructionSet;
	}

}
