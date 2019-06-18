package com.ql.util.express;


import com.ql.util.express.exception.QLException;

public final class RunEnvironment {
		private static int INIT_DATA_LENTH = 15;
	    private boolean isTrace = false;	
		private int point = -1;
	    protected int programPoint = 0;
		private OperateData[] dataContainer;
		private ArraySwap arraySwap = new ArraySwap();
		
		private boolean isExit = false;
		private Object returnValue = null; 
		
		private InstructionSet instructionSet;
		private InstructionSetContext context;
		
		
		public RunEnvironment(InstructionSet aInstructionSet,InstructionSetContext  aContext,boolean aIsTrace){
			dataContainer = new OperateData[INIT_DATA_LENTH];
			this.instructionSet = aInstructionSet;
			this.context = aContext;
			this.isTrace = aIsTrace;
		}
		
		public void initial(InstructionSet aInstructionSet,InstructionSetContext  aContext,boolean aIsTrace){
			this.instructionSet = aInstructionSet;
			this.context = aContext;
			this.isTrace = aIsTrace;
		}
		public void clear(){
		    isTrace = false;	
			point = -1;
		    programPoint = 0;
			
			isExit = false;
			returnValue = null; 
			
			instructionSet = null;
			context = null;
			
		}
		public InstructionSet getInstructionSet() {
			return instructionSet;
		}


		public InstructionSetContext getContext(){
			return this.context;
		}
		public void setContext(InstructionSetContext aContext){
			this.context = aContext;
		}

		public boolean isExit() {
			return isExit;
		}
		public Object getReturnValue() {
			return returnValue;
		}
		public void setReturnValue(Object value){
			this.returnValue = value;
		}
		public void quitExpress(Object aReturnValue){
			this.isExit = true;
			this.returnValue = aReturnValue;
		}
		public void quitExpress(){
			this.isExit = true;
			this.returnValue = null;
		}
		public boolean isTrace(){
			return this.isTrace;
		}
		public int getProgramPoint() {
			return programPoint;
		}
		public void programPointAddOne() {
			programPoint ++ ;
		}
		public void gotoLastWhenReturn(){
			programPoint = this.instructionSet.getInstructionLength();
		}
	    public int getDataStackSize(){
	    	return this.point + 1;
	    }
		public void push(OperateData data){
			this.point++;
			if(this.point >= this.dataContainer.length){
			   ensureCapacity(this.point + 1);
			}
			this.dataContainer[point] = data;
		}
		public OperateData peek(){
			if(point <0){
				throw new RuntimeException("系统异常，堆栈指针错误");
			}
			return this.dataContainer[point];		
		}
		public OperateData pop(){
			if(point <0)
				throw new RuntimeException("系统异常，堆栈指针错误");
			OperateData result = this.dataContainer[point];
			this.point--;
			return result;
		}
		public void clearDataStack(){
			this.point = -1;
		}
		public void gotoWithOffset(int aOffset ){
			this.programPoint = this.programPoint + aOffset;
		}
	/**
	 * 此方法是调用最频繁的，因此尽量精简代码，提高效率 
	 * @param context
	 * @param len
	 * @return
	 * @throws Exception
	 */
		public ArraySwap popArray(InstructionSetContext context,int len) throws Exception {
			int start = point - len + 1;
			this.arraySwap.swap(this.dataContainer,start,len);
			point = point - len;
			return this.arraySwap;
		}
		
		public OperateData[] popArrayOld(InstructionSetContext context,int len) throws Exception {
			int start = point - len + 1;
			OperateData[] result = new OperateData[len];
			System.arraycopy(this.dataContainer,start, result,0, len);
			point = point - len;
			return result;
		}
		
		public OperateData[] popArrayBackUp(InstructionSetContext context,int len) throws Exception {
			int start = point - len + 1;
			if(start <0){
				throw new QLException("堆栈溢出，请检查表达式是否错误");
			}
			OperateData[] result = new OperateData[len];
			for (int i = 0 ; i < len; i++) {
				result[i] = this.dataContainer[start + i];
				if(void.class.equals(result[i].getType(context))){
					throw new QLException("void 不能参与任何操作运算,请检查使用在表达式中使用了没有返回值的函数,或者分支不完整的if语句");
				}
			}
			point = point - len;
			return result;
		}

		public void ensureCapacity(int minCapacity) {
			int oldCapacity = this.dataContainer.length;
			if (minCapacity > oldCapacity) {
				int newCapacity = (oldCapacity * 3) / 2 + 1;
				if (newCapacity < minCapacity){
					newCapacity = minCapacity;
				}
				OperateData[] tempList = new OperateData[newCapacity];
				System.arraycopy(this.dataContainer,0,tempList,0,oldCapacity);
				this.dataContainer = tempList;
			}
		}
	}
