package com.ql.util.express.annotation;

/**
 * @author tianqiao@come-future.com
 * 2021-11-15 5:52 下午
 */
public class Person {
    @QLAlias({"出生年月", "生日"})
    private String birth;

    @QLAlias("姓名")
    private String name;

    @QLAlias("性别")
    private String sex;

    @QLAlias("获取年龄")
    public int getAge() {
        return 2021 - Integer.parseInt(this.birth.substring(0, 4));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @QLAlias({"出生年月", "生日"})
    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
