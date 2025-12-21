package com.bank.utils;

public class ContactInfo {
    private String email;
    private String phone;
    private String address;

    public ContactInfo(String email, String phone, String address) {
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    // Getters
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getAddress() { return address; }

    @Override
    public String toString() {
        return "Email: " + email + ", Phone: " + phone + ", Address: " + address;
    }
}