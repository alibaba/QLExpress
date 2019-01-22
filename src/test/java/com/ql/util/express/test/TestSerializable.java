package com.ql.util.express.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.ql.util.express.ExpressRunner;
import com.ql.util.express.InstructionSet;

public class TestSerializable {

	@Test
	public void testSerializable() throws Exception {

		ExpressRunner runner = new ExpressRunner();
		InstructionSet staff = runner.parseInstructionSet("1+1");
		try {

			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream("target/qlcache.dat"));

			out.writeObject(staff);

			out.close();

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					"target/qlcache.dat"));

			InstructionSet newStaff = (InstructionSet) in.readObject();

			in.close();

			System.out.print(newStaff);

		}

		catch (Exception e)

		{

			throw e;

		}

	}
}
