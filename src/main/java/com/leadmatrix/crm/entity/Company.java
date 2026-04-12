package com.leadmatrix.crm.entity;

import jakarta.persistence.*;

@Entity
@Table(name="companies")
public class Company {

        @OneToOne
        private Subscription subscription;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        private String email;

        private String phone;
        private String address;
        private String status;

      //  private String plan;

        public Company() {

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

        public String getAddress() {
                return address;
        }

        public void setAddress(String address) {
                this.address = address;
        }

        public String getStatus() {
                return status;
        }

        public void setStatus(String status) {
                this.status = status;
        }

       // public String getPlan() {
       //         return  plan;
      //  }
      //  public void setPlan(String plan) {
       //         this.plan = plan;
    //    }


    }
