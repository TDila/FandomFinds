package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Adapter.ExploreProductsAdapter;
import com.vulcan.fandomfinds.Adapter.ExploreSellerAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    FirebaseFirestore firestore;
    private EditText exploreSearchBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        initComponents();
        setListeners();

        loadSellers();
        loadProducts();

        Intent intent = getIntent();
        String searchText = intent.getStringExtra("text");
        if(isNotEmpty(searchText)){
            exploreSearchBar.setText(searchText);
            search(searchText);
        }
    }

    private void initComponents() {
        firestore = FirebaseFirestore.getInstance();
        exploreSearchBar = findViewById(R.id.explore_search_bar2);
    }

    private void setListeners() {
        ImageView back_button = findViewById(R.id.explore_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        exploreSearchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(isNotEmpty(v.getText().toString())){
                        search(v.getText().toString());
                    }else{
                        loadSellers();
                        loadProducts();
                    }
                    return true;
                }
                return false;
            }
        });
        exploreSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isNotEmpty(s.toString())){
                    loadProducts();
                    loadSellers();
                }
            }
        });
    }

    private boolean isNotEmpty(String value){
        if(value != null && !value.isEmpty()){
            return true;
        }
        return false;
    }

    private void search(String text) {
        ArrayList<SellerDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.explore_seller_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreSellerAdapter sellerAdapter = new ExploreSellerAdapter(items,ExploreActivity.this);
        recyclerView.setAdapter(sellerAdapter);

        firestore.collection("Sellers").limit(3).where(Filter.or(Filter.equalTo("sellerName",text),Filter.equalTo("sellerNameInsensitive",text))).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                SellerDomain seller = snapshot.toObject(SellerDomain.class);
                                items.add(new SellerDomain(seller.getId(),seller.getEmail(),seller.getFname(),seller.getLname(),seller.getProfilePicUrl(),seller.getFollowers(),seller.getSellerName(),seller.getBio(),seller.getSellerNameInsensitive()));
                            }
                            sellerAdapter.notifyDataSetChanged();
                        }
                    }
                });

        ArrayList<ProductsDomain> itemsProduct = new ArrayList<>();

        RecyclerView recyclerViewProduct = findViewById(R.id.explore_product_list);
        recyclerViewProduct.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreProductsAdapter exploreProductsAdapter = new ExploreProductsAdapter(itemsProduct,ExploreActivity.this);
        recyclerViewProduct.setAdapter(exploreProductsAdapter);

        firestore.collection("Products").where(Filter.or(Filter.equalTo("title",text), Filter.equalTo("titleInsensitive",text))).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        ProductsDomain product = snapshot.toObject(ProductsDomain.class);
                        if(product != null){
                            if(product.getType().equals("Apparel")){
                                List<String> sizes = product.getSizesList();
                                itemsProduct.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                        ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),sizes,product.getStatus(),product.getType(),product.getSellerId()));
                            }else{
                                itemsProduct.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                        ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),product.getSizesList(),product.getStatus(),product.getType(),product.getSellerId()));
                            }
                        }
                    }
                    exploreProductsAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExploreActivity.this,"Product Loading Failure!",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSellers() {
        ArrayList<SellerDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.explore_seller_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreSellerAdapter sellerAdapter = new ExploreSellerAdapter(items,ExploreActivity.this);
        recyclerView.setAdapter(sellerAdapter);

        firestore.collection("Sellers").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()){
                    SellerDomain item = change.getDocument().toObject(SellerDomain.class);
                    switch (change.getType()){
                        case ADDED:
                            items.add(item);
                        case MODIFIED:
                            SellerDomain old = items.stream().filter(i -> i.getId().equals(item.getId())).findFirst().orElse(null);
                            if(old != null){
                                old.setSellerName(item.getSellerName());
                                old.setFname(item.getFname());
                                old.setLname(item.getLname());
                                old.setBio(item.getBio());
                                old.setFollowers(item.getFollowers());
                                old.setProfilePicUrl(item.getProfilePicUrl());
                            }
                            break;
                        case REMOVED:
                            items.remove(item);
                    }
                }
                sellerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadProducts() {
        ArrayList<ProductsDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.explore_product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreProductsAdapter exploreProductsAdapter = new ExploreProductsAdapter(items,ExploreActivity.this);
        recyclerView.setAdapter(exploreProductsAdapter);

        firestore.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    exploreProductsAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExploreActivity.this,"Product Loading Failure!",Toast.LENGTH_LONG).show();
            }
        });
    }
}