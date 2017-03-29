package com.ql.util.express.bugfix;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializeTest {

    @Test
    public void Test() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        try {
            /*
             * 添加一个规则方法
             * */
            runner.addFunctionOfClassMethod("getS", TestExpress.class.getName(),
                    "getScore", new String[]{TestData.class.getName()}, null);
            /*
             * 添加一个规则宏
             * */
            runner.addMacro("获取分数宏", "getS(testInfo)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] bytes1 = null;
        byte[] bytes2 = null;
        byte[] bytes3 = null;

        String express1 = "获取分数宏";
        String express2 = "getS(testInfo)";
        String express3 = "int score = getS(testInfo)";
        
        /*
         * 根据两种不同的规则文本获取指令集
         * */
        InstructionSet is1 = runner.parseInstructionSet(express1);
        InstructionSet is2 = runner.parseInstructionSet(express2);
        InstructionSet is3 = runner.parseInstructionSet(express3);

        /*
         * 对指令集进行序列化
         * */
        testSerialize(is1);
        testSerialize(is2);
        testSerialize(is3);

    }
    
    private void testSerialize(InstructionSet is)
    {
        int len = is.getInstructionLength();
        for(int i=0;i<len;i++){
            System.out.println(is.getInstruction(i));
            encode(is.getInstruction(i));
        }
        
    }

    private byte[] encode(Object object) {
        byte[] result = null;
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
    
    public class TestExpress implements Serializable{
        
        public int getScore(TestData testInfo) {
            if (!"None".equals(testInfo.getName()))
                return 600;
            return 300;
        }
    }
    
    public class TestData {
        
        private String name = "None";
        
        public TestData() {
        }
        
        TestData(String name) {
            this.name = name;
        }
        
        public String getName() {
            return this.name;
        }
    }

}


