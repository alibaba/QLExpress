package com.alibaba.qlexpress4.parser;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Author: DQinYuan
 */
public class QListTest {

    @Test
    public void addLastTest() {
        QList<Integer> empty = QList.emptyList();
        empty.addLast(100);
        assertEquals(1, empty.getSize());
    }

    @Test
    public void emptyAdd() {
        QList<Integer> empty = QList.emptyList();
        empty.addAll(QList.singletonList(2));
        assertArrayEquals(new Integer[] {2}, empty.toArray(new Integer[1]));
        assertEquals(1, empty.getSize());
    }

    @Test
    public void listTest() {
        QList<Integer> qList = QList.singletonList(1).addLast(10).addLast(18);
        assertArrayEquals(new Integer[] {1, 10, 18}, qList.toArray(new Integer[3]));
        assertEquals(3, qList.getSize());

        qList.addAll(QList.singletonList(101));
        assertArrayEquals(new Integer[] {1, 10, 18, 101}, qList.toArray(new Integer[4]));
        assertEquals(4, qList.getSize());

        QList<Integer> qList2 = QList.singletonList(102).addLast(103);
        qList.addAll(qList2);
        assertArrayEquals(new Integer[] {1, 10, 18, 101, 102, 103}, qList.toArray(new Integer[6]));
        assertEquals(6, qList.getSize());
    }

}