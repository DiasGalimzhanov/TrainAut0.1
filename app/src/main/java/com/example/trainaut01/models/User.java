package com.example.trainaut01.models;


import com.example.trainaut01.enums.Gender;
import com.example.trainaut01.enums.Role;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
    private String pass;
    private Role role = Role.USER;

    public User(String userId, String fullName, String phone, String birthDate, String city, Gender gender, String email, String pass) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.city = city;
        this.gender = gender;
        this.email = email;
        this.pass = pass;
    }

    public User(String userId, String fullName, String phone, String birthDate, String city, Gender gender, String email) {
        this.userId = userId;
        this.fullName = fullName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.city = city;
        this.gender = gender;
        this.email = email;
    }

    public User(String fullName, String phone, String birthDate, String city, Gender gender, String email, String pass) {
        this.fullName = fullName;
        this.phone = phone;
        this.birthDate = birthDate;
        this.city = city;
        this.gender = gender;
        this.email = email;
        this.pass = pass;
    }

    public void setPass(String pass){
        this.pass = hashPassword(pass);
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while hashing password", e);
        }
    }

    /**
     * Преобразует объект User в Map<String, Object> для сохранения в базу данных.
     *
     * @return Map с данными пользователя.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("fullName", fullName);
        userMap.put("phone", phone);
        userMap.put("birthDate", birthDate);
        userMap.put("city", city);
        userMap.put("gender", gender != null ? gender.toString() : null);
        userMap.put("email", email);
//        userMap.put("pass", pass);
        userMap.put("role", role != null ? role.toString() : Role.USER.toString());
        return userMap;
    }
}
