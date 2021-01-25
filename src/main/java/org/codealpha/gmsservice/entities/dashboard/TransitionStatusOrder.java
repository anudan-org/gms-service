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
}
