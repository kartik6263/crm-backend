package com.resolion.crm.entity;
import com.resolion.crm.ENUMS.CompanyRole;
import jakarta.persistence.*;

@Entity
@Table(name = "company_invites")
public class CompanyInvite {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private Long companyId;

        private String invitedEmail;

        @Enumerated(EnumType.STRING)
        private CompanyRole role;

        private String inviteToken;

        private String status; // PENDING / ACCEPTED / EXPIRED

        private String createdDate;

        public CompanyInvite() {
        }

        public Long getId() {
            return id;
        }

        public Long getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Long companyId) {
            this.companyId = companyId;
        }

        public String getInvitedEmail() {
            return invitedEmail;
        }

        public void setInvitedEmail(String invitedEmail) {
            this.invitedEmail = invitedEmail;
        }

        public CompanyRole getRole() {
            return role;
        }

        public void setRole(CompanyRole role) {
            this.role = role;
        }

        public String getInviteToken() {
            return inviteToken;
        }

        public void setInviteToken(String inviteToken) {
            this.inviteToken = inviteToken;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }
    }

