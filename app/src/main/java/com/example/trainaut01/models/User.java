package com.example.trainaut01.models;

import java.util.List;

public class User {
    private long userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private int lvl;
    private int countDays;

    public User() {}

    public User(long userId, String firstName, String lastName, String phone, String email, int lvl, int countDays) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.lvl = lvl;
        this.countDays = countDays;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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


}
