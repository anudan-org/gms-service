package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "reports")
public class Report {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text") private String name;
    @Column private Date startDate;
    @Column private Date endDate;
    @Column private Date dueDate;
    @OneToOne @JoinColumn(referencedColumnName = "id") private WorkflowStatus status;
    @Column private Date createdAt;
    @Column private Long createdBy;
    @Column private Date updatedAt;
    @Column private Long updatedBy;
    @Column private String type;
    @Transient private List<ReportAssignment> assignments;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private Grant grant;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public WorkflowStatus getStatus() {
        return status;
    }

    public void setStatus(WorkflowStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public void setAssignments(List<ReportAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<ReportAssignment> getAssignments() {
        return assignments;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
