package com.ql.util.express;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

public class ExpressClassLoader extends ClassLoader {
	public ExpressClassLoader(ClassLoader parent){
		super(parent);
	}
	public Class<?> loadClass(String name, byte[] code) {
		return this.defineClass(name, code, 0, code.length);
	}

	public synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		//System.out.print("Start to find class" + name + "。。。。。。。。。。。");
		Class<?> clasz = findLoadedClass(this, name);
		if (clasz != null) {
			//System.out.println(clasz.getClassLoader());
			return clasz;
		}
		if (clasz == null) {
			clasz = parentLoadClass(this, name);
		}
		if (clasz == null && name.startsWith("[")) { // Array processing
			int index = name.indexOf("L");
			String str = name.substring(0, index);
			String componentClassName = name.substring(index + 1,
					name.length() - 1);
			int[] dimes = new int[str.length()];
			for (int i = 0; i < dimes.length; i++) {
				dimes[i] = 0;
			}
			try {
				Class<?> componentType = this.loadClass(componentClassName);
				clasz = Array.newInstance(componentType, dimes).getClass();
			} catch (Exception e) {
				// Good deal
			}
		}

		if (clasz == null)
			throw new ClassNotFoundException(name);
		return clasz;
	}

	public static Class<?> findLoadedClass(ClassLoader loader, String name)
			throws ClassNotFoundException {
		Method m = null;
		try {
			m = ClassLoader.class.getDeclaredMethod("findLoadedClass",
					new Class[] { String.class });
			m.setAccessible(true);
			Class<?> result = (Class<?>) m.invoke(loader, new Object[] { name });
			if (result == null) {
				result = (Class<?>) m.invoke(loader.getClass().getClassLoader(),
						new Object[] { name });
			}
			if (result == null) {
				result = (Class<?>) m.invoke(Thread.currentThread()
						.getContextClassLoader(), new Object[] { name });
			}
			return result;
		} catch (Exception ex) {
			throw new ClassNotFoundException(ex.getMessage());
		} finally {
			if (m != null) {
				m.setAccessible(false);
			}
		}
	}

	public static Class<?> parentLoadClass(ClassLoader loader, String name)
			throws ClassNotFoundException {
		// If this class exists, return directly
		Class<?> clasz = null;
		if (clasz == null) {
			try {
				clasz = loader.getClass().getClassLoader().loadClass(name);
			} catch (Throwable e) {
			}
		}
		if (clasz == null)
			try {
				clasz = Thread.currentThread().getContextClassLoader()
						.loadClass(name);
			} catch (Throwable e) {
			}
		return clasz;
	}
}
