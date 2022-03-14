package com.example.e2tech.Models;

import com.google.type.DateTime;

import java.util.Date;

public class CommentModel {
    String userEmail;
    DateTime createdAt;
    int rating;
    String content;

    public CommentModel(String userEmail, DateTime createdAt, int rating, String content) {
        this.userEmail = userEmail;
        this.createdAt = createdAt;
        this.rating = rating;
        this.content = content;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
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
}
