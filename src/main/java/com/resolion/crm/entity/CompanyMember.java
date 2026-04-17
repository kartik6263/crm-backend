package com.resolion.crm.entity;
import com.resolion.crm.ENUMS.CompanyRole;
import jakarta.persistence.*;


@Entity
@Table(name = "company_members")
public class CompanyMember {




        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long companyId;

        @Column(nullable = false)
        private Long userId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private CompanyRole role;

        @Column(nullable = false)
        private Boolean active = true;

        public CompanyMember() {
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

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public CompanyRole getRole() {
            return role;
        }

        public void setRole(CompanyRole role) {
            this.role = role;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

