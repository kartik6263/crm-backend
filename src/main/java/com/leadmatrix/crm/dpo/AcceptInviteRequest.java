package com.leadmatrix.crm.dpo;

public class AcceptInviteRequest {
    private String inviteToken;
    private String email;

    public String getInviteToken() {
        return inviteToken;
    }

    public String getEmail() {
        return email;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}