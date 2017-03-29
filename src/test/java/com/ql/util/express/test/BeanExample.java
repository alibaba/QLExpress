package com.ql.util.express.test;

public class BeanExample {
	public String name = "qhlhl2010@gmail.com";
	public int intValue;
	public long longValue;
	public double doubleValue;
	
	public BeanExampleChild child = new BeanExampleChild();
	public BeanExample() {
	}
    public static void printNumber(Number l,Number f){
    	System.out.println("f-------:" + l +'-'+ f);
    }
	public BeanExampleChild getChild() {
		return child;
	}

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String getText(String a,String b){
		return a +"-" + b;
	}
	public String getText(String a,Long b){
		return a +"-" + b;
	}
	
	public long getLongValue() {
		return longValue;
	}

	public void setLongValue(long longValue) {
		this.longValue = longValue;
	}

	public double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public void setChild(BeanExampleChild child) {
		this.child = child;
	}
    public String getName(){
    	return this.name;
    }
	public BeanExample(String aName) {
		name = aName;
	}
	public String testLongObject(Long i){		
		return "toString-LongObject:" + i;
	}
	public String testLong(long i){		
		return "toString-long:" + i;
	}
	public String testInt(int i){		
		return "toString-int:" + i;
	}
	public String unionName(String otherName) {
		//System.out.println(" execute unionName("+ otherName+ ") .....");
		return name + "-" + otherName;
	}

	public static String upper(String abc) {
		return abc.toUpperCase();
	}

	public static boolean isVIP(String name) {
		//System.out.println(" execute isVIP("+ name+ ") .....");
		return false;
	}
	public static boolean isVIPTwo(String name) {
		//System.out.println(" execute isVIP("+ name+ ") .....");
		return true;
	}
	public static boolean isVIPFalse() {		
		return false;
	}
}
