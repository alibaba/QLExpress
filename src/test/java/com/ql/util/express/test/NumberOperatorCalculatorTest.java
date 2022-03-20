package com.ql.util.express.test;

import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.OperatorOfNumber;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * 高精度测试，低精度不能改变原有数据样式
 * @throws Exception
 */
public class NumberOperatorCalculatorTest {

    @Test
    public void add() throws Exception {
        //-2147483648,2147483647,-9223372036854775808,9223372036854775807
        Object[] ob1 = {Integer.MIN_VALUE,Integer.MAX_VALUE,Long.MIN_VALUE,Long.MAX_VALUE,new BigDecimal(Long.MAX_VALUE)};
        Object[] ob2 = {-0.5f,0.5f,-0.5d,0.5d,0,1,-1,1.0f,-1.0d,1.3f,-1.3d,new BigDecimal(Long.MAX_VALUE)};

        Object result = null;
        //Integer测试Float -2147483648.5 BigDecimal True:-2147483648.5
        result = OperatorOfNumber.add(ob1[0],ob2[0],true);
        Assert.assertTrue("ADD CASE1",result instanceof BigDecimal && result.equals(new BigDecimal(-2147483648.5)));
        //Integer测试Double -2147483648.5 BigDecimal True:-2147483648.5
        result = OperatorOfNumber.add(ob1[0],ob2[2],true);
        Assert.assertTrue("ADD CASE2",result instanceof BigDecimal && result.equals(new BigDecimal(-2147483648.5)));
        //0值 -2147483648 Integer True:-2147483648
        result = OperatorOfNumber.add(ob1[0],ob2[4],true);
        Assert.assertTrue("ADD CASE3",result instanceof Integer && result.equals(-2147483648));
        //Integer转Long Ori:2147483648 Long True:2147483648
        result = OperatorOfNumber.add(ob1[1],ob2[5],true);
        Assert.assertTrue("ADD CASE4",result instanceof Long && result.equals(2147483648L));
        //Long测试Float Ori:-9223372036854775808.5 BigDecimal True:-9223372036854775808.5
        result = OperatorOfNumber.add(ob1[2],ob2[2],true);
        Assert.assertTrue("ADD CASE5",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).add(new BigDecimal(-0.5))));
        //Long溢出 Ori:9223372036854775807 Long False:-9223372036854775809 BigDecimal
        result = OperatorOfNumber.add(ob1[2],ob2[6],true);
        Assert.assertTrue("ADD CASE6",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).add(new BigDecimal(-1))));
        //Long测试Double Ori:9223372036854775807.5 BigDecimal True:9223372036854775807.5
        result = OperatorOfNumber.add(ob1[3],ob2[3],true);
        Assert.assertTrue("ADD CASE7",result instanceof BigDecimal && result.equals(new BigDecimal(9223372036854775807L).add(new BigDecimal(0.5))));
        //溢出 Ori:-9223372036854775808 Long False:9223372036854775808 BigDecimal
        result = OperatorOfNumber.add(ob1[3],ob2[5],true);
        Assert.assertTrue("ADD CASE8",result instanceof BigDecimal && result.equals(new BigDecimal(9223372036854775807L).add(new BigDecimal(1L))));
        //溢出 Ori:-2 Integer False:9223372036854775807+9223372036854775807 BigDecimal
        result = OperatorOfNumber.add(ob1[4],ob2[11],true);
        Assert.assertTrue("ADD CASE9",result instanceof BigDecimal && result.equals(new BigDecimal(9223372036854775807L).add(new BigDecimal(9223372036854775807L))));
    }
    @Test
    public void subtract() throws Exception {
        //-2147483648,2147483647,-9223372036854775808,9223372036854775807
        Object[] ob1 = {Integer.MIN_VALUE,Integer.MAX_VALUE,Long.MIN_VALUE,Long.MAX_VALUE,new BigDecimal(Long.MIN_VALUE)};
        Object[] ob2 = {-0.5f,0.5f,-0.5d,0.5d,0,1,-1,1.0f,-1.0d,1.3f,-1.3d,new BigDecimal(Long.MAX_VALUE)};

        Object result = null;
        //Integer测试Float -2147483647.5 BigDecimal True:-2147483647.5
        result = OperatorOfNumber.subtract(ob1[0],ob2[0],true);
        Assert.assertTrue("SUB CASE1",result instanceof BigDecimal && result.equals(new BigDecimal(-2147483647.5)));
        //Integer测试Double -2147483648.5 BigDecimal True:-2147483648.5
        result = OperatorOfNumber.subtract(ob1[0],ob2[3],true);
        Assert.assertTrue("SUB CASE2",result instanceof BigDecimal && result.equals(new BigDecimal(-2147483648.5)));
        //0值 -2147483648 Integer True:-2147483648
        result = OperatorOfNumber.subtract(ob1[0],ob2[4],true);
        Assert.assertTrue("SUB CASE3",result instanceof Integer && result.equals(-2147483648));
        //Integer转Long Ori:2147483648 Long True:2147483648
        result = OperatorOfNumber.subtract(ob1[1],ob2[6],true);
        Assert.assertTrue("SUB CASE4",result instanceof Long && result.equals(2147483648L));
        //Long测试Float Ori:-9223372036854775808.5 BigDecimal True:-9223372036854775808.5
        result = OperatorOfNumber.subtract(ob1[2],ob2[1],true);
        Assert.assertTrue("SUB CASE5",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).subtract(new BigDecimal(0.5))));
        //Long溢出 Ori:9223372036854775807 Long False:-9223372036854775809 BigDecimal
        result = OperatorOfNumber.subtract(ob1[2],ob2[5],true);
        Assert.assertTrue("SUB CASE6",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).subtract(new BigDecimal(1L))));
        //Long测试Double Ori:9223372036854775807.5 BigDecimal True:9223372036854775807.5
        result = OperatorOfNumber.subtract(ob1[3],ob2[2],true);
        Assert.assertTrue("SUB CASE7",result instanceof BigDecimal && result.equals(new BigDecimal(9223372036854775807L).add(new BigDecimal(0.5))));
        //溢出 Ori:1 Integer False:-9223372036854775808-9223372036854775807 BigDecimal
        result = OperatorOfNumber.subtract(ob1[4],ob2[11],true);
        Assert.assertTrue("SUB CASE8",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).subtract(new BigDecimal(9223372036854775807L))));
    }
    @Test
    public void multiply() throws Exception {
        //-2147483648,2147483647,-9223372036854775808,9223372036854775807
        Object[] ob1 = {Integer.MIN_VALUE,Integer.MAX_VALUE,Long.MIN_VALUE,Long.MAX_VALUE,new BigDecimal(Long.MIN_VALUE)};
        Object[] ob2 = {-0.5f,0.5f,-0.5d,0.5d,0,1,-1,1.0f,-1.0d,1.3f,-1.3d,2,-2,new BigDecimal(2L)};

        Object result = null;
        //Integer测试Float 1073741824.0 BigDecimal True:1073741824.0
        result = OperatorOfNumber.multiply(ob1[0],ob2[0],true);
        Assert.assertTrue("MUL CASE1",result instanceof BigDecimal && result.equals(new BigDecimal(1073741824.0).setScale(1)));
        //Integer测试Double -1073741824.0 BigDecimal True:-1073741824.0
        result = OperatorOfNumber.multiply(ob1[0],ob2[3],true);
        Assert.assertTrue("MUL CASE2",result instanceof BigDecimal && result.equals(new BigDecimal(-1073741824.0).setScale(1)));
        //0值 0 Integer True:0
        result = OperatorOfNumber.multiply(ob1[0],ob2[4],true);
        Assert.assertTrue("MUL CASE3",result instanceof Integer && result.equals(0));
        //Integer转Long Ori:4294967294 Long True:4294967294
        result = OperatorOfNumber.multiply(ob1[1],ob2[11],true);
        //Long测试Float Ori:-4611686018427387904.0 BigDecimal True:-4611686018427387904.0
        Assert.assertTrue("MUL CASE5",result instanceof Long && result.equals(4294967294L));
        result = OperatorOfNumber.multiply(ob1[2],ob2[1],true);
        Assert.assertTrue("MUL CASE6",result instanceof BigDecimal && result.equals(new BigDecimal(-4611686018427387904.0).setScale(1)));
        //Long溢出 Ori:0 Long False:-9223372036854775808*2 BigDecimal
        result = OperatorOfNumber.multiply(ob1[2],ob2[11],true);
        Assert.assertTrue("MUL CASE7",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).multiply(new BigDecimal(2))));
        //Long溢出 Ori:0 Integer False:-9223372036854775808*2 BigDecimal
        result = OperatorOfNumber.multiply(ob1[4],ob2[13],true);
        Assert.assertTrue("MUL CASE8",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).multiply(new BigDecimal(2))));
    }
    @Test
    public void divide() throws Exception {
        //-2147483648,2147483647,-9223372036854775808,9223372036854775807
        Object[] ob1 = {Integer.MIN_VALUE,Integer.MAX_VALUE,Long.MIN_VALUE,Long.MAX_VALUE,new BigDecimal(Long.MAX_VALUE)};
        Object[] ob2 = {-0.5f,0.5f,-0.5d,0.5d,0,1,-1,1.0f,-1.0d,1.3f,-1.3d,2,-2,new BigDecimal(2L),888888};

        Object result = null;
        //Integer测试Float 4294967296.0000000000 BigDecimal True:4294967296.0000000000
        result = OperatorOfNumber.divide(ob1[0],ob2[0],true);
        Assert.assertTrue("DIV CASE1",result instanceof BigDecimal && result.equals(new BigDecimal(4294967296.0000000000).setScale(10)));
        //Integer测试Double -4294967296.0000000000 BigDecimal True:-4294967296.0000000000
        result = OperatorOfNumber.divide(ob1[0],ob2[3],true);
        Assert.assertTrue("DIV CASE2",result instanceof BigDecimal && result.equals(new BigDecimal(-4294967296.0000000000).setScale(10)));
        //Long转Integer Ori:-18446744073709551616.0000000000 Long BigDecimal:-18446744073709551616.0000000000
        result = OperatorOfNumber.divide(ob1[2],ob2[1],true);
        Assert.assertTrue("DIV CASE3",result instanceof BigDecimal && result.equals(new BigDecimal(-9223372036854775808L).divide(new BigDecimal(0.5)).setScale(10)));
        //Long测试Float Ori:4611686018427387903.5000000000 BigDecimal True:4611686018427387903.5000000000
        result = OperatorOfNumber.divide(ob1[4],ob2[13],true);
        Assert.assertTrue("DIV CASE4",result instanceof BigDecimal && result.equals(new BigDecimal(9223372036854775807L).divide(new BigDecimal(2)).setScale(10)));
    }
    @Test
    public void modulo() throws Exception {
        //-2147483648,2147483647,-9223372036854775808,9223372036854775807
        Object[] ob1 = {Integer.MIN_VALUE,Integer.MAX_VALUE,Long.MIN_VALUE,Long.MAX_VALUE,new BigDecimal(Long.MAX_VALUE)};
        Object[] ob2 = {-0.5f,0.5f,-0.5d,0.5d,0,1,-1,1.0f,-1.0d,1.3f,-1.3d,2,-2,new BigDecimal(2L),888888};

        Object result = null;
        //Integer -819128 Integer True:-819128
        result = OperatorOfNumber.modulo(ob1[0],ob2[14]);
        Assert.assertTrue("MOD CASE1",result instanceof Integer && result.equals(-819128));
        //Long -480488 Long True:-480488
        result = OperatorOfNumber.modulo(ob1[2],ob2[14]);
        Assert.assertTrue("MOD CASE2",result instanceof Long && result.equals(-480488L));
    }



    @Test
    public void systemCaseRunTime() throws Exception{
        String evalExpress = "a*2";

        final ExpressRunner runner = new ExpressRunner(true, false);

        DefaultContext<String, Object> nameMap = new DefaultContext<String, Object>();

        nameMap.put("a", new BigDecimal(Long.MAX_VALUE));

        long timeStart = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++){
            Object obj = runner.execute(evalExpress, nameMap, null, false, false);
        }
        System.out.println(System.currentTimeMillis()-timeStart);
    }
}
