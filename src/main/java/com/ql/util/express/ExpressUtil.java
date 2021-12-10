package com.ql.util.express;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ql.util.express.annotation.QLAlias;
import com.ql.util.express.annotation.QLAliasUtils;
import com.ql.util.express.config.QLExpressRunStrategy;
import com.ql.util.express.exception.QLException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * 表达式工具类
 *
 * @author qhlhl2010@gmail.com
 */
@SuppressWarnings("unchecked")
public class ExpressUtil {
    public static final String DT_STRING = "String";
    public static final String DT_SHORT = "Short";
    public static final String DT_INTEGER = "Integer";
    public static final String DT_LONG = "Long";
    public static final String DT_DOUBLE = "Double";
    public static final String DT_FLOAT = "Float";
    public static final String DT_BYTE = "Byte";
    public static final String DT_CHAR = "Char";
    public static final String DT_BOOLEAN = "Boolean";
    public static final String DT_DATE = "Date";
    public static final String DT_TIME = "Time";
    public static final String DT_DATETIME = "DateTime";
    public static final String DT_OBJECT = "Object";

    public static final String DT_short = "short";
    public static final String DT_int = "int";
    public static final String DT_long = "long";
    public static final String DT_double = "double";
    public static final String DT_float = "float";
    public static final String DT_byte = "byte";
    public static final String DT_char = "char";
    public static final String DT_boolean = "boolean";

    public static Map<String, Object> methodCache = new ConcurrentHashMap<>();

    public static Class<?>[][] classMatchs = new Class[][] {
        //原始数据类型
        {BigDecimal.class, double.class}, {BigDecimal.class, float.class}, {BigDecimal.class, long.class},
        {BigDecimal.class, int.class}, {BigDecimal.class, short.class}, {BigDecimal.class, byte.class},
        {double.class, float.class}, {double.class, long.class}, {double.class, int.class}, {double.class, short.class},
        {double.class, byte.class}, {double.class, BigDecimal.class},
        {float.class, long.class}, {float.class, int.class}, {float.class, short.class}, {float.class, byte.class},
        {float.class, BigDecimal.class},
        {long.class, int.class}, {long.class, short.class}, {long.class, byte.class},
        {int.class, short.class}, {int.class, byte.class},
        {short.class, byte.class},
        //---------
        {char.class, Character.class}, {Character.class, char.class},
        {boolean.class, Boolean.class}, {Boolean.class, boolean.class}
    };

    public static Class<?> getSimpleDataType(Class<?> aClass) {
        if (aClass.isPrimitive()) {
            if (Integer.class.equals(aClass)) {
                return Integer.TYPE;
            }
            if (Short.class.equals(aClass)) {
                return Short.TYPE;
            }
            if (Long.class.equals(aClass)) {
                return Long.TYPE;
            }
            if (Double.class.equals(aClass)) {
                return Double.TYPE;
            }
            if (Float.class.equals(aClass)) {
                return Float.TYPE;
            }
            if (Byte.class.equals(aClass)) {
                return Byte.TYPE;
            }
            if (Character.class.equals(aClass)) {
                return Character.TYPE;
            }
            if (Boolean.class.equals(aClass)) {
                return Boolean.TYPE;
            }
            return aClass;
        } else {
            return aClass;
        }
    }

    public static boolean isAssignable(Class<?> target, Class<?> source) {
        if (target == source) {
            return true;
        }
        if (target.isArray() && source.isArray()) {
            return isAssignable(target.getComponentType(), source.getComponentType());
        }
        return isAssignablePrivate(target, source);
    }

    public static boolean isAssignablePrivate(Class<?> target, Class<?> source) {
        if (target == source) {
            return true;
        }

        if (target == null) {
            return false;
        }
        if (source == null)//null转换
        {return !target.isPrimitive();}

        if (target.isAssignableFrom(source)) {
            return true;
        }
        if (source.isPrimitive() && target == Object.class) {
            return true;
        }

        if (!target.isPrimitive()) {
            if (target == Byte.class) {
                target = byte.class;
            } else if (target == Short.class) {
                target = short.class;
            } else if (target == Integer.class) {
                target = int.class;
            } else if (target == Long.class) {
                target = long.class;
            } else if (target == Float.class) {
                target = float.class;
            } else if (target == Double.class) {
                target = double.class;
            }
        }
        if (!source.isPrimitive()) {
            if (source == Byte.class) {
                source = byte.class;
            } else if (source == Short.class) {
                source = short.class;
            } else if (source == Integer.class) {
                source = int.class;
            } else if (source == Long.class) {
                source = long.class;
            } else if (source == Float.class) {
                source = float.class;
            } else if (source == Double.class) {
                source = double.class;
            }
        }
        // 转换后需要在判断一下
        if (target == source) {
            return true;
        }

        // QLambda 与函数式接口之间允许互转
        if (source == QLambda.class && isFunctionInterface(target)) {
            return true;
        }

        for (int i = 0; i < classMatchs.length; i++) {
            if (target == classMatchs[i][0] && source == classMatchs[i][1]) {
                return true;
            }
        }

        return false;
    }

