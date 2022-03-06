package com.example.e2tech.Models;

import com.google.type.DateTime;

import java.util.Date;

public class CommentModel {
    String commentId;
    String userId;
    String productId;
    DateTime createdAt;
    Float rating;
    String title;
    String content;

    public CommentModel(String userId, DateTime createdAt, Float rating, String title, String content) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
