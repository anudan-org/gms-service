package org.codealpha.gmsservice.entities;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;


/**
 * @author Developer <developer@enstratify.io>
 **/
@Entity
@Table(name = "organizations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "organization_type")
public abstract class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "name")
	protected String name;

	@Column(name = "code")
	protected String code;

	@Column(name = "created_at")
	protected LocalDateTime createdAt;

	@Column(name = "created_by")
	protected String createdBy;

	@Column(name = "updated_at")
	protected LocalDateTime updatedAt;

	@Column(name = "updated_by")
	protected String updatedBy;

	@Column(name = "organization_type")
	private String organizationType;

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

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Transient
	public String getType() {
		return this.getClass().getAnnotation(DiscriminatorValue.class).value();
	}


}
