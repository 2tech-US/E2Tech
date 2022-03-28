package com.example.e2tech.Builders;

import com.example.e2tech.Models.ProductModel;

public interface ProductBuilder {

    ProductBuilder productName(String productNames);
    ProductBuilder productPrice(int price);
    ProductBuilder productDescription(String description);
    ProductBuilder productRating(double rating);
    ProductBuilder productDiscount(double discount);
    ProductBuilder productType(String type);
    ProductBuilder productImage(String img_url);
    ProductBuilder productID(String id);
    ProductBuilder productCompany(String company);
    ProductBuilder productRemain(int remain);
    ProductBuilder productQuantityOnOrder(int quantity);
    ProductBuilder productNumberReview(int numberReview);
    ProductBuilder productNumberPoint(int numberPoint);
    ProductBuilder productNumberSold(int numberSold);

    ProductModel build();

}
