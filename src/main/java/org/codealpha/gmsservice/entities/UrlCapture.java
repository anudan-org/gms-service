package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity
public class UrlCapture {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "text")
    private String url;
    @Column(columnDefinition = "text")
    private String method;

    public UrlCapture(String servletPath, String method) {
        this.url = servletPath;
        this.method = method;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
