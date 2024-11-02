package com.example.trainaut01.models;

import java.util.List;

public class User {
    private String userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private int lvl;
    private int countDays;
    private int exp;

    public User() {}

    public User(String userId, String firstName, String lastName, String phone, String email, int lvl, int countDays, int exp) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.lvl = lvl;
        this.countDays = countDays;
        this.exp = exp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLvl() {
        return lvl;
    }

    public int getCountDays() {
        return countDays;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public void setCountDays(int countDays) {
        this.countDays = countDays;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }
}
