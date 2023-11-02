package com.demo.model;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordModel {

    private String email;
    private String oldPassword;
    private String newPassword;


}
