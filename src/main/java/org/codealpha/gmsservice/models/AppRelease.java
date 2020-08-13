package org.codealpha.gmsservice.models;

public class AppRelease {

    private String releaseCandidate;
    private String productionRelease;
    private String hotFixRelease;

    public String getReleaseCandidate() {
        return releaseCandidate;
    }

    public void setReleaseCandidate(String releaseCandidate) {
        this.releaseCandidate = releaseCandidate;
    }

    public String getProductionRelease() {
        return productionRelease;
    }

    public void setProductionRelease(String productionRelease) {
        this.productionRelease = productionRelease;
    }

    public String getHotFixRelease() {
        return hotFixRelease;
    }

    public void setHotFixRelease(String hotFixRelease) {
        this.hotFixRelease = hotFixRelease;
    }

}
