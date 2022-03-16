package org.codealpha.gmsservice.models;

import java.util.List;

public class GrantInvite {
    private GrantDTO grant;
    private List<InviteEntry> invites;

    public GrantDTO getGrant() {
        return grant;
    }

    public void setGrant(GrantDTO grant) {
        this.grant = grant;
    }

    public List<InviteEntry> getInvites() {
        return invites;
    }

    public void setInvites(List<InviteEntry> invites) {
        this.invites = invites;
    }
}
