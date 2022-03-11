package com.example.e2tech;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.e2tech.Models.ProductModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProductDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProductDetail extends Fragment {

    ProductModel product;

    ImageView imgProduct;
    EditText edtName, edtPrice, edtRemain, edtDescription, edtDiscount;




    public AdminProductDetail() {
        // Required empty public constructor
    }


    public static AdminProductDetail newInstance(String param1, String param2) {
        AdminProductDetail fragment = new AdminProductDetail();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = (ProductModel) getArguments().getSerializable("product");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_admin_product_detail, container, false);

        edtName = root.findViewById(R.id.edt_admin_product_detail_name);
        edtPrice = root.findViewById(R.id.edt_admin_product_detail_price);
        edtDiscount = root.findViewById(R.id.edt_admin_product_detail_discount);
        edtDescription = root.findViewById(R.id.edt_admin_product_detail_description);
        edtRemain = root.findViewById(R.id.edt_admin_product_detail_remain);
        imgProduct = root.findViewById(R.id.img_admin_product_detail);
        Glide.with(getActivity()).load(product.getImg_url()).into(imgProduct);

        edtName.setText(product.getName());
        edtPrice.setText(Integer.toString(product.getPrice()));
        edtDiscount.setText(Double.toString(product.getDiscount()));
        edtDescription.setText(product.getDescription());
        edtRemain.setText(Integer.toString(product.getRemain()));

        return root;
    }
}