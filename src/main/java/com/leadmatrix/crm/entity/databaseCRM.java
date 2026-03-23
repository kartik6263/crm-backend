package com.leadmatrix.crm.entity;

import com.leadmatrix.crm.ENUMS.Role;
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

    private int Phone;


    private String password;


   // private String role; // Admin or Sales
    //@Enumerated(EnumType.STRING)
    //private Role role;
   private String role;

    private Long companyId;

    public databaseCRM() {
        //TODO Auto-generated constructor stub
    }
    public databaseCRM(String name, String password, String email, String role, int Phone) {
        this.name= name;
        this.password = password;
        this.email = email;
       // this.role = role;
        this.Phone = Phone;
    }



}
