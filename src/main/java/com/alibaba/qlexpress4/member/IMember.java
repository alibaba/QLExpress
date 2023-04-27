package com.alibaba.qlexpress4.member;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午12:04
 */
public interface IMember {
    Class getClazz();

    String getName();

    String getQualifyName();

    boolean allowVisitWithOutPermission();

    void seVisitWithOutPermission(boolean allow);
}
