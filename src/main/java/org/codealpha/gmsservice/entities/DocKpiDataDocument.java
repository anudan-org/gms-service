package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class DocKpiDataDocument {

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
  private GrantDocumentKpiData docKpiData;

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

  public GrantDocumentKpiData getDocKpiData() {
    return docKpiData;
  }

  public void setDocKpiData(GrantDocumentKpiData docKpiData) {
    this.docKpiData = docKpiData;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DocKpiDataDocument that = (DocKpiDataDocument) o;
    return fileName.equals(that.fileName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName);
  }
}
