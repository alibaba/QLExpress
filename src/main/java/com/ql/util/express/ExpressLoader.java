package com.ql.util.express;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.FunctionInstructionSet;


/**
 * 表达式装载器
 * 
 * @author xuannan
 * 
 */
public class ExpressLoader {
	private  ConcurrentHashMap<String, InstructionSet> expressInstructionSetCache = new ConcurrentHashMap<String, InstructionSet>();
	ExpressRunner creator;
	public ExpressLoader(ExpressRunner aCreator){
		this.creator = aCreator;
	}
	public InstructionSet loadExpress(String expressName)
			throws Exception {
		return parseInstructionSet(expressName,this.creator.getExpressResourceLoader().loadExpress(expressName));		
	}

	public void addInstructionSet(String expressName, InstructionSet set)
			throws Exception {
		synchronized (expressInstructionSetCache) {
			if (expressInstructionSetCache.containsKey(expressName)) {
				throw new QLException("表达式定义重复：" + expressName);
			}
			expressInstructionSetCache.put(expressName, set);
		}
	}

	public InstructionSet parseInstructionSet(String expressName,
			String expressString) throws Exception {
		InstructionSet parseResult = null;
		if (expressInstructionSetCache.containsKey(expressName)) {
			throw new QLException("表达式定义重复：" + expressName);
		}
		synchronized (expressInstructionSetCache) {
			parseResult = this.creator.parseInstructionSet(expressString);
			parseResult.setName(expressName);
			parseResult.setGlobeName(expressName);
			// 需要将函数和宏定义都提取出来
			for (FunctionInstructionSet item : parseResult
					.getFunctionInstructionSets()) {
				this.addInstructionSet(item.name, item.instructionSet);
				item.instructionSet.setName(item.name);
				item.instructionSet.setGlobeName(expressName+ "." + item.name);
			}
			if(parseResult.hasMain()){
			   this.addInstructionSet(expressName, parseResult);
			}
		}
		return parseResult;
	}
   public void clear(){
	   this.expressInstructionSetCache.clear();
   }
	public InstructionSet getInstructionSet(String expressName) {
			return expressInstructionSetCache.get(expressName);
	}
	public ExportItem[] getExportInfo(){
		Map<String,ExportItem> result = new TreeMap<String,ExportItem>();
		for(InstructionSet item:expressInstructionSetCache.values()){
			for(ExportItem var:item.getExportDef()){
				var.setGlobeName(item.getGlobeName() + "." + var.name);
				result.put(var.getGlobeName(),var);
			}
			result.put(item.getGlobeName(),new ExportItem(item.getGlobeName(), item.getName(),item.getType(),item.toString()));
		}
		return (ExportItem[])result.values().toArray(new ExportItem[0]);
	}
}
