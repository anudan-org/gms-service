package org.codealpha.gmsservice.models;

import java.util.List;

public class WorkFlowDataModel {
    private String name;
    private String type;
    private List<WorkflowTransitionDataModel> transitions;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<WorkflowTransitionDataModel> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<WorkflowTransitionDataModel> transitions) {
        this.transitions = transitions;
    }
}
