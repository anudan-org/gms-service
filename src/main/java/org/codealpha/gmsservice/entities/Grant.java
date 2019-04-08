package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
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
import javax.persistence.Table;
import org.codealpha.gmsservice.constants.GrantStatus;
import org.codealpha.gmsservice.constants.GrantSubStatus;

/**
 * @author Developer <developer@enstratify.com>
 **/
@Entity
@Table(name = "grants")
public class Grant extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "organization_id")
	private Organization organization;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "grantor_org_id")
	private Organization grantorOrganization;

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

	@Column
	@Enumerated(EnumType.STRING)
	private GrantStatus status;

	@Column
	@Enumerated(EnumType.STRING)
	private GrantSubStatus substatus;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	@OneToMany(mappedBy = "grant",fetch = FetchType.EAGER)
	private List<GrantKpi> kpis;

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

	public GrantStatus getStatus() {
		return status;
	}

	public void setStatus(GrantStatus status) {
		this.status = status;
	}

	public GrantSubStatus getSubstatus() {
		return substatus;
	}

	public void setSubstatus(GrantSubStatus substatus) {
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

	public List<GrantKpi> getKpis() {
		return kpis;
	}

	public void setKpis(List<GrantKpi> kpis) {
		this.kpis = kpis;
	}
}
