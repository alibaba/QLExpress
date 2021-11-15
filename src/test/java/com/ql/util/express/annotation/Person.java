package com.ql.util.express.annotation;

/**
 * @Description
 * @Author tianqiao@come-future.com
 * @Date 2021-11-15 5:52 下午
 */
@QLAlias("患者")
public class Person {

    @QLAlias({"出生年月","生日"})
    private String birth = "1987-02-23";
    @QLAlias("姓名")
    private String name;
    @QLAlias("性别")
    private String sex;

    @QLAlias("获取年龄")
    public int getAge() {
        return 2021- Integer.valueOf(this.birth.substring(0,4));
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

    @QLAlias({"出生年月","生日"})
    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }
}
