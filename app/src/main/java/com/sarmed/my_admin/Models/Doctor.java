package com.sarmed.my_admin.Models;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Doctor implements Serializable {
    private String id;
    private String image , name , number , email , address , description;

    public Doctor() {
    }

    public Doctor(String id, String image, String name, String number, String email, String address, String description) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
        this.description = description;
    }

    public Doctor(String image, String name, String number, String email, String address, String description) {

        this.image = image;
        this.name = name;
        this.number = number;
        this.email = email;
        this.address = address;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
