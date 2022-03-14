package com.example.e2tech.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.Serializable;

public class ProductModel extends ViewModel implements Serializable {

    String name;
    int price;
    String description;
    double rating;
    double discount;
    String type;
    String img_url;
    String id;
    String company;

    int remain;

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    
    int numberOfReview;
    int numberOfPoint;


    public ProductModel(String name, int price, String description, double rating, double discount, String type, String img_url, String id, String company, int remain) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.discount = discount;
        this.type = type;
        this.img_url = img_url;
        this.id = id;
        this.company = company;
        this.remain = remain;
    }

    public ProductModel(String name, int price, String description, double rating, double discount, String type, String img_url, String id, String company) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.discount = discount;
        this.type = type;
        this.img_url = img_url;
        this.id = id;
        this.company = company;
    }

    public ProductModel(String name, int price, String description, double rating, double discount, String type, String img_url, String company) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.discount = discount;
        this.type = type;
        this.img_url = img_url;
        this.company = company;
    }

    public ProductModel(String name, int price, String description, String type, String img_url) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.type = type;
        this.img_url = img_url;
    }

    public ProductModel(String name, int price, String description, double rating, String img_url, String company) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.rating = rating;
        this.img_url = img_url;
        this.company = company;
    }

    public ProductModel(String name, int price, String type, String img_url) {
        this.name = name;
        this.price = price;
        this.type = type;
        this.img_url = img_url;
    }

    public ProductModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}