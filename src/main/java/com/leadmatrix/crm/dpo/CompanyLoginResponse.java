package com.leadmatrix.crm.dpo;

import java.util.List;
import java.util.Map;

public class CompanyLoginResponse {
    private String token;
    private String email;
    private List<Map<String, Object>> companies;

    public CompanyLoginResponse(String token, String email, List<Map<String, Object>> companies) {
        this.token = token;
        this.email = email;
        this.companies = companies;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public List<Map<String, Object>> getCompanies() {
        return companies;
    }
}