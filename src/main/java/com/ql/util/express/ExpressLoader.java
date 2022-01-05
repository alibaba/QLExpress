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
 */
public class ExpressLoader {
    private final ConcurrentHashMap<String, InstructionSet> expressInstructionSetCache = new ConcurrentHashMap<>();
    final ExpressRunner expressRunner;

    public ExpressLoader(ExpressRunner expressRunner) {
        this.expressRunner = expressRunner;
    }

    public InstructionSet loadExpress(String expressName) throws Exception {
        return parseInstructionSet(expressName, this.expressRunner.getExpressResourceLoader().loadExpress(expressName));
    }

    public void addInstructionSet(String expressName, InstructionSet set) throws Exception {
        synchronized (expressInstructionSetCache) {
            if (expressInstructionSetCache.containsKey(expressName)) {
                throw new QLException("表达式定义重复：" + expressName);
            }
            expressInstructionSetCache.put(expressName, set);
        }
    }

    public InstructionSet parseInstructionSet(String expressName, String expressString) throws Exception {
        InstructionSet parseResult;
        if (expressInstructionSetCache.containsKey(expressName)) {
            throw new QLException("表达式定义重复：" + expressName);
        }
        synchronized (expressInstructionSetCache) {
            parseResult = this.expressRunner.parseInstructionSet(expressString);
            parseResult.setName(expressName);
            parseResult.setGlobeName(expressName);
            // 需要将函数和宏定义都提取出来
            for (FunctionInstructionSet item : parseResult
                .getFunctionInstructionSets()) {
                this.addInstructionSet(item.name, item.instructionSet);
                item.instructionSet.setName(item.name);
                item.instructionSet.setGlobeName(expressName + "." + item.name);
            }
            if (parseResult.hasMain()) {
                this.addInstructionSet(expressName, parseResult);
            }
        }
        return parseResult;
    }

    public void clear() {
        this.expressInstructionSetCache.clear();
    }

    public InstructionSet getInstructionSet(String expressName) {
        return expressInstructionSetCache.get(expressName);
    }

    public ExportItem[] getExportInfo() {
        Map<String, ExportItem> result = new TreeMap<>();
        for (InstructionSet instructionSet : expressInstructionSetCache.values()) {
            String globeName = instructionSet.getGlobeName();
            for (ExportItem exportItem : instructionSet.getExportDef()) {
                exportItem.setGlobeName(globeName + "." + exportItem.getName());
                result.put(exportItem.getGlobeName(), exportItem);
            }
            String name = instructionSet.getName();
            String type = instructionSet.getType();
            result.put(globeName, new ExportItem(globeName, name, type, instructionSet.toString()));
        }
        return result.values().toArray(new ExportItem[0]);
    }
}
