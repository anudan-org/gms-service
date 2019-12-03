package org.codealpha.gmsservice.models;

import java.util.List;

public class templateVO {

    private String type;
    private String name;
    private String description;
    private List<TemplateSectionVO> sections;
    private boolean _default;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public List<TemplateSectionVO> getSections() {
        return sections;
    }

    public void setSections(List<TemplateSectionVO> sections) {
        this.sections = sections;
    }

    public boolean is_default() {
        return _default;
    }

    public void set_default(boolean _default) {
        this._default = _default;
    }
}
