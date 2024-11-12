package com.example.trainaut01.models;


public class User {

    public enum Role {
       USER, DOCTOR, ADMIN
    }

    private String userId;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private Role role = Role.USER;
    private int lvl = 0;
    private int countDays = 0;
    private int exp = 0;

    public User() {}

    public User(String userId, String firstName, String lastName, String phone, String email, Role role, int lvl, int countDays, int exp) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.lvl = lvl;
        this.countDays = countDays;
        this.exp = exp;
    }


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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role){
        this.role = role;
    }

}
