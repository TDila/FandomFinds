package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vulcan.fandomfinds.Adapter.ExploreProductsAdapter;
import com.vulcan.fandomfinds.Adapter.ExploreSellerAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.List;

public class ExploreActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        ImageView back_button = findViewById(R.id.explore_back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadSellers();
        loadProducts();
    }

    private void loadSellers() {
        ArrayList<SellerDomain> items = new ArrayList<>();
        items.add(new SellerDomain("ManiYa Streams","maniya_streams_round",444));
        items.add(new SellerDomain("ManiYa Streams","maniya_streams_round",444));
        items.add(new SellerDomain("ManiYa Streams","maniya_streams_round",444));

        RecyclerView recyclerView = findViewById(R.id.explore_seller_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreSellerAdapter sellerAdapter = new ExploreSellerAdapter(items,ExploreActivity.this);
        recyclerView.setAdapter(sellerAdapter);
    }

    private void loadProducts() {
        String[] sizes = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",null));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",sizes));

        RecyclerView recyclerView = findViewById(R.id.explore_product_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreActivity.this,LinearLayoutManager.VERTICAL,false));
        ExploreProductsAdapter exploreProductsAdapter = new ExploreProductsAdapter(items,ExploreActivity.this);
        recyclerView.setAdapter(exploreProductsAdapter);
    }
}