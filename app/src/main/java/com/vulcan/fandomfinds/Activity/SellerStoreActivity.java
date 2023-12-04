package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store);

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

        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        seller = (new Gson()).fromJson(userString, SellerDomain.class);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SellerStoreActivity.this, SellerStoreSaveUpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("user",userString);
                startActivity(intent);
                finish();
            }
        });
    }

    public void initComponents(){
        button = findViewById(R.id.sellerStoreAddNewProductButton);

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
        firestore.collection("Products").whereEqualTo("sellerId",seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        ProductsDomain product = snapshot.toObject(ProductsDomain.class);
                        if(product != null){
                            if(product.getType().equals("Apparel")){
                                List<String> sizes = product.getSizesList();
                                items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                        ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),sizes,product.getStatus(),product.getType(),product.getSellerId()));
                            }else{
                                items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                        ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),product.getSizesList(),product.getStatus(),product.getType(),product.getSellerId()));
                            }
                        }
                    }
                    sellerStoreItemAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SellerStoreActivity.this,"Product Loading Failure!",Toast.LENGTH_LONG).show();
            }
        });
    }
}