package com.example.e2tech.Models;

public class UserModel {

    String username, password, email, address, age, phone, gender, img_url, id;

    public UserModel() {

    }

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public UserModel(String username, String email, String password) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public UserModel(String username, String password, String email, String id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public UserModel(String username, String password, String email, String address, String img_url, String id) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.img_url = img_url;
        this.id = id;
    }

    public UserModel(String username, String email, String password, String address, String age, String phone, String gender, String img_url) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.address = address;
        this.age = age;
        this.phone = phone;
        this.gender = gender;
        this.img_url = img_url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) { this.age = age; }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) { this.phone = phone; }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) { this.gender = gender; }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
