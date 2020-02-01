package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity(name = "granter_report_templates")
public class GranterReportTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @OneToMany(mappedBy = "reportTemplate",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<GranterReportSection> sections;

    @Column
    private boolean published;
    @Column
    private boolean privateToReport;

    @Column
    private Long granterId;

    @Column
    private Boolean defaultTemplate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getGranterId() {
        return granterId;
    }

    public void setGranterId(Long granterId) {
        this.granterId = granterId;
    }

    public List<GranterReportSection> getSections() {
        return sections;
    }

    public void setSections(List<GranterReportSection> sections) {
        this.sections = sections;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isPrivateToReport() {
        return privateToReport;
    }

    public void setPrivateToReport(boolean privateToReport) {
        this.privateToReport = privateToReport;
    }

    public Boolean getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(Boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
}
