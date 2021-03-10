package org.codealpha.gmsservice.entities.dashboard;

import javax.persistence.*;

@Entity
public class TransitionStatusOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String state;
    @Column
    private String internalStatus;
    @Column
    private int seqOrder;
    @Column
    private Long fromStateId;
    @Column
    private Long workflowId;
    @Column
    private Long grantTypeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSeqOrder() {
        return seqOrder;
    }

    public void setSeqOrder(int seqOrder) {
        this.seqOrder = seqOrder;
    }

    public String getInternalStatus() {
        return internalStatus;
    }

    public void setInternalStatus(String internalStatus) {
        this.internalStatus = internalStatus;
    }

    public Long getFromStateId() {
        return fromStateId;
    }

    public void setFromStateId(Long fromStateId) {
        this.fromStateId = fromStateId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public Long getGrantTypeId() {
        return grantTypeId;
    }

    public void setGrantTypeId(Long grantTypeId) {
        this.grantTypeId = grantTypeId;
    }
}
