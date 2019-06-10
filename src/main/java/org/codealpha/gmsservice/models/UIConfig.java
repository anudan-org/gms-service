package org.codealpha.gmsservice.models;

import java.util.List;
import org.codealpha.gmsservice.entities.GrantSection;

/**
 * @author Developer <developer@enstratify.com>
 **/
public class UIConfig {

	private String logoUrl;

	private String navbarColor;

	private String tenantCode;

	private String navbarTextColor;

	private List<GrantSection> defaultSections;

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getNavbarColor() {
		return navbarColor;
	}

	public void setNavbarColor(String navbarColor) {
		this.navbarColor = navbarColor;
	}

	public String getTenantCode() {
		return tenantCode;
	}

	public void setTenantCode(String tenantCode) {
		this.tenantCode = tenantCode;
	}

	public String getNavbarTextColor() {
		return navbarTextColor;
	}

	public void setNavbarTextColor(String navbarTextColor) {
		this.navbarTextColor = navbarTextColor;
	}

	public List<GrantSection> getDefaultSections() {
		return defaultSections;
	}

	public void setDefaultSections(
			List<GrantSection> defaultSections) {
		this.defaultSections = defaultSections;
	}
}
