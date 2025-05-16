package com.example.campusapp;

// User.java
public class User {
    public String email;
    public String nickname;

    public User() {}  // Required for Firebase

    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}