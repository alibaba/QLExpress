package com.ql.util.express.bugfix;

import org.junit.Test;

public class TestOverFlow {


    public class Result{
        private int a = 0;
        private String b = "我是一个长长的字符串";

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public String getB() {
            return b;
        }

        public void setB(String b) {
            this.b = b;
        }
    }

    private int count = 0;

    public void fun(){
        count ++;
        fun();
    }

    public void fun_a(int a){
        count ++;
        fun_a(a);
    }

    public int funReturn(){
        count ++;
        int a = funReturn();
        return 1 + a + 1;
    }

    public Result funBigReturn(){
        count ++;
        Result a = funBigReturn();
        a.setA(1);
        return a;
    }

    public void fun_abc(int a,int b,int c){
        count ++;
        fun_abc(a+1,b+1,c+1);
    }


    public void fun_abc2(int a,int b,int c){
        count ++;
        fun_a(a,b,c);
    }


    public void fun_a(int a,int b,int c){
        count ++;
        fun_abc2(a+1,b,c);
    }
    public void fun_b(int a,int b,int c){
        count ++;
        fun_abc2(a,b+1,c);
    }
    public void fun_c(int a,int b,int c){
        count ++;
        fun_abc2(a,b,c+1);
    }

    public void test(){
        try{
            this.count = 0;
            fun();
        }catch(Throwable e){
            System.out.println("fun() 最大栈深度:"+count);
        }
    }

    public void test_a(){
        try{
            this.count = 0;
            fun_a(1);
        }catch(Throwable e){

            System.out.println("fun_a() 最大栈深度:"+count);
        }
    }

    public void testReturn(){
        try{
            this.count = 0;
            funReturn();
        }catch(Throwable e){

            System.out.println("funReturn() 最大栈深度:"+count);
        }

        try{
            this.count = 0;
            funBigReturn();
        }catch(Throwable e){

            System.out.println("funBigReturn() 最大栈深度:"+count);
        }
    }
    public void test_abc() {
        try {
            this.count = 0;
            fun_abc(1, 1, 1);
        } catch (Throwable e) {

            System.out.println("fun_abc() 最大栈深度:" + count);
        }
    }
    public void test_abc2(){
        try{
            this.count = 0;
            fun_abc2(1,1,1);
        }catch(Throwable e){

            System.out.println("fun_abc2() 最大栈深度:"+count);
        }
    }


    public void testOverFlow()
    {
        test();
        test_a();
        testReturn();
        test_abc();
        test_abc2();
    }
    
    @Test
    public void testOverflow(){
        new TestOverFlow().testOverFlow();
    }
}
