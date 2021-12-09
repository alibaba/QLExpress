package com.ql.util.express.annotation;

/**
 * @author tianqiao@come-future.com
 * 2021-11-16 5:02 下午
 */
@QLAlias("患者")
public class Patient extends Person {

    @QLAlias("级别")
    private String level = "高危";

    @QLAlias("患者姓名")
    private String name;

    @QLAlias("获取患者年龄")
    public int getAge() {
        return 2021 - Integer.valueOf(this.getBirth().substring(0, 4));
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}
