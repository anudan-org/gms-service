package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;


/**
 * @author Developer code-alpha.org
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
public class Organization {

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

	@Column
	private String description;
	@Column
	private String website;
	@Column
	private String twitter;
	@Column
	private String facebook;
	@Column
	private String linkedin;
	@Column
	private String instagram;


	protected Organization() {
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
		return this.organizationType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getTwitter() {
		return twitter;
	}

	public void setTwitter(String twitter) {
		this.twitter = twitter;
	}

	public String getFacebook() {
		return facebook;
	}

	public void setFacebook(String facebook) {
		this.facebook = facebook;
	}

	public String getLinkedin() {
		return linkedin;
	}

	public void setLinkedin(String linkedin) {
		this.linkedin = linkedin;
	}

	public String getInstagram() {
		return instagram;
	}

	public void setInstagram(String instagram) {
		this.instagram = instagram;
	}
}
