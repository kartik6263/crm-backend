package com.leadmatrix.crm.dpo;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private Long id;
    private String name;
    private String email;
    private String password;

}
