package com.ql.util.express;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.ql.util.express.config.QLExpressTimer;
import com.ql.util.express.exception.QLException;
import com.ql.util.express.instruction.FunctionInstructionSet;
import com.ql.util.express.instruction.OperateDataCacheManager;
import com.ql.util.express.instruction.detail.Instruction;
import com.ql.util.express.instruction.detail.InstructionCallSelfDefineFunction;
import com.ql.util.express.instruction.detail.InstructionConstData;
import com.ql.util.express.instruction.detail.InstructionLoadAttr;
import com.ql.util.express.instruction.detail.InstructionNewVirClass;
import com.ql.util.express.instruction.detail.InstructionOperator;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

/**
 * 表达式执行编译后形成的指令集合
 *
 * @author qhlhl2010@gmail.com
 */
public class InstructionSet {
    public static final String TYPE_CLASS = "VClass";
    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_MACRO = "macro";

    private final String type;
    private String name;
    private String globeName;

    /**
     * 指令
     */
    private Instruction[] instructionList = new Instruction[0];

    /**
     * 函数和宏定义
     */
    private final Map<String, FunctionInstructionSet> functionDefine = new HashMap<>();

    /**
     * 为了增加性能，开始的时候缓存为数组
     */
    private Map<String, Object> cacheFunctionSet = null;
    private final List<ExportItem> exportVar = new ArrayList<>();
    /**
     * 函数参数定义
     */
    private final List<OperateDataLocalVar> parameterList = new ArrayList<>();

    public InstructionSet(String type) {
        this.type = type;
    }

    public String[] getOutFunctionNames() {
        Map<String, String> result = new TreeMap<>();
        for (Instruction instruction : instructionList) {
            if (instruction instanceof InstructionCallSelfDefineFunction) {
                String functionName = ((InstructionCallSelfDefineFunction)instruction).getFunctionName();
                if (!functionDefine.containsKey(functionName)) {
                    result.put(functionName, null);
                }
            }
        }
        return result.keySet().toArray(new String[0]);
    }

    public String[] getVirClasses() {
        Map<String, String> result = new TreeMap<>();
        for (Instruction instruction : instructionList) {
            if (instruction instanceof InstructionNewVirClass) {
                String functionName = ((InstructionNewVirClass)instruction).getClassName();
                result.put(functionName, null);
            }
        }
        return result.keySet().toArray(new String[0]);
    }

    public String[] getOutAttrNames() throws Exception {
        Map<String, String> result = new TreeMap<>();
        for (Instruction instruction : instructionList) {
            if (instruction instanceof InstructionLoadAttr) {
                if ("null".equals(((InstructionLoadAttr)instruction).getAttrName())) {
                    continue;
                }
                result.put(((InstructionLoadAttr)instruction).getAttrName(), null);
            }
        }

        //剔除本地变量定义和别名定义
        for (int i = 0; i < instructionList.length; i++) {
            Instruction instruction = instructionList[i];
            if (instruction instanceof InstructionOperator) {
                String opName = ((InstructionOperator)instruction).getOperator().getName();
                //addOperator(op)中op.name有可能为空
                if (opName != null) {
                    if ("def".equalsIgnoreCase(opName) || "exportDef".equalsIgnoreCase(opName)) {
                        String varLocalName = (String)((InstructionConstData)instructionList[i - 1]).getOperateData()
                            .getObject(null);
                        result.remove(varLocalName);
                    } else if ("alias".equalsIgnoreCase(opName) || "exportAlias".equalsIgnoreCase(opName)) {
                        String varLocalName = (String)((InstructionConstData)instructionList[i - 2]).getOperateData()
                            .getObject(null);
                        result.remove(varLocalName);
                    }
                }
            }
        }
        return result.keySet().toArray(new String[0]);
    }

    /**
     * 添加指令，为了提高运行期的效率，指令集用数组存储
     *
     * @param item
     */
    private void addArrayItem(Instruction item) {
        Instruction[] newArray = new Instruction[this.instructionList.length + 1];
        System.arraycopy(this.instructionList, 0, newArray, 0, this.instructionList.length);
        newArray[this.instructionList.length] = item;
        this.instructionList = newArray;
    }

    /**
     * 插入数据
     *
     * @param point
     * @param item
     */
    private void insertArrayItem(int point, Instruction item) {
        Instruction[] newArray = new Instruction[this.instructionList.length + 1];
        System.arraycopy(this.instructionList, 0, newArray, 0, point);
        System.arraycopy(this.instructionList, point, newArray, point + 1, this.instructionList.length - point);
        newArray[point] = item;
        this.instructionList = newArray;
    }

