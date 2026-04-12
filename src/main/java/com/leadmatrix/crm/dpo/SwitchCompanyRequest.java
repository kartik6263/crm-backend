package com.leadmatrix.crm.dpo;

public class SwitchCompanyRequest {
    private String email;
    private Long companyId;

    public String getEmail() {
        return email;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }
}