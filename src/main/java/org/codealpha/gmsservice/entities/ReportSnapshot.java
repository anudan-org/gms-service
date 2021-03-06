package org.codealpha.gmsservice.entities;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Developer code-alpha.org
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

  @Column(name = "name", columnDefinition = "text")
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column
  private Long statusId;

  @Column
  private Date startDate;

  @Column
  private Date endDate;

  @Column
  private Date dueDate;
  @Column
  private Long fromStateId;
  @Column
  private Long toStateId;
  @Column
  private String fromNote;
  @Column
  private Long movedBy;
  @Column
  private String fromStringAttributes;
  @Column
  private Date movedOn;

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

  public Long getFromStateId() {
    return fromStateId;
  }

  public void setFromStateId(Long fromStateId) {
    this.fromStateId = fromStateId;
  }

  public Long getToStateId() {
    return toStateId;
  }

  public void setToStateId(Long toStateId) {
    this.toStateId = toStateId;
  }

  public String getFromNote() {
    return fromNote;
  }

  public void setFromNote(String fromNote) {
    this.fromNote = fromNote;
  }

  public Long getMovedBy() {
    return movedBy;
  }

  public void setMovedBy(Long movedBy) {
    this.movedBy = movedBy;
  }

  public String getFromStringAttributes() {
    return fromStringAttributes;
  }

  public void setFromStringAttributes(String fromStringAttributes) {
    this.fromStringAttributes = fromStringAttributes;
  }

  public Date getMovedOn() {
    return movedOn;
  }

  public void setMovedOn(Date movedOn) {
    this.movedOn = movedOn;
  }
}
