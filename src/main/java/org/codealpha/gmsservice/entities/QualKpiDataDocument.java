package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class QualKpiDataDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String fileName;
  @Column
  private String fileType;
  @Column
  private int version = 1;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonIgnore
  private GrantQualitativeKpiData qualKpiData;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getFileType() {
    return fileType;
  }

  public void setFileType(String fileType) {
    this.fileType = fileType;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public GrantQualitativeKpiData getQualKpiData() {
    return qualKpiData;
  }

  public void setQualKpiData(GrantQualitativeKpiData qualKpiData) {
    this.qualKpiData = qualKpiData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QualKpiDataDocument that = (QualKpiDataDocument) o;
    return fileName.equals(that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName);
  }
}
