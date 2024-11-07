package com.example.trainaut01.models;

import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Parent {
    private String userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private Gender gender;
    private String email;
    private Role role = Role.USER;

    public Parent(String userId, String email, Gender gender, String city, String phone, String lastName, String firstName) {
        this.userId = userId;
        this.email = email;
        this.gender = gender;
        this.city = city;
        this.phone = phone;
        this.lastName = lastName;
        this.firstName = firstName;
    }
}
