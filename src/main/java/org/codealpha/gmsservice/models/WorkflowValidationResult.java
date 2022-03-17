package org.codealpha.gmsservice.models;

import java.util.List;

public class WorkflowValidationResult {
    private List<WarningMessage> messages;
    private Boolean canMove;


    public List<WarningMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<WarningMessage> messages) {
        this.messages = messages;
    }

    public Boolean getCanMove() {
        return canMove;
    }

    public void setCanMove(Boolean canMove) {
        this.canMove = canMove;
    }
}
