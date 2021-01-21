package com.ql.util.express.instruction;

import java.util.ArrayList;
import java.util.List;

import com.ql.util.express.instruction.detail.InstructionGoTo;

public class ForRelBreakContinue{
	 List<InstructionGoTo> breakList = new ArrayList<InstructionGoTo>();
	 List<InstructionGoTo> continueList = new ArrayList<InstructionGoTo>();

	public void sign(){
		for(InstructionGoTo instructionGoTo : this.breakList){
			instructionGoTo.setMoveTimes(instructionGoTo.getMoveTimes()+1);
		}
		for(InstructionGoTo instructionGoTo : this.continueList){
			instructionGoTo.setMoveTimes(instructionGoTo.getMoveTimes()+1);
		}
	}

}
