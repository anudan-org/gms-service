package org.codealpha.gmsservice.entities;

import javax.persistence.*;

@Entity(name = "hygiene_checks")
public class HygieneCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String hygieneQuery;
    @Column
    private Boolean active;
    @Column
    private String object;
    @Column
    private String message;
    @Column
    private String subject;
    @Column
    private String name;
    @Column
    private String description;
    @Column
    private String scheduledRun;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHygieneQuery() {
        return hygieneQuery;
    }

    public void setHygieneQuery(String hygieneQuery) {
        this.hygieneQuery = hygieneQuery;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getScheduledRun() {
        return scheduledRun;
    }

    public void setScheduledRun(String scheduledRun) {
        this.scheduledRun = scheduledRun;
    }
}
