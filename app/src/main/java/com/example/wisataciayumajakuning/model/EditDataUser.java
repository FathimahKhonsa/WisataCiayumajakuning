package com.example.wisataciayumajakuning.model;

public class EditDataUser {
    String username, phone;

    public EditDataUser(){}

    public EditDataUser(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
