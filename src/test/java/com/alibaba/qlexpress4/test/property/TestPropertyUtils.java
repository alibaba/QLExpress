package com.alibaba.qlexpress4.test.property;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author TaoKan
 * @Date 2022/4/9 上午10:51
 */


public class TestPropertyUtils {

    public static void main(String[] args) throws Exception {
        Integer[][]os = new Integer[][]{new Integer[]{1,2}, new Integer[]{3,4}};
        assert(Arrays.equals(os[0],new Integer[]{1,2}));
        Method a = Arrays.class.getMethod("equals", Integer[].class, Integer[].class);
        a.invoke(Arrays.class,os[0], new Integer[]{1,2});
//          Integer a = Integer.MAX_VALUE;
//          System.out.println(a);
//        ExpressRunner expressRunner = new ExpressRunner();
//        DefaultContext<String,Object> defaultContext = new DefaultContext<>();
//        defaultContext.put("a",new Child9());
//        Object a = expressRunner.execute("return a.addField(1,\"5.5\",\"6\");",defaultContext,null, false, false);
//        System.out.println(a);
//         Optional<Integer> targetOp = Optional.ofNullable(1);
//         System.out.println(targetOp.orElse(3));
//          Object a = null;
//          System.out.println(Child7.class.toString());
//          System.out.println(StringConversion.trans(Child7.class).getCastValue());
//
//          Express4Runner express4Runner = new Express4Runner(InitOptions.builder().build());
//          Child10 as = new Child10();
//         express4Runner.addFunction("test", as::setAA);
//        express4Runner.addFunction("test", (Function<Integer[], Integer>) as::setAAA, Integer.class);
//        express4Runner.addFunction("test", (QLFunctionalVarargs<Object, Object>) as::setAAAA, String.class,Integer.class);
//        express4Runner.addFunction("test", as, "setAAAA");
//        express4Runner.addFunction("test", Math.class, "max");

//        express4Runner.addFunction("test",as::add);

//        BigInteger b = new BigInteger("6");
//        QLConvertResult q = NumberConversion.trans(b,Integer.class);
//        System.out.println(q.getCastValue());
//         Integer a = 5;
//        Child5 parent = new Child5();
//
//          Method method = parent.getClass().getMethod("getMethod8",double.class);
//        System.out.println(method.invoke(parent,5));
//        CacheUtil.initCache(128, false);
//
//        Child child = new Child();
//        Method m = MethodHandler.getGetter(Child.class,"parentOwn");
//        System.out.println(m);
//        // getPropertyValue private field - non get
//        Assert.assertNull(PropertiesUtil.getPropertyValue(parent, "name", false));
//        Assert.assertNull(PropertiesUtil.getPropertyType(parent, "name"));
//        Assert.assertNull(PropertiesUtil.getClzField(Parent.class, "name", false));
//        Assert.assertTrue(PropertiesUtil.getClzField(Parent.class, "staticPublic", false).equals("staticPublic"));
//        Assert.assertNull(PropertiesUtil.getClzField(Parent.class, "staticPrivate", false));
//        Assert.assertTrue(PropertiesUtil.getClzField(Parent.class, "staticGet", false).equals("staticGet"));
//        // getPropertyValue private field - public get
//        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(parent, "age", false) == 35);
//        Assert.assertTrue(PropertiesUtil.getPropertyType(parent, "age") == int.class);
//        // getPropertyValue public field
//        Assert.assertTrue(PropertiesUtil.getPropertyValue(parent, "sex", false).equals("man"));
//        Assert.assertTrue(PropertiesUtil.getPropertyValue(parent, "生日", false).equals("2022-01-01"));
//
//        Assert.assertTrue(PropertiesUtil.getPropertyType(parent, "sex").equals(String.class));
//        Assert.assertTrue(PropertiesUtil.getPropertyType(Parent.class, "staticPublic").equals(java.lang.String.class));
//        PropertiesUtil.setPropertyValue(parent, "age", 15, false);
//        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(parent, "age", false) == 15);
//
//        List<Method> method1 = PropertiesUtil.getMethod(parent, "getWork", false);
//        Assert.assertTrue(method1.size() == 1);
//
//        Parent pc = new Child();
//        pc.setAge(35);
//        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(pc, "age", false) == 35);
//        Assert.assertTrue(PropertiesUtil.getPropertyType(pc, "age") == int.class);
//
//        Child c = new Child();
//        c.setAge(35);
//        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "work", false).equals("child"));
//        Assert.assertTrue((int) PropertiesUtil.getPropertyValue(c, "age", false) == 35);
//        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "sex", false).equals("man"));
//        Assert.assertTrue(PropertiesUtil.getPropertyType(c, "age") == int.class);
//        List<Method> method3 = PropertiesUtil.getMethod(c, "getWork", false);
//        Assert.assertTrue(method3.size() == 2);
//
//        List<Method> method4 = PropertiesUtil.getClzMethod(Child.class, "findStatic");
//        Assert.assertTrue(method4.size() == 1 && method4.get(0).getDeclaringClass().equals(com.alibaba.qlexpress4.test.property.Parent.class));
//
//        PropertiesUtil.setClzPropertyValue(Parent.class, "staticSet", "st1", false);
//        Assert.assertTrue(Parent.staticSet.equals("st1"));
//
//        Assert.assertTrue(PropertiesUtil.getPropertyValue(c, "booValue", false).equals(true));
    }
}
