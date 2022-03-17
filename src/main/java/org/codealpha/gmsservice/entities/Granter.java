package org.codealpha.gmsservice.entities;

import javax.persistence.*;

/**
 * @author Developer code-alpha.org
 **/
@Entity
@Table(name = "granters")
@DiscriminatorValue("GRANTER")
public class Granter extends Organization {

	@PrimaryKeyJoinColumn(name = "id", referencedColumnName = "id")
	protected Long id;

	@Column(name = "host_url")
	private String hostUrl;

	@Column(name = "image_name")
	private String imageName;

	@Column(name = "navbar_color")
	private String navbarColor;

	@Column
	private String navbarTextColor;


	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getHostUrl() {
		return hostUrl;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getNavbarColor() {
		return navbarColor;
	}

	public void setNavbarColor(String navbarColor) {
		this.navbarColor = navbarColor;
	}



	public String getNavbarTextColor() {
		return navbarTextColor;
	}

	public void setNavbarTextColor(String navbarTextColor) {
		this.navbarTextColor = navbarTextColor;
	}

	@Override
	public String toString() {
		return "Granter{" +
				"id=" + id +
				", hostUrl='" + hostUrl + '\'' +
				", imageName='" + imageName + '\'' +
				", name='" + name + '\'' +
				", code='" + code + '\'' +
				", createdAt=" + createdAt +
				", createdBy='" + createdBy + '\'' +
				", updatedAt=" + updatedAt +
				", updatedBy='" + updatedBy + '\'' +
				'}';
	}
}
