package com.ql.util.express.rule;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianqiao on 16/12/6.
 */

public class Condition extends Node{


    private boolean prior = false;

    private ConditionType type;

    private List<Condition> children;

    public Condition(ConditionType type) {
        this.type = type;
    }

    public Condition() {
    }

    public boolean isPrior() {
        return prior;
    }

    public void setPrior(boolean prior) {
        this.prior = prior;
    }

    public ConditionType getType() {
        return type;
    }

    public void setType(ConditionType type) {
        this.type = type;
    }

    public List<Condition> getChildren() {
        return children;
    }

    public void setChildren(List<Condition> children) {
        this.children = children;
    }

    public void addChild(Condition child) {
        if(this.children==null){
            this.children = new ArrayList<Condition>();
        }
        this.children.add(child);
    }

    public String toString()
    {
        if(this.type == ConditionType.Leaf){
            return priorString(this.getText());
        } else if(this.type == ConditionType.And){
            return priorString(StringUtils.join(this.getChildren()," and "));
        } else if(this.type == ConditionType.Or){
            return priorString(StringUtils.join(this.getChildren()," or "));
        }
        return null;
    }

    private String priorString(String orig)
    {
        if(this.isPrior()){
            return new StringBuilder("(").append(orig).append(")").toString();
        }
        return orig;

    }
}
