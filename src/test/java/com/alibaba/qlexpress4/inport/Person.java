package com.alibaba.qlexpress4.inport;

/**
 * @author 冰够
 */
public class Person implements Comparable<Person> {
    private final int age;

    public Person(int age) {
        this.age = age;
    }

    @Override
    public int compareTo(Person o) {
        return Integer.compare(age, o.age);
    }
}
