package com.demo.model;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoginModel {

    private String email;
    private String password;

}
