package com.ql.util.express.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import org.junit.Ignore;
import org.junit.Test;

public class SerializableTest {
    @Test
    @Ignore("不在实现Serializable接口，因此不能序列化")
    public void testSerializable() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        InstructionSet instructionSet = runner.parseInstructionSet("1+1");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("target/qlcache.dat"));
        objectOutputStream.writeObject(instructionSet);
        objectOutputStream.close();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("target/qlcache.dat"));
        InstructionSet newInstructionSet = (InstructionSet)objectInputStream.readObject();
        objectInputStream.close();
        System.out.print(newInstructionSet);
    }
}
