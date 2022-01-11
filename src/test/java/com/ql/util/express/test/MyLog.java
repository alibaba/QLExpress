package com.ql.util.express.test;

import org.apache.commons.logging.Log;

public class MyLog implements Log {
    private final String name;

    public MyLog(String name) {
        this.name = name;
    }

    public boolean isDebugEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isErrorEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isFatalEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isInfoEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isTraceEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isWarnEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public void trace(Object message) {
        // TODO Auto-generated method stub
    }

    public void trace(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }

    public void debug(Object message) {
        // TODO Auto-generated method stub
    }

    public void debug(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }

    public void info(Object message) {
        // TODO Auto-generated method stub
    }

    public void info(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }

    public void warn(Object message) {
        // TODO Auto-generated method stub
    }

    public void warn(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }

    public void error(Object message) {
        // TODO Auto-generated method stub
    }

    public void error(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }

    public void fatal(Object message) {
        // TODO Auto-generated method stub
    }

    public void fatal(Object message, Throwable t) {
        // TODO Auto-generated method stub
    }
}
