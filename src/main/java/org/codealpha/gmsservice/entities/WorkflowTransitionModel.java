package org.codealpha.gmsservice.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class WorkflowTransiationModel {
    @Id
    private Long id;
    @Column
    private String action;
    @Column
    private String _from;
    @Column
    private String _to;
    @Column
    private String _performedby;
}
