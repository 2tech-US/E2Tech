package com.example.e2tech.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class OrderModel implements Serializable {

    String image, receiverName, address, createAt, id, note, phone, status, orderBy, userName, nameProducts;
    int fee_ship, subTotal, total, quantity;

    public ArrayList<ProductModel> getProductList() {
        return productList;
    }

    public void setProductList(ArrayList<ProductModel> productList) {
        this.productList = productList;
    }

    ArrayList<ProductModel> productList;

    public OrderModel(String receiverName, String address, String createAt, String id, String note, String phone, String status, String orderBy, String userName, String nameProducts, int fee_ship, int subTotal, int total, int quantity, ArrayList<ProductModel> productList) {
        this.receiverName = receiverName;
        this.address = address;
        this.createAt = createAt;
        this.id = id;
        this.note = note;
        this.phone = phone;
        this.status = status;
        this.orderBy = orderBy;
        this.userName = userName;
        this.nameProducts = nameProducts;
        this.fee_ship = fee_ship;
        this.subTotal = subTotal;
        this.total = total;
        this.quantity = quantity;
        this.productList = productList;
    }

    public OrderModel(String id, String address, String createAt, String nameProducts, int total, int quantity) {
        this.address = address;
        this.createAt = createAt;
        this.id = id;
        this.nameProducts = nameProducts;
        this.total = total;
        this.quantity = quantity;
    }

    public String getNameProducts() {
        return nameProducts;
    }

    public void setNameProducts(String nameProducts) {
        this.nameProducts = nameProducts;
    }

    public OrderModel(String receiverName, String address, String createAt, String id, String note, String phone, String status, String orderBy, String userName, int total, int quantity) {
        this.receiverName = receiverName;
        this.address = address;
        this.createAt = createAt;
        this.id = id;
        this.note = note;
        this.phone = phone;
        this.status = status;
        this.orderBy = orderBy;
        this.userName = userName;
        this.fee_ship = 0;
//        this.subTotal = subTotal;
        this.total = total;
        this.quantity = quantity;
    }

    public OrderModel() {
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getImage() { return image;}

    public void setImage(String image) { this.image = image; }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getFee_ship() {
        return fee_ship;
    }

    public void setFee_ship(int fee_ship) {
        this.fee_ship = fee_ship;
    }

    public int getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(int subTotal) {
        this.subTotal = subTotal;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }



    // Builder class
    public static class OrderBuilder {
        String receiverName, address, createAt, id, note, phone, status, orderBy, userName, nameProducts;
        int fee_ship, subTotal, total, quantity;
        ArrayList<ProductModel> productList;

        public OrderBuilder receiver(String receiverName) {
            this.receiverName = receiverName;
            return this;
        }

        public OrderBuilder createAt(String createAt) {
            this.createAt = createAt;
            return this;
        }

        public OrderBuilder id(String id) {
            this.id = id;
            return this;
        }

        public OrderBuilder note(String note) {
            this.note = note;
            return this;
        }

        public OrderBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public OrderBuilder status(String status) {
            this.status = status;
            return this;
        }

        public OrderBuilder orderByID(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public OrderBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public OrderBuilder productNames(String nameProducts) {
            this.nameProducts = nameProducts;
            return this;
        }

        public OrderBuilder address(String address) {
            this.address = address;
            return this;
        }

        public OrderBuilder feeShip(int fee) {
            this.fee_ship = fee;
            return this;
        }

        public OrderBuilder subTotal(int subTotal) {
            this.subTotal = subTotal;
            return this;
        }

        public OrderBuilder total(int total) {
            this.total = total;
            return this;
        }

        public OrderBuilder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public OrderBuilder productList(ArrayList<ProductModel> productList) {
            this.productList = productList;
            return this;
        }


        public OrderModel build() {
            return new OrderModel(receiverName, address, createAt, id, note, phone, status, orderBy, userName, nameProducts, fee_ship, subTotal, total, quantity, productList);
        }

    }


}
