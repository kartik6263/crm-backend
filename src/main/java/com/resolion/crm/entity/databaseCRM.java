package com.resolion.crm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@Table(name = "users")
public class databaseCRM {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String name;

    @Column(unique = true)
    private String email;

    private String phone;


    private String password;


    // private String role; // Admin or Sales
    //@Enumerated(EnumType.STRING)
    //private CompanyRole role;
    private String role;
    //private String twoFactorSecret;
    // private Boolean twoFactorEnabled = false;
    //  private String backupCodes;

    // private Long companyId;


    public databaseCRM() {
        //TODO Auto-generated constructor stub
    }

    public databaseCRM(String name, String password, String email, String role, String phone) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    //public Long getCompanyId() {
    // return companyId;
    //}

    // public void setCompanyId(Long companyId) {
    //this.companyId = companyId;
    //}


    // public String getTwoFactorSecret() {
    // return twoFactorSecret;
    //  }

    // public void setTwoFactorSecret(String twoFactorSecret) {
    //    this.twoFactorSecret = twoFactorSecret;
    // }

    //  public Boolean getTwoFactorEnabled() {
    //     return twoFactorEnabled;
    //  }

    //  public void setTwoFactorEnabled(Boolean twoFactorEnabled) {
    //      this.twoFactorEnabled = twoFactorEnabled;
    //  }

    // public String getBackupCodes() {
    //return backupCodes;
    // }

    // public void setBackupCodes(String backupCodes) {
    //  this.backupCodes = backupCodes;
    //  }
//}
}