    /**
     * @param environment
     * @param context
     * @param errorList
     * @param isReturnLastData 是否最后的结果，主要是在执行宏定义的时候需要
     * @return
     * @throws Exception
     */
    public CallResult execute(RunEnvironment environment, InstructionSetContext context, List<String> errorList,
        boolean isReturnLastData) throws Exception {
        //将函数export到上下文中,这儿就是重入也没有关系，不需要考虑并发
        if (cacheFunctionSet == null) {
            Map<String, Object> tempMap = new HashMap<>();
            for (FunctionInstructionSet s : this.functionDefine.values()) {
                tempMap.put(s.name, s.instructionSet);
            }
            cacheFunctionSet = tempMap;
        }

        context.addSymbol(cacheFunctionSet);

        this.executeInnerOriginalInstruction(environment, errorList);
        // 是在执行完所有的指令后结束的代码
        if (!environment.isExit()) {
            if (environment.getDataStackSize() > 0) {
                OperateData tmpObject = environment.pop();
                if (tmpObject == null) {
                    environment.quitExpress(null);
                } else {
                    if (isReturnLastData) {
                        if (tmpObject.getType(context) != null && tmpObject.getType(context).equals(void.class)) {
                            environment.quitExpress(null);
                        } else {
                            environment.quitExpress(tmpObject.getObject(context));
                        }
                    } else {
                        environment.quitExpress(tmpObject);
                    }
                }
            }
        }
        if (environment.getDataStackSize() > 1) {
            throw new QLException("在表达式执行完毕后，堆栈中还存在多个数据");
        }
        return OperateDataCacheManager.fetchCallResult(environment.getReturnValue(), environment.isExit());
    }

    public void executeInnerOriginalInstruction(RunEnvironment environment, List<String> errorList)
        throws Exception {
        Instruction instruction = null;
        try {
            while (environment.programPoint < this.instructionList.length) {
                QLExpressTimer.assertTimeOut();
                instruction = this.instructionList[environment.programPoint];
                instruction.execute(environment, errorList);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public int getInstructionLength() {
        return this.instructionList.length;
    }

    public void addMacroDefine(String macroName, FunctionInstructionSet functionInstructionSet) {
        this.functionDefine.put(macroName, functionInstructionSet);
    }

    public FunctionInstructionSet getMacroDefine(String macroName) {
        return this.functionDefine.get(macroName);
    }

    public FunctionInstructionSet[] getFunctionInstructionSets() {
        return this.functionDefine.values().toArray(new FunctionInstructionSet[0]);
    }

    public void addExportDef(ExportItem e) {
        this.exportVar.add(e);
    }

    public List<ExportItem> getExportDef() {
        return new ArrayList<>(this.exportVar);
    }

    public OperateDataLocalVar[] getParameters() {
        return this.parameterList.toArray(new OperateDataLocalVar[0]);
    }

    public void addParameter(OperateDataLocalVar localVar) {
        this.parameterList.add(localVar);
    }

    public void addInstruction(Instruction instruction) {
        this.addArrayItem(instruction);
    }

    public void insertInstruction(int point, Instruction instruction) {
        this.insertArrayItem(point, instruction);
    }

    public Instruction getInstruction(int point) {
        return this.instructionList[point];
    }

    public int getCurrentPoint() {
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

    public boolean hasMain() {
        return this.instructionList.length > 0;
    }

    public String getType() {
        return type;
    }

    public void appendSpace(StringBuilder stringBuilder, int level) {
        for (int i = 0; i < level; i++) {
            stringBuilder.append("    ");
        }
    }

    @Override
    public String toString() {
        return "\n" + toString(0);
    }

    public String toString(int level) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            // 输出宏定义
            for (FunctionInstructionSet set : this.functionDefine.values()) {
                appendSpace(stringBuilder, level);
                stringBuilder.append(set.type).append(":").append(set.name).append("(");
                for (int i = 0; i < set.instructionSet.parameterList.size(); i++) {
                    OperateDataLocalVar operateDataLocalVar = set.instructionSet.parameterList.get(i);
                    if (i > 0) {
                        stringBuilder.append(",");
                    }
                    stringBuilder.append(operateDataLocalVar.getType(null).getName()).append(" ")
                        .append(operateDataLocalVar.getName());
                }
                stringBuilder.append("){\n");
                stringBuilder.append(set.instructionSet.toString(level + 1));
                appendSpace(stringBuilder, level);
                stringBuilder.append("}\n");
            }
            for (int i = 0; i < this.instructionList.length; i++) {
                appendSpace(stringBuilder, level);
                stringBuilder.append(i + 1).append(":").append(this.instructionList[i]).append("\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
