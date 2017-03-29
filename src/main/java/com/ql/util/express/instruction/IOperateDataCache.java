package com.ql.util.express.instruction;

import com.ql.util.express.CallResult;
import com.ql.util.express.ExpressLoader;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.IExpressContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.InstructionSetContext;
import com.ql.util.express.OperateData;
import com.ql.util.express.RunEnvironment;
import com.ql.util.express.instruction.opdata.OperateDataArrayItem;
import com.ql.util.express.instruction.opdata.OperateDataAttr;
import com.ql.util.express.instruction.opdata.OperateDataField;
import com.ql.util.express.instruction.opdata.OperateDataKeyValue;
import com.ql.util.express.instruction.opdata.OperateDataLocalVar;

public interface IOperateDataCache {	
		public  OperateData fetchOperateData(Object obj, Class<?> aType);
		public  OperateDataAttr fetchOperateDataAttr(String name, Class<?> aType);
		public  OperateDataLocalVar fetchOperateDataLocalVar(String name, Class<?> aType);
		public  OperateDataField fetchOperateDataField(Object aFieldObject,String aFieldName);
		public  OperateDataArrayItem fetchOperateDataArrayItem(OperateData aArrayObject,int aIndex);
		public  OperateDataKeyValue fetchOperateDataKeyValue(OperateData aKey, OperateData aValue);
		public  RunEnvironment fetRunEnvironment(InstructionSet aInstructionSet,InstructionSetContext  aContext,boolean aIsTrace);
		public  CallResult fetchCallResult(Object aReturnValue,boolean aIsExit);
		public  InstructionSetContext fetchInstructionSetContext(boolean aIsExpandToParent,ExpressRunner aRunner,IExpressContext<String,Object> aParent,ExpressLoader aExpressLoader,boolean aIsSupportDynamicFieldName);
		public  void resetCache();
		public  long getFetchCount();		
	}
