package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "report_snapshot")
public class ReportSnapshot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @OrderBy("id ASC")
  private Long id;

  @Column
  private Long assignedToId;

  @Column
  private Long reportId;


  @Column(columnDefinition = "text")
  private String stringAttributes;

  @Column(name = "name",columnDefinition = "text")
  private String name;

  @Column(name = "description",columnDefinition = "text")
  private String description;


  @Column
  private Long statusId;

  @Column
  private Date startDate;

  @Column
  private Date endDate;

  @Column
  private Date dueDate;


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getAssignedToId() {
    return assignedToId;
  }

  public void setAssignedToId(Long assignedToId) {
    this.assignedToId = assignedToId;
  }

  public Long getReportId() {
    return reportId;
  }

  public void setReportId(Long reportId) {
    this.reportId = reportId;
  }

  public String getStringAttributes() {
    return stringAttributes;
  }

  public void setStringAttributes(String stringAttributes) {
    this.stringAttributes = stringAttributes;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }



    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
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
}
