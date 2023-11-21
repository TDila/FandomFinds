package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.vulcan.fandomfinds.Adapter.CartAdapter;
import com.vulcan.fandomfinds.Helper.ChangeNumberitemsListener;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

public class CartActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;
    private TextView cart_subtotal,cart_delivery,cart_total_tax,cart_total,cart_empty_txt;
    private double tax;
    private ScrollView scrollView;
    private ImageView cart_back_btn,empty_cart_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        managementCart = new ManagementCart(this);

        initView();
        setVariable();
        calculateCart();
        initList();
    }

    private void initList() {
        if(managementCart.getListCart().isEmpty()){
            cart_empty_txt.setVisibility(View.VISIBLE);
            empty_cart_img.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
        }else{
            cart_empty_txt.setVisibility(View.GONE);
            empty_cart_img.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new CartAdapter(managementCart.getListCart(), this, new ChangeNumberitemsListener() {
            @Override
            public void change() {
                calculateCart();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void calculateCart() {
        double percentTax = 0.02;
        double delivery = 10;
        tax = Math.round(managementCart.getTotal()*percentTax*100.0)/100.0;

        double total = Math.round((managementCart.getTotal()+tax+delivery)*100)/100;
        double itemTotal = Math.round(managementCart.getTotal()*100)/100;

        cart_subtotal.setText("$"+itemTotal);
        cart_total_tax.setText("$"+tax);
        cart_delivery.setText("$"+delivery);
        cart_total.setText("$"+total);
    }

    private void setVariable() {
        cart_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        cart_subtotal = findViewById(R.id.cart_subtotal_value);
        cart_delivery = findViewById(R.id.cart_delivery_value);
        cart_total_tax = findViewById(R.id.cart_total_tax_value);
        cart_total = findViewById(R.id.cart_total_value);
        recyclerView = findViewById(R.id.cart_product_list);
        scrollView = findViewById(R.id.cart_scrollView);
        cart_back_btn = findViewById(R.id.cart_back_btn);
        cart_empty_txt = findViewById(R.id.cart_empty_txt);
        empty_cart_img = findViewById(R.id.empty_cart_img);
    }

}