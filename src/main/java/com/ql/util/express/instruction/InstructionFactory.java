package com.ql.util.express.instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.parse.ExpressNode;

public abstract class InstructionFactory {
	private static Map<String,InstructionFactory> instructionFactory = new HashMap<String,InstructionFactory>();

	public static InstructionFactory getInstructionFactory(String factory) {
		try {
			InstructionFactory result = instructionFactory.get(factory);
			if (result == null) {
				result = (InstructionFactory) Class.forName(factory)
						.newInstance();
				instructionFactory.put(factory, result);
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public abstract boolean createInstruction(ExpressRunner aCompile,InstructionSet result,
			Stack<ForRelBreakContinue> forStack, ExpressNode node,boolean isRoot)
			throws Exception;
}
