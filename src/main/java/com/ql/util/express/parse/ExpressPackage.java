package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.ql.util.express.ExpressUtil;

public class ExpressPackage {
    private List<String> packages;
    private Map<String, Class<?>> name2CallCache = null;
    private static final Class<?> S_NULL = NullClass.class;
    private final ExpressPackage parent;

    public ExpressPackage(ExpressPackage parent) {
        this.parent = parent;
    }

    public void addPackage(String packageName) {
        if (this.packages == null) {
            this.packages = new ArrayList<>();
        }
        int point = packageName.indexOf(".*");
        if (point >= 0) {
            packageName = packageName.substring(0, point);
        }
        this.packages.add(packageName);
    }

    public Class<?> getClass(String name) {
        Class<?> tempClass = null;
        if (this.parent != null) {
            tempClass = this.parent.getClass(name);
        }
        if (tempClass == null) {
            if (this.packages == null && this.parent != null) {
                return null;
            }
            if (this.name2CallCache == null) {
                this.name2CallCache = new ConcurrentHashMap<>();
            } else {
                tempClass = this.name2CallCache.get(name);
            }
            if (tempClass == null) {
                tempClass = this.getClassInner(name, this.parent == null);
                if (tempClass == null) {
                    tempClass = S_NULL;
                }
            }
            this.name2CallCache.put(name, tempClass);
        }

        if (tempClass == S_NULL) {
            return null;
        } else {
            return tempClass;
        }
    }

    private Class<?> getClassInner(String name, boolean isRootCall) {
        Class<?> result = null;
        if (isRootCall) {
            // 如果本身具有包名，这直接定位
            if (name.contains(".")) {
                try {
                    result = ExpressUtil.loadClass(name);
                } catch (Throwable ignore) {
                }
                return result;
            }
            if (Integer.TYPE.getName().equals(name)) {
                return Integer.TYPE;
            }
            if (Short.TYPE.getName().equals(name)) {
                return Short.TYPE;
            }
            if (Long.TYPE.getName().equals(name)) {
                return Long.TYPE;
            }
            if (Double.TYPE.getName().equals(name)) {
                return Double.TYPE;
            }
            if (Float.TYPE.getName().equals(name)) {
                return Float.TYPE;
            }
            if (Byte.TYPE.getName().equals(name)) {
                return Byte.TYPE;
            }
            if (Character.TYPE.getName().equals(name)) {
                return Character.TYPE;
            }
            if (Boolean.TYPE.getName().equals(name)) {
                return Boolean.TYPE;
            }
        }
        if (this.packages != null) {
            for (String aPackage : packages) {
                String tmp;
                if (aPackage.endsWith("." + name)) {
                    tmp = aPackage;
                } else {
                    tmp = aPackage + "." + name;
                }
                try {
                    result = ExpressUtil.loadClass(tmp);
                } catch (ClassNotFoundException ex) {
                    // 不做任何操作
                }
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
}

class NullClass {
}
