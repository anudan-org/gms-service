package org.codealpha.gmsservice.models;

/**
 * @author Developer code-alpha.org
 **/
public class GranteeDTO extends OrganizationDTO {

	@Override
	public Long getId() {
		return id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String toString() {
		return "Grantee{" +
				"id=" + id +
				", id=" + id +
				", name='" + name + '\'' +
				", code='" + code + '\'' +
				", createdAt=" + createdAt +
				", createdBy='" + createdBy + '\'' +
				", updatedAt=" + updatedAt +
				", updatedBy='" + updatedBy + '\'' +
				'}';
	}
}
