package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;
import org.springframework.web.multipart.MultipartFile;

public class GrantFileUpload {
    private Grant grant;
    private MultipartFile[] file;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public MultipartFile[] getFile() {
        return file;
    }

    public void setFile(MultipartFile[] file) {
        this.file = file;
    }
}
