package com.ql.util.express.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressPackage {
    private List<String> m_packages;
    private Map<String, Class<?>> name2CallCache = null;
    private Class<?> S_NULL = NullClass.class;
    private ExpressPackage parent;

    public ExpressPackage(ExpressPackage aParent) {
        this.parent = aParent;
    }

    public void addPackage(String aPackageName) {
        if (this.m_packages == null) {
            this.m_packages = new ArrayList<>();
        }
        int point = aPackageName.indexOf(".*");
        if (point >= 0) {
            aPackageName = aPackageName.substring(0, point);
        }
        this.m_packages.add(aPackageName);
    }

    public void removePackage(String aPackageName) {
        if (this.m_packages != null) {
            this.m_packages.remove(aPackageName);
        }
    }

    public Class<?> getClass(String name) {
        Class<?> tempClass = null;
        if (this.parent != null) {
            tempClass = this.parent.getClass(name);
        }
        if (tempClass == null) {
            if (this.m_packages == null && this.parent != null) {
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
                    result = Class.forName(name);
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
        if (this.m_packages != null) {
            for (int i = 0; i < m_packages.size(); i++) {
                String tmp;
                if (m_packages.get(i).endsWith("." + name)) {
                    tmp = m_packages.get(i);
                } else {
                    tmp = m_packages.get(i) + "." + name;
                }
                try {
                    result = Class.forName(tmp);
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
