package org.codealpha.gmsservice.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author Developer code-alpha.org
 **/
@Entity
@Table(name = "grantees")
@DiscriminatorValue("GRANTEE")
public class Grantee extends Organization{

	@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
	protected Long id;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
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
