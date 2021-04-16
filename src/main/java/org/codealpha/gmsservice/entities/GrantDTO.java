package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity(name = "grants")
/*@SqlResultSetMapping(name="GRANTS",
        entities={
                @EntityResult(entityClass= GrantDTO.class,
                        fields={
                                @FieldResult(name="id",column="id"),
                                @FieldResult(name="name", column="name")
                        }),
        },
        columns={
                @ColumnResult(name="approved_reports_for_grant"),
                @ColumnResult(name="approved_disbursements_total"),
                @ColumnResult(name="project_documents_count")
        }
)*/
public class GrantDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Transient
    private int approvedReportsForGrant;
    @Transient
    private Double approvedDisbursementsTotal;
    @Transient
    private int projectDocumentsCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getApprovedReportsForGrant() {
        return approvedReportsForGrant;
    }

    public void setApprovedReportsForGrant(int approvedReportsForGrant) {
        this.approvedReportsForGrant = approvedReportsForGrant;
    }

    public Double getApprovedDisbursementsTotal() {
        return approvedDisbursementsTotal;
    }

    public void setApprovedDisbursementsTotal(Double approvedDisbursementsTotal) {
        this.approvedDisbursementsTotal = approvedDisbursementsTotal;
    }

    public int getProjectDocumentsCount() {
        return projectDocumentsCount;
    }

    public void setProjectDocumentsCount(int projectDocumentsCount) {
        this.projectDocumentsCount = projectDocumentsCount;
    }
}
