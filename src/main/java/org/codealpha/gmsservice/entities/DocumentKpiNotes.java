package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;

@Entity
public class DocumentKpiNotes {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String message;

  @Column
  private Date postedOn;

  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private GrantDocumentKpiData kpiData;

  @OneToOne
  @JoinColumn(referencedColumnName = "id")
  private User postedBy;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Date getPostedOn() {
    return postedOn;
  }

  public void setPostedOn(Date postedOn) {
    this.postedOn = postedOn;
  }

  public User getPostedBy() {
    return postedBy;
  }

  public void setPostedBy(User postedBy) {
    this.postedBy = postedBy;
  }

  public GrantDocumentKpiData getKpiData() {
    return kpiData;
  }

  public void setKpiData(GrantDocumentKpiData kpiData) {
    this.kpiData = kpiData;
  }
}
