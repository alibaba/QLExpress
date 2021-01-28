package com.ql.util.express;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.detail.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;



/**
 * The instruction set formed after the expression is executed and compiled
 * @author qhlhl2010@gmail.com
 *
 */

public class InstructionSet implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1841743860792681669L;
	
	private static final transient  Log log = LogFactory.getLog(InstructionSet.class);
	public static AtomicInteger uniqIndex = new AtomicInteger(1);
	public static String TYPE_MAIN ="main";
	public static String TYPE_CLASS ="VClass";
	public static String TYPE_FUNCTION ="function";
	public static String TYPE_MARCO ="marco";
	
	public static boolean printInstructionError = false;
	
	
	private String type ="main";
	private String name;
	private String globeName;
	
  /**
   * instruction
   */
  private Instruction[] instructionList = new Instruction[0];
  /**
   * Function and macro definition
   */
  private Map<String,FunctionInstructionSet> functionDefine = new HashMap<String,FunctionInstructionSet>();
  //为了增加性能，开始的时候缓存为数组
  private Map<String,Object> cacheFunctionSet = null;
  private List<ExportItem> exportVar = new ArrayList<ExportItem>();
  /**
   * Function parameter definition
   */
  private List<OperateDataLocalVar> parameterList = new ArrayList<OperateDataLocalVar>();
  
  public static int getUniqClassIndex(){
	  return uniqIndex.getAndIncrement();
  }
  public InstructionSet(String aType){
	  this.type = aType;
  }

  public String[] getOutFunctionNames() throws Exception {
	  Map<String,String> result = new TreeMap<String,String>();
	  for (int i = 0; i < instructionList.length; i++) {
		  Instruction instruction = instructionList[i];
		  if (instruction instanceof InstructionCallSelfDefineFunction) {
			  String functionName = ((InstructionCallSelfDefineFunction)instruction).getFunctionName();
			  if(!functionDefine.containsKey(functionName)) {
				  result.put(functionName, null);
			  }
		  }
	  }
	  return result.keySet().toArray(new String[0]);

  }
    
    public String[] getVirClasses() throws Exception {
        Map<String,String> result = new TreeMap<String,String>();
        for (int i = 0; i < instructionList.length; i++) {
            Instruction instruction = instructionList[i];
            if (instruction instanceof InstructionNewVirClass) {
                String functionName = ((InstructionNewVirClass)instruction).getClassName();
                result.put(functionName, null);
            }
        }
        return result.keySet().toArray(new String[0]);
        
    }
  public String[] getOutAttrNames() throws Exception{
	  Map<String,String> result = new TreeMap<String,String>();
	  for(Instruction instruction:instructionList){
		   if(instruction instanceof InstructionLoadAttr){
			   if("null".equals(((InstructionLoadAttr)instruction).getAttrName())){
				   continue;
			   }
			   result.put(((InstructionLoadAttr)instruction).getAttrName(),null);
		   }
	  }
	 
	  //Eliminate local variable definitions and alias definitions
		for (int i = 0; i < instructionList.length; i++) {
			Instruction instruction = instructionList[i];
			if (instruction instanceof InstructionOperator) {
				String opName = ((InstructionOperator) instruction)
						.getOperator().getName();
				if(opName != null){//addOperator(op)中op.name有可能为空
                    if (opName.equalsIgnoreCase("def")
                            || opName.equalsIgnoreCase("exportDef")) {
                        String varLocalName = (String) ((InstructionConstData) instructionList[i - 1])
                                .getOperateData().getObject(null);
                        result.remove(varLocalName);
                    } else if (opName.equalsIgnoreCase("alias")
                            || opName.equalsIgnoreCase("exportAlias")) {
                        String varLocalName = (String) ((InstructionConstData) instructionList[i - 2])
                                .getOperateData().getObject(null);
                        result.remove(varLocalName);
                    }
                }
			}
		}
	  return result.keySet().toArray(new String[0]);
  }

  
  /**
   * Add instructions, in order to improve the efficiency of the runtime, the instruction set is stored in an array
   * @param item
   * @return
   */
  private void addArrayItem(Instruction item){
	  Instruction[] newArray = new Instruction[this.instructionList.length + 1];
	  System.arraycopy(this.instructionList, 0, newArray, 0, this.instructionList.length);
	  newArray[this.instructionList.length] = item;
	  this.instructionList = newArray;
  }
  /**
   * Insert data
   * @param aPoint
   * @param item
   */
  private void insertArrayItem(int aPoint,Instruction item){
	  Instruction[] newArray = new Instruction[this.instructionList.length + 1];
	  System.arraycopy(this.instructionList, 0, newArray, 0, aPoint);
	  System.arraycopy(this.instructionList, aPoint, newArray, aPoint + 1,this.instructionList.length - aPoint);
	  newArray[aPoint] = item;
	  this.instructionList = newArray;
  }

