package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import javax.persistence.*;


/**
 * @author Developer <developer@enstratify.io>
 **/
@Entity
@Table(name = "organizations")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "organization_type")
@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "type")
@JsonSubTypes({
		@JsonSubTypes.Type(value = Granter.class, name = "GRANTER"),
		@JsonSubTypes.Type(value = Grantee.class, name = "GRANTEE")
})
public abstract class Organization {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Column(name = "name",nullable = true)
	protected String name;

	@Column(name = "code",nullable = true)
	protected String code;

	@Column(name = "created_at")
	protected Date createdAt;

	@Column(name = "created_by")
	protected String createdBy;

	@Column(name = "updated_at",nullable = true)
	protected LocalDateTime updatedAt;

	@Column(name = "updated_by",nullable = true)
	protected String updatedBy;

	@Column(name = "organization_type",insertable = false,updatable = false)
	private String organizationType;


	public Organization() {
	}

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

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
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

	public String getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(String organizationType) {
		this.organizationType = organizationType;
	}

	@Transient
	public String getType() {
		return this.getClass().getAnnotation(DiscriminatorValue.class).value();
	}

}
