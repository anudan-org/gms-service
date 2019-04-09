package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * @author Developer <developer@enstratify.com>
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

	@OneToMany(mappedBy = "granter", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<Rfp> rfps;

	@OneToMany(mappedBy = "granter")
	@JsonIgnore
	private List<Workflow> workflows;

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

	public List<Rfp> getRfps() {
		return rfps;
	}

	public void setRfps(List<Rfp> rfps) {
		this.rfps = rfps;
	}

	@Override
	public String toString() {
		return "Granter{" +
				"id=" + id +
				", hostUrl='" + hostUrl + '\'' +
				", imageName='" + imageName + '\'' +
				", rfps=" + rfps +
				", name='" + name + '\'' +
				", code='" + code + '\'' +
				", createdAt=" + createdAt +
				", createdBy='" + createdBy + '\'' +
				", updatedAt=" + updatedAt +
				", updatedBy='" + updatedBy + '\'' +
				'}';
	}
}
