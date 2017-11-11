package com.ql.util.express.rule;

import java.util.List;

/**
 * Created by tianqiao on 16/12/6.
 */
public class RuleCase extends Node{

    private List<Action> actions;

    private Condition condition;

    public RuleCase(Condition condition,List<Action> actions) {
        this.actions = actions;
        this.condition = condition;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
