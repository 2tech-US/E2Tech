package com.example.e2tech.Models;

import com.google.firebase.Timestamp;

public class CommentModel {
    String id;
    String email;
    Timestamp createdAt;
    int rating;
    String content;
    String name;
    String img_url;

    public CommentModel() {

    }

    public CommentModel(String name, String email, Timestamp createdAt, int rating, String content,String img_url) {
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.rating = rating;
        this.content = content;
        this.img_url = img_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

}
