package com.ql.util.express;

/**
 * 输出给其它指令共享使用的对象
 *
 * @author xuannan
 */
public class ExportItem {
    public static final String TYPE_ALIAS = "alias";
    public static final String TYPE_DEF = "def";
    public static final String TYPE_FUNCTION = "function";
    public static final String TYPE_MACRO = "macro";
    private String globeName;
    String name;

    /**
     * def, alias
     */
    private String type;

    /**
     * 类名或者别名
     */
    private String desc;

    public ExportItem(String name, String type, String desc) {
        this.globeName = name;
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    public ExportItem(String globeName, String name, String type, String desc) {
        this.globeName = globeName;
        this.name = name;
        this.type = type;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.globeName + "[" + this.type + ":" + this.name + " " + this.desc + "]";
    }

    public String getGlobeName() {
        return globeName;
    }

    public void setGlobeName(String globeName) {
        this.globeName = globeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
