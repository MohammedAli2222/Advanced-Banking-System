package com.bank.security;

public class User {
    private final String username;
    private final UserState state;

    public User(String username, UserState state) {
        this.username = username;
        this.state = state;
    }

    public String getUsername() { return username; }
    public UserState getState() { return state; }
}