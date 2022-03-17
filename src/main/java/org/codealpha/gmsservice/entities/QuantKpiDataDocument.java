package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class QuantKpiDataDocument {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column
  private String fileName;
  @Column
  private String fileType;
  @Column
  private int version = 1;
  @Transient
  private String data;
  @ManyToOne
  @JoinColumn(referencedColumnName = "id")
  @JsonBackReference
  @JsonProperty(access = Access.WRITE_ONLY)
  private GrantQuantitativeKpiData quantKpiData;

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

  public GrantQuantitativeKpiData getQuantKpiData() {
    return quantKpiData;
  }

  public void setQuantKpiData(GrantQuantitativeKpiData quantKpiData) {
    this.quantKpiData = quantKpiData;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    QuantKpiDataDocument that = (QuantKpiDataDocument) o;
    return fileName.equals(that.fileName);
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName);
  }
}
