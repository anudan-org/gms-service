package org.codealpha.gmsservice.constants;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityEndpointProperties {
    
    private String[] publicEndpoints = {};
    private String[] authenticatedEndpoints = {};
    private String[] adminEndpoints = {};
    
    public String[] getPublicEndpoints() {
        return publicEndpoints;
    }
    
    public void setPublicEndpoints(String[] publicEndpoints) {
        this.publicEndpoints = publicEndpoints;
    }
    
    public String[] getAuthenticatedEndpoints() {
        return authenticatedEndpoints;
    }
    
    public void setAuthenticatedEndpoints(String[] authenticatedEndpoints) {
        this.authenticatedEndpoints = authenticatedEndpoints;
    }
    
    public String[] getAdminEndpoints() {
        return adminEndpoints;
    }
    
    public void setAdminEndpoints(String[] adminEndpoints) {
        this.adminEndpoints = adminEndpoints;
    }
}
