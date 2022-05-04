package com.alibaba.qlexpress4.runtime.data;

import com.alibaba.qlexpress4.runtime.Value;

import java.lang.reflect.Method;

/**
 * @Author TaoKan
 * @Date 2022/4/19 下午10:05
 */
public class DataMethodInvoke implements Value {

    public DataMethodInvoke(Method method, Object obj, Object[] args, boolean allowAccessPrivate){
        this.method = method;
        this.bean = obj;
        this.args = args;
        this.allowAccessPrivate = allowAccessPrivate;
    }

    private Method method;
    private Object[] args;
    private Object bean;
    private boolean allowAccessPrivate;


    @Override
    public Object get() {
        try {
            if(this.method == null){
                return null;
            }
            if(!allowAccessPrivate || this.method.isAccessible()){
                return this.method.invoke(this.bean,this.args);
            }else {
                synchronized (this.method) {
                    try {
                        this.method.setAccessible(true);
                        return this.method.invoke(this.bean,this.args);
                    }finally {
                        this.method.setAccessible(false);
                    }
                }
            }
        }catch (Exception e){
            return null;
        }
    }
}
