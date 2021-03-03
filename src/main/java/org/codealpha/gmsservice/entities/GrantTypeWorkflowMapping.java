package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity
public class GrantTypeWorkflowMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long grantTypeId;
    @Column
    private Long workflowId;
    @Column
    private boolean internal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGrantTypeId() {
        return grantTypeId;
    }

    public void setGrantTypeId(Long grantTypeId) {
        this.grantTypeId = grantTypeId;
    }

    public Long getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(Long workflowId) {
        this.workflowId = workflowId;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(boolean internal) {
        this.internal = internal;
    }
}