/**
 * 
 * @param environmen
 * @param context
 * @param errorList
 * @param isReturnLastData Whether the final result is mainly needed when executing the macro definition
 * @param aLog
 * @return
 * @throws Exception
 */
	public CallResult excute(RunEnvironment environmen,InstructionSetContext context,
			List<String> errorList,boolean isReturnLastData,Log aLog)
			throws Exception {
		
		//Export the function to the context, it does not matter if it is reentrant here, no need to consider concurrency
		if(cacheFunctionSet == null){
			Map<String,Object> tempMap = new HashMap<String,Object>();
			for(FunctionInstructionSet s : this.functionDefine.values()){
				tempMap.put(s.name,s.instructionSet);
			}
			cacheFunctionSet = tempMap;
		}
		
		context.addSymbol(cacheFunctionSet);
		
		this.executeInnerOrigiInstruction(environmen, errorList, aLog);
		if (environmen.isExit() == false) {// Is the code that ends after executing all the instructions
			if (environmen.getDataStackSize() > 0) {
				OperateData tmpObject = environmen.pop();
				if (tmpObject == null) {
					environmen.quitExpress(null);
				} else {
					if(isReturnLastData == true){
						if(tmpObject.getType(context) != null && tmpObject.getType(context).equals(void.class)){
							environmen.quitExpress(null);
						}else{
						    environmen.quitExpress(tmpObject.getObject(context));
						}
					}else{
					    environmen.quitExpress(tmpObject);
					}
				}
			}
		}
		if (environmen.getDataStackSize() > 1) {
			throw new QLException("After the expression is executed, there are multiple data in the stack ");
		}
		CallResult result = OperateDataCacheManager.fetchCallResult(environmen.getReturnValue(), environmen.isExit());
		return result;
	}
	  public void executeInnerOrigiInstruction(RunEnvironment environmen,List<String> errorList,Log aLog) throws Exception{
			Instruction instruction =null;
		try {
			while (environmen.programPoint < this.instructionList.length) {
				QLExpressTimer.assertTimeOut();
				instruction = this.instructionList[environmen.programPoint];
				instruction.setLog(aLog);// 设置log
				instruction.execute(environmen, errorList);
			}
		} catch (Exception e) {
			if (printInstructionError) {
				log.error("Current ProgramPoint = " + environmen.programPoint);
				log.error("Current instruction =" + instruction);
				log.error(e);
			}
			throw e;
		}
	}
  public int getInstructionLength(){
	  return this.instructionList.length;
  }
  public void addMacroDefine(String macroName,FunctionInstructionSet iset){
	  this.functionDefine.put(macroName, iset);
  }
  public FunctionInstructionSet getMacroDefine(String macroName){
	  return this.functionDefine.get(macroName);
  }
  public FunctionInstructionSet[] getFunctionInstructionSets(){
	  return this.functionDefine.values().toArray(new FunctionInstructionSet[0]);
  }
  public void addExportDef(ExportItem e){
	  this.exportVar.add(e);
  }
  public List<ExportItem> getExportDef(){
	  List<ExportItem> result = new ArrayList<ExportItem> ();
	  result.addAll(this.exportVar);
	  return result;
  }

	
	public OperateDataLocalVar[] getParameters() {
		return this.parameterList.toArray(new OperateDataLocalVar[0]);
	}

	public void addParameter(OperateDataLocalVar localVar) {
		this.parameterList.add(localVar);
	}	
  public void addInstruction(Instruction instruction){
	  this.addArrayItem(instruction);
  }
  public void insertInstruction(int point,Instruction instruction){
	  this.insertArrayItem(point, instruction);
  } 
  public Instruction getInstruction(int point){
	  return this.instructionList[point];
  }
  public int getCurrentPoint(){
	  return this.instructionList.length - 1; 
  }
  
  public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getGlobeName() {
	return globeName;
}

public void setGlobeName(String globeName) {
	this.globeName = globeName;
}
public boolean hasMain(){
	return this.instructionList.length >0;
}
public String getType() {
	return type;
}
public void appendSpace(StringBuffer buffer,int level){
	for(int i=0;i<level;i++){
		buffer.append("    ");
	}
}
public String toString() {
	return "\n" + toString(0);
}
	public String toString(int level) {
		try {
			StringBuffer buffer = new StringBuffer();
			//Output macro definition
			for (FunctionInstructionSet set : this.functionDefine.values()) {
				appendSpace(buffer,level);
				buffer.append(set.type + ":" + set.name).append("(");
				for (int i=0;i<set.instructionSet.parameterList.size();i++) {
				    OperateDataLocalVar var = set.instructionSet.parameterList.get(i);
				    if(i > 0){
				    	buffer.append(",");
				    }
					buffer.append(var.getType(null).getName()).append(" ").append(var.getName());
				}
				buffer.append("){\n");
				buffer.append(set.instructionSet.toString(level + 1));
				appendSpace(buffer,level);
				buffer.append("}\n");
			}
			for (int i = 0; i < this.instructionList.length; i++) {
				appendSpace(buffer,level);
				buffer.append(i + 1).append(":").append(this.instructionList[i])
						.append("\n");
			}
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

	
