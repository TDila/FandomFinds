package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.BaseDomain;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Enum.OrderStatus;
import com.vulcan.fandomfinds.R;

import java.util.HashMap;
import java.util.Map;

public class OrderViewActivity extends AppCompatActivity {
    private ImageView itemBackButton,itemProductImg;
    private LinearLayout itemSizeLayout;
    private TextView itemProductTitle,itemProductCount,itemTotalPrice,itemPurchasedDate,itemOrderId
            ,itemDeliveryAddress,itemOrderStatus,itemOrderSizeValue,customerPostalCode,customerEmail,customerPhone;
    private Button completeOrderButton;
    OrderDomain order;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);

        initComponents();
        setListeners();
        loadingDialog.show();
        loadOrder();
    }
    private void initComponents() {
        itemBackButton= findViewById(R.id.itemBackButton);
        itemProductImg = findViewById(R.id.itemProductImg);
        itemProductTitle = findViewById(R.id.itemProductTitle);
        itemProductCount = findViewById(R.id.itemProductCount);
        itemTotalPrice = findViewById(R.id.itemTotalPrice);
        itemPurchasedDate = findViewById(R.id.itemPurchasedDate);
        itemOrderId = findViewById(R.id.itemOrderId);
        itemSizeLayout = findViewById(R.id.itemSizeLayout);
        itemOrderSizeValue = findViewById(R.id.itemOrderSizeValue);
        itemDeliveryAddress = findViewById(R.id.itemDeliveryAddress);
        customerPostalCode = findViewById(R.id.customerPostalCodeValue);
        customerEmail  = findViewById(R.id.customerEmailValue);
        customerPhone = findViewById(R.id.customerPhoneValue);
        itemOrderStatus= findViewById(R.id.itemOrderStatus);
        completeOrderButton = findViewById(R.id.completeOrder);
        loadingDialog = new LoadingDialog(OrderViewActivity.this);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }
    private void setListeners() {
        itemBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        completeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                completeOrderStatus();
            }
        });
    }

    private void loadOrder() {
        Intent intent = getIntent();
        String orderString = intent.getStringExtra("order");
        order = (new Gson()).fromJson(orderString,OrderDomain.class);

        if(order.getStatus().equals(String.valueOf(OrderStatus.COMPLETED))){
            completeOrderButton.setVisibility(View.GONE);
        }else{
            completeOrderButton.setVisibility(View.VISIBLE);
        }

        firestore.collection("Orders").whereEqualTo("id",order.getId()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                OrderDomain order = snapshot.toObject(OrderDomain.class);
                                snapshot.getReference().collection("Product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()){
                                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                ProductsDomain product = snapshot1.toObject(ProductsDomain.class);
                                                itemProductTitle.setText(product.getTitle());
                                                firebaseStorage.getReference("product-images/"+product.getPicUrl())
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                Picasso.get()
                                                                        .load(uri)
                                                                        .into(itemProductImg);
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                int drawableId = getResources()
                                                                        .getIdentifier("product_default","drawable",getPackageName());

                                                                Glide.with(OrderViewActivity.this)
                                                                        .load(drawableId)
                                                                        .into(itemProductImg);
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                });
                                snapshot.getReference().collection("Customer").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                        BaseDomain customer = snapshot1.toObject(BaseDomain.class);
                                                        customerEmail.setText(customer.getEmail());
                                                        snapshot1.getReference().collection("Delivery-Payment").get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                                                BillingShippingDomain billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                                                                itemDeliveryAddress.setText(billingShipping.getShippingAddress());
                                                                                System.out.println(billingShipping.getMobileNumber());
                                                                                if(billingShipping.getMobileNumber() != null){
                                                                                    customerPhone.setText(billingShipping.getMobileNumber());
                                                                                }else{
                                                                                    customerPhone.setText("NONE");
                                                                                }
                                                                                customerPostalCode.setText(String.valueOf(billingShipping.getPostalCode()));
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
        if(order.getSelectedSize() != null && !order.getSelectedSize().isEmpty()){
            itemSizeLayout.setVisibility(View.VISIBLE);
            itemOrderSizeValue.setText(order.getSelectedSize());
        }else{
            itemSizeLayout.setVisibility(View.GONE);
        }
        itemProductCount.setText("Item Count : "+order.getItemCount());
        itemTotalPrice.setText("$"+order.getTotalPrice());
        itemPurchasedDate.setText(order.getDateTime());
        itemOrderId.setText(order.getId());
        itemOrderStatus.setText(String.valueOf(order.getStatus()));
        loadingDialog.cancel();
    }

    private void completeOrderStatus() {
        if (order != null){
            Map<String,Object> map = new HashMap<>();
            map.put("status", OrderStatus.COMPLETED);
            firestore.collection("Orders").whereEqualTo("id",order.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()){
                        for (QueryDocumentSnapshot snapshot : task.getResult()){
                            snapshot.getReference().update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    loadingDialog.cancel();
                                    Toast.makeText(OrderViewActivity.this,"Successfully Updated!",Toast.LENGTH_LONG).show();
                                    itemOrderStatus.setText(String.valueOf(OrderStatus.COMPLETED));
                                    completeOrderButton.setEnabled(false);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.cancel();
                                    Toast.makeText(OrderViewActivity.this,"Order Status Updating Failed!",Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingDialog.cancel();
                    Toast.makeText(OrderViewActivity.this,"Order Status Updating Failed!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}