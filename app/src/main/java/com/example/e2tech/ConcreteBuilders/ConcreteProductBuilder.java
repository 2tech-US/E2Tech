package com.example.e2tech.ConcreteBuilders;

import com.example.e2tech.Builders.ProductBuilder;
import com.example.e2tech.Models.ProductModel;

public class ConcreteProductBuilder implements ProductBuilder {

    String name;
    int price;
    String description;
    double rating;
    double discount;
    String type;
    String img_url;
    String id;
    String company;
    int quantityOnOrder;
    int remain;
    int numberOfReview;
    int numberOfPoint;
    int numberSold;

    @Override
    public ProductBuilder productName(String productNames) {
        this.name = productNames;
        return this;
    }

    @Override
    public ProductBuilder productPrice(int price) {
        this.price = price;
        return this;
    }

    @Override
    public ProductBuilder productDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public ProductBuilder productRating(double rating) {
        this.rating = rating;
        return this;
    }

    @Override
    public ProductBuilder productDiscount(double discount) {
        this.discount = discount;
        return this;
    }

    @Override
    public ProductBuilder productType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public ProductBuilder productImage(String img_url) {
        this.img_url = img_url;
        return this;
    }

    @Override
    public ProductBuilder productID(String id) {
        this.id = id;
        return this;
    }

    @Override
    public ProductBuilder productCompany(String company) {
        this.company = company;
        return this;
    }

    @Override
    public ProductBuilder productRemain(int remain) {
        this.remain = remain;
        return this;
    }

    @Override
    public ProductBuilder productQuantityOnOrder(int quantity) {
        this.quantityOnOrder = quantity;
        return this;
    }

    @Override
    public ProductBuilder productNumberReview(int numberReview) {
        this.numberOfReview = numberReview;
        return this;
    }

    @Override
    public ProductBuilder productNumberPoint(int numberPoint) {
        this.numberOfPoint = numberPoint;
        return this;
    }

    @Override
    public ProductBuilder productNumberSold(int numberSold) {
        this.numberSold = numberSold;
        return this;
    }

    @Override
    public ProductModel build() {
        return new ProductModel(name, price, description, rating, discount, type, img_url, id, company, quantityOnOrder, remain, numberOfReview, numberOfPoint, numberSold);
    }
}
