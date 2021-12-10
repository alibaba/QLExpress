package com.ql.util.express.example;

import org.apache.commons.lang.StringUtils;

public class CustBean {

    long id;
    String name;
    int age;

    public CustBean(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public static String firstToUpper(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        value = StringUtils.trim(value);
        String f = StringUtils.substring(value, 0, 1);
        String s = "";
        if (value.length() > 1) {
            s = StringUtils.substring(value, 1);
        }
        return f.toUpperCase() + s;
    }
}
