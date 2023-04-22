package com.alibaba.qlexpress4.member;

/**
 * @Author TaoKan
 * @Date 2023/4/9 下午7:15
 */
public interface IMember {

    boolean allowVisitWithOutPermission();

    void seVisitWithOutPermission(boolean allow);

    String getQualifyName();

}
