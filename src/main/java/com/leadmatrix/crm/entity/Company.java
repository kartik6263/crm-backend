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

        private String name;

        private String email;

        private String plan;


    }
