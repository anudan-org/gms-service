package org.codealpha.gmsservice.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity(name = "granter_grant_templates")
public class GranterGrantTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    @OneToMany(mappedBy = "grantTemplate",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<GranterGrantSection> sections;

    @Column
    private boolean published;
    @Column
    private boolean privateToGrant;

    @Column
    private Long granterId;

    @Column
    private boolean defaultTemplate;

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

    public List<GranterGrantSection> getSections() {
        return sections;
    }

    public void setSections(List<GranterGrantSection> sections) {
        this.sections = sections;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isPrivateToGrant() {
        return privateToGrant;
    }

    public void setPrivateToGrant(boolean privateToGrant) {
        this.privateToGrant = privateToGrant;
    }

    public boolean isDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(boolean defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
}
