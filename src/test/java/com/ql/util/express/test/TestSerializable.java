package com.ql.util.express.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;
import org.junit.Ignore;
import org.junit.Test;

public class TestSerializable {
    @Test
    @Ignore // TODO：不在实现Serializable接口，因此不能序列化
    public void testSerializable() throws Exception {
        ExpressRunner runner = new ExpressRunner();
        InstructionSet staff = runner.parseInstructionSet("1+1");
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("target/qlcache.dat"));
        out.writeObject(staff);
        out.close();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("target/qlcache.dat"));
        InstructionSet newStaff = (InstructionSet)in.readObject();
        in.close();
        System.out.print(newStaff);
    }
}
