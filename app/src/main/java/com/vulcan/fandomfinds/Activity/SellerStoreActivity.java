package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.SellerStoreItemAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.List;

public class SellerStoreActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    SellerDomain seller;
    String userString;
    private Button button;
    private LinearLayout ordersLayout,noMerchLayout,mapLayout,followersLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store);

        Intent intent = getIntent();
        userString = intent.getStringExtra("user");
        seller = (new Gson()).fromJson(userString, SellerDomain.class);

        initComponents();
        setListeners();
        loadStoreItems();
    }

    private void setListeners() {
        findViewById(R.id.sellerStoreBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerStoreActivity.this, SellerStoreSaveUpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                String userString = (new Gson()).toJson(seller);
                intent.putExtra("user",userString);
                startActivity(intent);
            }
        });
        ordersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerStoreActivity.this,OrdersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("user",userString);
                startActivity(intent);
            }
        });
        mapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerStoreActivity.this,MapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerStoreActivity.this,FollwersActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("user",userString);
                startActivity(intent);
            }
        });
    }

    public void initComponents(){
        button = findViewById(R.id.sellerStoreAddNewProductButton);
        ordersLayout = findViewById(R.id.ordersLayout);
        noMerchLayout = findViewById(R.id.noMerchLayout);
        noMerchLayout.setVisibility(View.GONE);
        mapLayout = findViewById(R.id.mapLayout);
        followersLayout = findViewById(R.id.followersLayout);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    private void loadStoreItems() {
        ArrayList<ProductsDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.sellerStoreItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(SellerStoreActivity.this,LinearLayoutManager.VERTICAL,false));
        SellerStoreItemAdapter sellerStoreItemAdapter = new SellerStoreItemAdapter(items,SellerStoreActivity.this);
        recyclerView.setAdapter(sellerStoreItemAdapter);
        firestore.collection("Products").whereEqualTo("sellerId",seller.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.size() == 0){
                        noMerchLayout.setVisibility(View.VISIBLE);
                    }
                    for (DocumentChange change : value.getDocumentChanges()){
                        ProductsDomain item = change.getDocument().toObject(ProductsDomain.class);
                        switch (change.getType()){
                            case ADDED:
                                items.add(item);
                            case MODIFIED:
                                ProductsDomain old = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().orElse(null);
                                if(old != null){
                                    old.setDescription(item.getDescription());
                                    old.setPrice(item.getPrice());
                                    old.setPicUrl(item.getPicUrl());
                                    old.setTitle(item.getTitle());
                                    old.setDiscount(item.getDiscount());
                                    old.setType(item.getType());
                                    old.setSizesList(item.getSizesList());
                                }
                                break;
                            case REMOVED:
                                items.remove(item);
                        }

                    sellerStoreItemAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}