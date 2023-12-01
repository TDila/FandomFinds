package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Adapter.CartAdapter;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Fragments.bottomNavigation;
import com.vulcan.fandomfinds.Helper.ChangeNumberitemsListener;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

public class CartActivity extends AppCompatActivity{
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private ManagementCart managementCart;
    private TextView cart_subtotal,cart_delivery,cart_total_tax,cart_total,cart_empty_txt,cart_delivery_address_value,cart_payment_method_value;
    private double tax;
    private ScrollView scrollView;
    private ImageView cart_back_btn,empty_cart_img,billingShipping_address,billingShipping_paymentMethod;
    private LinearLayout cartErrorMessageLayout;
    FirebaseFirestore firestore;
    CustomerDomain customer;
    FirebaseAuth firebaseAuth;
    SellerDomain seller;
    FirebaseUser user;
    BillingShippingDomain billingShipping;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        managementCart = new ManagementCart(this);

        initView();
        loadingDialog.show();
        setVariable();
        calculateCart();
        initList();
        loadBottomNavigationBar();
        searchUserProcess();

        billingShipping_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBillingShipping();
            }
        });

        billingShipping_paymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBillingShipping();
            }
        });
    }

    private void loadBillingShipping(){
        Intent intent = new Intent(CartActivity.this,BillingShippingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(customer != null){
            intent.putExtra("customerId",customer.getId());
        }else if(seller != null){
            intent.putExtra("sellerId",seller.getId());
        }
        startActivity(intent);
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
        billingShipping_address = findViewById(R.id.cart_to_billingShipping_address);
        billingShipping_paymentMethod = findViewById(R.id.cart_to_billingShipping_paymentMethod);
        cartErrorMessageLayout = findViewById(R.id.cartErrorMessageLayout);
        cartErrorMessageLayout.setVisibility(View.GONE);
        loadingDialog = new LoadingDialog(CartActivity.this);
        cart_delivery_address_value = findViewById(R.id.cart_delivery_address_value);
        cart_payment_method_value = findViewById(R.id.cart_payment_method_value);
    }
    private void loadBottomNavigationBar(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView2, bottomNavigation.class,null)
                .commit();
    }

    private void searchUserProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                customer = snapshot.toObject(CustomerDomain.class);
                                getBillingShippingDetails(snapshot);
                            }
                        }
                    }
                });
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                seller = snapshot.toObject(SellerDomain.class);
                                getBillingShippingDetails(snapshot);
                            }
                        }
                    }
                });
    }

    public void getBillingShippingDetails(QueryDocumentSnapshot snapshot){
        snapshot.getReference().collection("Billing-Shipping").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                initErrorMessage();
                                loadBillingDetails();
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CartActivity.this,"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                        loadingDialog.cancel();
                    }
                });
    }
    public void initErrorMessage(){
        if(billingShipping != null){
            if(billingShipping.getShippingAddress() == null || billingShipping.getPostalCode() == 0 || billingShipping.getPaymentMethod() == null){
                cartErrorMessageLayout.setVisibility(View.VISIBLE);
            }else{
                cartErrorMessageLayout.setVisibility(View.GONE);
            }
        }else{
            cartErrorMessageLayout.setVisibility(View.VISIBLE);
        }
    }

    private void loadBillingDetails() {
        if(billingShipping != null){
            if(billingShipping.getShippingAddress() != null){
                cart_delivery_address_value.setText(billingShipping.getShippingAddress());
            }
            if(billingShipping.getPaymentMethod() != null){
                cart_payment_method_value.setText(billingShipping.getPaymentMethod());
            }
        }
        loadingDialog.cancel();
    }

}