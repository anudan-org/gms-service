package org.codealpha.gmsservice.models;

import org.codealpha.gmsservice.entities.Grant;

import java.util.List;

public class GrantInvite {
    private Grant grant;
    private List<InviteEntry> invites;

    public Grant getGrant() {
        return grant;
    }

    public void setGrant(Grant grant) {
        this.grant = grant;
    }

    public List<InviteEntry> getInvites() {
        return invites;
    }

    public void setInvites(List<InviteEntry> invites) {
        this.invites = invites;
    }
}
