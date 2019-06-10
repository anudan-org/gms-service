package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import org.codealpha.gmsservice.constants.GrantStatus;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "grants")
public class Grant extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@OrderBy("id ASC")
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "grantor_org_id")
	private Organization grantorOrganization;

	@OneToMany(mappedBy = "grant")
	@JsonProperty("kpis")
	private List<GrantKpi> kpis;

	@OneToMany(mappedBy = "grant")
	@JsonProperty("stringAttribute")
	private List<GrantStringAttribute> stringAttributes;

	@OneToMany(mappedBy = "grant")
	@JsonProperty("docAttribute")
	private List<GrantDocumentAttributes> documentAttributes;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column
	private Date createdAt;

	@Column
	private String createdBy;

	@Column
	private Date updatedAt;

	@Column
	private String updatedBy;

	@OneToOne
	@JoinColumn(referencedColumnName = "id")
	private WorkflowStatus grantStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private GrantStatus statusName;

	@OneToOne
	@JoinColumn(referencedColumnName = "id")
	private WorkflowStatus substatus;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	@OneToMany(mappedBy = "grant",fetch = FetchType.LAZY)
	@OrderBy("submitBy ASC")
	private List<Submission> submissions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
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


	public Organization getGrantorOrganization() {
		return grantorOrganization;
	}

	public void setGrantorOrganization(Organization grantorOrganization) {
		this.grantorOrganization = grantorOrganization;
	}

	public GrantStatus getStatusName() {
		return statusName;
	}

	public void setStatusName(GrantStatus status) {
		this.statusName = status;
	}

	public WorkflowStatus getSubstatus() {
		return substatus;
	}

	public void setSubstatus(WorkflowStatus substatus) {
		this.substatus = substatus;
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

	public List<Submission> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<Submission> submissions) {
		this.submissions = submissions;
	}

	public WorkflowStatus getGrantStatus() {
		return grantStatus;
	}

	public void setGrantStatus(WorkflowStatus status) {
		this.grantStatus = status;
	}

	public List<GrantStringAttribute> getStringAttributes() {
		return stringAttributes;
	}

	public void setStringAttributes(
			List<GrantStringAttribute> stringAttributes) {
		this.stringAttributes = stringAttributes;
	}

	public List<GrantDocumentAttributes> getDocumentAttributes() {
		return documentAttributes;
	}

	public void setDocumentAttributes(
			List<GrantDocumentAttributes> documentAttributes) {
		this.documentAttributes = documentAttributes;
	}

	public List<GrantKpi> getKpis() {
		return kpis;
	}

	public void setKpis(List<GrantKpi> kpis) {
		this.kpis = kpis;
	}
}
