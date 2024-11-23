package com.example.trainaut01.models;


import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private String userId;
    private String fullName;
    private String phone;
    private String birthDate;
    private String city;
    private Gender gender;
    private String email;
    private Role role = Role.USER;

    public User(String userId, String fullName, String phone, String birthDate, String city, Gender gender, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.city = city;
        this.gender = gender;
        this.email = email;
    }

}
