package com.example.e2tech.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.e2tech.Models.ProductModel;

import java.util.List;

public class ProductViewModel extends ViewModel {

    private final MutableLiveData<List<ProductModel>> mutableProduct = new MutableLiveData<>();

    public void setProducts(List<ProductModel> input) {
        mutableProduct.setValue(input);
    }

    public LiveData<List<ProductModel>> getProducts() {
        return mutableProduct;
    }
}
