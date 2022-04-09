package com.alibaba.qlexpress4.utils;

import com.ql.util.express.exception.QLException;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Character.toUpperCase;
import static java.lang.System.arraycopy;

/**
 * @Author TaoKan
 * @Date 2022/4/7 下午3:12
 */
public class BasicUtils {
    public static final String NULL_SIGN = "null";
    public static final Pattern PATTERN = Pattern.compile("\\$\\d+");

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
    public static final String DT_CHARACTER = "Character";

    public static final String DT_short = "short";
    public static final String DT_int = "int";
    public static final String DT_long = "long";
    public static final String DT_double = "double";
    public static final String DT_float = "float";
    public static final String DT_byte = "byte";
    public static final String DT_char = "char";
    public static final String DT_boolean = "boolean";

    public static final String LENGTH = "length";
    public static final String CLASS = "class";
    public static final String NEW = "new";

    public static final Class<?>[][] CLASS_MATCHES = new Class[][] {
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

    public static boolean isPublic(Member member) {
        return Modifier.isPublic(member.getModifiers());
    }

    public static boolean isStatic(Member member) {
        return Modifier.isStatic(member.getModifiers());
    }


    public static boolean isPreferredGetter(Method before, Method after, Map<String, Integer> map) {
        Class<?> beforeReturnType = before.getReturnType();
        Class<?> afterReturnType = after.getReturnType();
        if (beforeReturnType.equals(afterReturnType)) {
            return map.get(after.getName()) > map.get(before.getName());
        } else if (beforeReturnType.isAssignableFrom(afterReturnType)) {
            return true;
        } else {
            return false;
        }
    }


    public static String getGetter(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 3];
        chars[0] = 'g';
        chars[1] = 'e';
        chars[2] = 't';
        chars[3] = toUpperCase(c[0]);
        arraycopy(c, 1, chars, 4, c.length - 1);
        return new String(chars);
    }

    public static String getSetter(String s) {
        char[] chars = new char[s.length() + 3];
        chars[0] = 's';
        chars[1] = 'e';
        chars[2] = 't';
        chars[3] = toUpperCase(s.charAt(0));
        for (int i = s.length() - 1; i != 0; i--) {
            chars[i + 3] = s.charAt(i);
        }
        return new String(chars);
    }

    public static String getIsGetter(String s) {
        char[] c = s.toCharArray();
        char[] chars = new char[c.length + 2];
        chars[0] = 'i';
        chars[1] = 's';
        chars[2] = toUpperCase(c[0]);
        arraycopy(c, 1, chars, 3, c.length - 1);
        return new String(chars);
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

    public static Class<?> getSimpleDataType(Class<?> clazz) {
        if (!clazz.isPrimitive()) {
            return clazz;
        }
        if (Integer.class.equals(clazz)) {
            return Integer.TYPE;
        }
        if (Short.class.equals(clazz)) {
            return Short.TYPE;
        }
        if (Long.class.equals(clazz)) {
            return Long.TYPE;
        }
        if (Double.class.equals(clazz)) {
            return Double.TYPE;
        }
        if (Float.class.equals(clazz)) {
            return Float.TYPE;
        }
        if (Byte.class.equals(clazz)) {
            return Byte.TYPE;
        }
        if (Character.class.equals(clazz)) {
            return Character.TYPE;
        }
        if (Boolean.class.equals(clazz)) {
            return Boolean.TYPE;
        }
        return clazz;
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
        if (type.equals(DT_CHAR) || DT_CHARACTER.equals(type)) {
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
        StringBuilder arrays = new StringBuilder();
        if (name.contains("[")) {
            int point = 0;
            while (name.charAt(point) == '[') {
                arrays.append("[]");
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
        if (index > 0 && "java.lang".equals(name.substring(0, index))) {
            name = name.substring(index + 1);
        }
        name = name + arrays;
        return name;
    }

    public static Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }

    /**
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
        Matcher m = PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            int index = Integer.parseInt(m.group().substring(1)) - 1;
            if (index < 0 || index >= parameters.length) {
                throw new QLException("set param's location$" + (index + 1) + "out of range "
                        + parameters.length);
            }
            m.appendReplacement(sb, " " + parameters[index].toString() + " ");
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