    public static boolean isAssignableOld(Class<?> lhsType, Class<?> rhsType) {
        if (lhsType == null) {
            return false;
        }
        if (rhsType == null) {
            return !lhsType.isPrimitive();
        }

        if (lhsType.isPrimitive() && rhsType.isPrimitive()) {
            if (lhsType == rhsType) {
                return true;
            }

            if ((rhsType == Byte.TYPE) && (lhsType == Short.TYPE || lhsType == Integer.TYPE || lhsType == Long.TYPE
                || lhsType == Float.TYPE || lhsType == Double.TYPE)) {
                return true;
            }

            if ((rhsType == Short.TYPE) && (lhsType == Integer.TYPE || lhsType == Long.TYPE || lhsType == Float.TYPE
                || lhsType == Double.TYPE)) {
                return true;
            }

            if ((rhsType == Character.TYPE) && (lhsType == Integer.TYPE || lhsType == Long.TYPE || lhsType == Float.TYPE
                || lhsType == Double.TYPE)) {
                return true;
            }

            if ((rhsType == Integer.TYPE) && (lhsType == Long.TYPE || lhsType == Float.TYPE
                || lhsType == Double.TYPE)) {
                return true;
            }

            if ((rhsType == Long.TYPE) && (lhsType == Float.TYPE || lhsType == Double.TYPE)) {
                return true;
            }

            if ((rhsType == Float.TYPE) && (lhsType == Double.TYPE)) {
                return true;
            }
        } else if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }
        return false;
    }

    public static boolean isSignatureAssignable(Class<?>[] from, Class<?>[] to) {
        for (int i = 0; i < from.length; i++) {
            if (!isAssignable(to[i], from[i])) {
                return false;
            }
        }
        return true;
    }

    public static int findMostSpecificSignature(Class<?>[] idealMatch,
        Class<?>[][] candidates) {
        Class<?>[] bestMatch = null;
        int bestMatchIndex = -1;

        for (int i = candidates.length - 1; i >= 0; i--) {// 先从基类开始查找
            Class<?>[] targetMatch = candidates[i];
            if (ExpressUtil.isSignatureAssignable(idealMatch, targetMatch) && ((bestMatch == null)
                || ExpressUtil.isSignatureAssignable(targetMatch, bestMatch))) {
                bestMatch = targetMatch;
                bestMatchIndex = i;
            }
        }

        if (bestMatch != null) {
            return bestMatchIndex;
        } else {
            return -1;
        }
    }

    public static String createCacheKey(Class<?> aBaseClass, String aMethodName, Class<?>[] aTypes, boolean aPublicOnly,
        boolean aIsStatic) {
        StringBuilder builder = new StringBuilder();
        //builder.append(aPublicOnly).append("-").append(aIsStatic).append("-");
        builder.append(aBaseClass.getName()).append(".").append(aMethodName).append(".");
        if (aTypes == null) {
            builder.append("null");
        } else {
            for (int i = 0; i < aTypes.length; i++) {
                if (i > 0) {
                    builder.append(",");
                }
                if (aTypes[i] == null) {
                    builder.append("null");
                } else {
                    builder.append(aTypes[i].getName());
                }
            }
        }
        //builder.append(")");
        return builder.toString();
    }

    public static Method findMethodWithCache(Class<?> baseClass, String methodName,
        Class<?>[] types, boolean publicOnly, boolean isStatic) {
        String key = createCacheKey(baseClass, methodName, types, publicOnly, isStatic);
        Object result = methodCache.get(key);
        if (result == null) {
            result = findMethod(baseClass, methodName, types, publicOnly, isStatic);
            if (result == null) {
                methodCache.put(key, void.class);
            } else {
                ((Method)result).setAccessible(true);
                methodCache.put(key, result);
            }
        } else if (result == void.class) {
            result = null;
        }
        return (Method)result;
    }

    public static Method findMethod(Class<?> baseClass, String methodName, Class<?>[] types, boolean publicOnly,
        boolean isStatic) {
        List<Method> candidates = gatherMethodsRecursive(baseClass, methodName, types.length, publicOnly, isStatic,
            null /* candidates */);
        return findMostSpecificMethod(types, candidates.toArray(new Method[0]));
    }

    public static Constructor<?> findConstructorWithCache(Class<?> baseClass, Class<?>[] types) {
        String key = createCacheKey(baseClass, "new", types, true, false);
        Constructor<?> result = (Constructor<?>)methodCache.get(key);
        if (result == null) {
            result = findConstructor(baseClass, types);
            methodCache.put(key, result);
        }
        return result;
    }

    private static Constructor<?> findConstructor(Class<?> baseClass, Class<?>[] types) {
        Constructor<?>[] constructors = baseClass.getConstructors();
        List<Constructor<?>> constructorList = new ArrayList<>();
        List<Class<?>[]> listClass = new ArrayList<>();
        for (int i = 0; i < constructors.length; i++) {
            if (constructors[i].getParameterTypes().length == types.length) {
                listClass.add(constructors[i].getParameterTypes());
                constructorList.add(constructors[i]);
            }
        }

        int match = findMostSpecificSignature(types, listClass
            .toArray(new Class[0][]));
        return match == -1 ? null : constructorList.get(match);
    }

    public static Method findMostSpecificMethod(Class<?>[] idealMatch,
        Method[] methods) {
        Class<?>[][] candidateSigs = new Class[methods.length][];
        for (int i = 0; i < methods.length; i++) {candidateSigs[i] = methods[i].getParameterTypes();}

        int match = findMostSpecificSignature(idealMatch, candidateSigs);
        return match == -1 ? null : methods[match];
    }

    private static List<Method> gatherMethodsRecursive(Class<?> baseClass, String methodName, int numArgs,
        boolean publicOnly, boolean isStatic, List<Method> candidates) {
        if (candidates == null) {
            candidates = new ArrayList<>();
        }

        addCandidates(baseClass.getDeclaredMethods(), methodName, numArgs, publicOnly, isStatic, candidates);

        Class<?>[] intfs = baseClass.getInterfaces();
        for (int i = 0; i < intfs.length; i++) {
            gatherMethodsRecursive(intfs[i], methodName, numArgs, publicOnly, isStatic, candidates);
        }

        Class<?> superclass = baseClass.getSuperclass();
        if (superclass != null) {
            gatherMethodsRecursive(superclass, methodName, numArgs, publicOnly,
                isStatic, candidates);
        }

        return candidates;
    }

    private static List<Method> addCandidates(Method[] methods, String methodName,
        int numArgs, boolean publicOnly, boolean isStatic, List<Method> candidates) {
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (m.getName().equals(methodName)
                && (m.getParameterTypes().length == numArgs)
                && (!publicOnly || isPublic(m)
                && (!isStatic || isStatic(m)))) {
                candidates.add(m);
            } else if (m.isAnnotationPresent(QLAlias.class)) {
                String[] values = m.getAnnotation(QLAlias.class).value();
                if (values.length > 0) {
                    for (int j = 0; j < values.length; j++) {
                        if (values[j].equals(methodName) && (m.getParameterTypes().length == numArgs)
                            && (!publicOnly || isPublic(m)
                            && (!isStatic || isStatic(m)))) {candidates.add(m);}
                    }
                }
            }
        }
        return candidates;
    }

    public static boolean isPublic(Class<?> c) {
        return Modifier.isPublic(c.getModifiers());
    }

    public static boolean isPublic(Method m) {
        return Modifier.isPublic(m.getModifiers());
    }

    public static boolean isStatic(Method m) {
        return Modifier.isStatic(m.getModifiers());
    }

    public static Class<?> getJavaClass(String type) {
        int index = type.indexOf("[]");
        if (index < 0) {
            return getJavaClassInner(type);
        }

        StringBuilder arrayString = new StringBuilder();
        arrayString.append("[");
        String baseType = type.substring(0, index);
        while ((index = type.indexOf("[]", index + 2)) >= 0) {
            arrayString.append("[");
        }
        Class<?> baseClass = getJavaClassInner(baseType);

        try {
            String baseName = "";
            if (!baseClass.isPrimitive()) {
                return loadClass(arrayString + "L"
                    + baseClass.getName() + ";");
            } else {
                if (baseClass.equals(boolean.class)) {
                    baseName = "Z";
                } else if (baseClass.equals(byte.class)) {
                    baseName = "B";
                } else if (baseClass.equals(char.class)) {
                    baseName = "C";
                } else if (baseClass.equals(double.class)) {
                    baseName = "D";
                } else if (baseClass.equals(float.class)) {
                    baseName = "F";
                } else if (baseClass.equals(int.class)) {
                    baseName = "I";
                } else if (baseClass.equals(long.class)) {
                    baseName = "J";
                } else if (baseClass.equals(short.class)) {
                    baseName = "S";
                }
                return loadClass(arrayString + baseName);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Class<?> getJavaClassInner(String type) {
        if (type.equals(DT_STRING)) {
            return String.class;
        }
        if (type.equals(DT_SHORT)) {
            return Short.class;
        }
        if (type.equals(DT_INTEGER)) {
            return Integer.class;
        }
        if (type.equals(DT_LONG)) {
            return Long.class;
        }
        if (type.equals(DT_DOUBLE)) {
            return Double.class;
        }
        if (type.equals(DT_FLOAT)) {
            return Float.class;
        }
        if (type.equals(DT_BYTE)) {
            return Byte.class;
        }
        if (type.equals(DT_CHAR) || type.equals("Character")) {
            return Character.class;
        }
        if (type.equals(DT_BOOLEAN)) {
            return Boolean.class;
        }
        if (type.equals(DT_DATE)) {
            return Date.class;
        }
        if (type.equals(DT_TIME)) {
            return Time.class;
        }
        if (type.equals(DT_DATETIME)) {
            return Timestamp.class;
        }
        if (type.equals(DT_OBJECT)) {
            return Object.class;
        }
        if (type.equals(DT_short)) {
            return short.class;
        }
        if (type.equals(DT_int)) {
            return int.class;
        }
        if (type.equals(DT_long)) {
            return long.class;
        }
        if (type.equals(DT_double)) {
            return double.class;
        }
        if (type.equals(DT_float)) {
            return float.class;
        }
        if (type.equals(DT_byte)) {
            return byte.class;
        }
        if (type.equals(DT_char)) {
            return char.class;
        }
        if (type.equals(DT_boolean)) {
            return boolean.class;
        }
        try {
            return loadClass(type);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getClassName(Class<?> className) {
        if (className == null) {
            return null;
        }
        String name = className.getName();
        return getClassName(name);
    }

    private static String getClassName(String name) {
        String arrays = "";
        if (name.indexOf("[") >= 0) {
            int point = 0;
            while (name.charAt(point) == '[') {
                arrays = arrays + "[]";
                ++point;
            }
            if (name.charAt(point) == 'L') {
                name = name.substring(point + 1, name.length() - 1);
            } else if (name.charAt(point) == 'Z') {
                name = "boolean";
            } else if (name.charAt(point) == 'B') {
                name = "byte";
            } else if (name.charAt(point) == 'C') {
                name = "char";
            } else if (name.charAt(point) == 'D') {
                name = "double";
            } else if (name.charAt(point) == 'F') {
                name = "float";
            } else if (name.charAt(point) == 'I') {
                name = "int";
            } else if (name.charAt(point) == 'J') {
                name = "long";
            } else if (name.charAt(point) == 'S') {
                name = "short";
            }
        }
        int index = name.lastIndexOf('.');
        if (index > 0 && name.substring(0, index).equals("java.lang")) {
            name = name.substring(index + 1);
        }
        name = name + arrays;
        return name;
    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    /**
     * 替换字符串中的参数 replaceString("$1强化$2实施$2",new String[]{"qq","ff"})
     * ="qq 强化 ff 实施 ff"
     *
     * @param str
     * @param parameters
     * @return
     * @throws Exception
     */
    public static String replaceString(String str, Object[] parameters)
        throws Exception {
        if (str == null || parameters == null || parameters.length == 0) {
            return str;
        }
        Pattern p = Pattern.compile("\\$\\d+");
        Matcher m = p.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int index = Integer.parseInt(m.group().substring(1)) - 1;
            if (index < 0 || index >= parameters.length) {
                throw new QLException("设置的参数位置$" + (index + 1) + "超过了范围 "
                    + parameters.length);
            }
            m.appendReplacement(sb, " " + parameters[index].toString() + " ");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public static Object getProperty(Object bean, Object name) {
        try {
            if (bean == null && QLExpressRunStrategy.isAvoidNullPointer()) {
                return null;
            }
            if (bean.getClass().isArray() && name.equals("length")) {
                return Array.getLength(bean);
            } else if (bean instanceof Class) {
                if (name.equals("class")) {
                    return bean;
                } else {
                    Field f = ((Class<?>)bean).getDeclaredField(name.toString());
                    return f.get(null);
                }
            } else if (bean instanceof Map) {
                return ((Map<?, ?>)bean).get(name);
            } else {
                return QLAliasUtils.getProperty(bean, name.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Class<?> getPropertyClass(Object bean, Object name) {
        try {
            if (bean.getClass().isArray() && name.equals("length")) {
                return int.class;
            } else if (bean instanceof Class) {
                if (name.equals("class")) {
                    return Class.class;
                } else {
                    Field f = ((Class<?>)bean).getDeclaredField(name.toString());
                    return f.getType();
                }
            } else if (bean instanceof Map) {
                Object o = ((Map<?, ?>)bean).get(name);
                if (o == null) {
                    return null;
                } else {
                    return o.getClass();
                }
            } else {
                return QLAliasUtils.getPropertyClass(bean, name.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setProperty(Object bean, Object name, Object value) {
        try {
            if (bean instanceof Class) {
                Field f = ((Class<?>)bean).getDeclaredField(name.toString());
                f.set(null, value);
            } else if (bean instanceof Map) {
                ((Map<Object, Object>)bean).put(name, value);
            } else {
                Class<?> filedClass = PropertyUtils.getPropertyType(bean, name.toString());
                PropertyUtils.setProperty(bean, name.toString(), ExpressUtil.castObject(value, filedClass, false));
            }
        } catch (Exception e) {
            throw new RuntimeException("不能访问" + bean + "的property:" + name, e);
        }
    }

    public static Object[] transferArray(Object[] values, Class<?>[] types) {
        for (int i = 0; i < values.length; i++) {
            values[i] = castObject(values[i], types[i], false);
        }
        return values;
    }

    /**
     * @param value
     * @param type
     * @param isForce 是否强制转换
     * @return
     */
    public static Object castObject(Object value, Class<?> type, boolean isForce) {
        if (value == null) {
            return null;
        }
        if (value.getClass() == type || type.isAssignableFrom(value.getClass())) {
            return value;
        }
        if (value instanceof Number
            && (type.isPrimitive() || Number.class.isAssignableFrom(type))) {
            return OperatorOfNumber.transfer((Number)value, type, isForce);
        } else if (type.isArray() && value.getClass().isArray()) {
            //需要对元素做兼容性,如果value的元素全部为null并且和声明的不一致,转化为所声明的类型
            Class<?> valueType = value.getClass().getComponentType();
            Class<?> declareType = type.getComponentType();
            if (declareType != valueType) {
                Object[] values = (Object[])value;
                boolean allBlank = true;
                for (int i = 0; i < values.length; i++) {
                    if (values[i] != null) {
                        allBlank = false;
                        break;
                    }
                }
                if (allBlank) {
                    return Array.newInstance(declareType, values.length);
                }
            }
            return value;

        } else if (value.getClass() == QLambda.class && isFunctionInterface(type)) {
            // 动态代理 QLambda 为指定接口类
            return Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] {type},
                new QLambdaInvocationHandler((QLambda)value));
        } else {
            return value;
        }
    }

    /**
     * 一个接口是否函数式接口的缓存
     */
    private static final Map<Class<?>, Boolean> IS_FUNCTION_INTERFACE_CACHE
        = new ConcurrentHashMap<>();

    /**
     * 是否函数式接口
     * 函数式接口的条件
     * 是接口
     * 有且仅有一个 abstract 方法
     *
     * @param clazz
     * @return
     */
    private static boolean isFunctionInterface(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        Boolean cacheRes = IS_FUNCTION_INTERFACE_CACHE.get(clazz);
        if (cacheRes != null) {
            return cacheRes;
        }
        boolean res = clazz.isInterface() && hasOnlyOneAbstractMethod(clazz.getMethods());
        IS_FUNCTION_INTERFACE_CACHE.put(clazz, res);
        return res;
    }

    private static boolean hasOnlyOneAbstractMethod(Method[] methods) {
        int count = 0;
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers())) {
                count++;
            }
        }
        return count == 1;
    }
}
