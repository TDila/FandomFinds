package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.vulcan.fandomfinds.Adapter.SellerStoreItemAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerStoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_store);

        findViewById(R.id.sellerStoreBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadStoreItems();
    }

    private void loadStoreItems() {
        String sizes[] = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","This is a T-shirt black","item_4",7,4.5,500,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Apple Watch","This is a Apple Watch","item_2",14,3,200,10,"Maniya Streams",sizes));
        items.add(new ProductsDomain("Mobile Phone","This is a Mobile Phone","item_3",9,2.1,1200,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Samsung TV","This is a Samsung TV","item_4",1,3.4,670,8.3,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","This is a T-shirt black","item_4",7,4.5,500,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Apple Watch","This is a Apple Watch","item_2",14,3,200,10,"Maniya Streams",sizes));
        items.add(new ProductsDomain("Mobile Phone","This is a Mobile Phone","item_3",9,2.1,1200,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Samsung TV","This is a Samsung TV","item_4",1,3.4,670,8.3,"Maniya Streams",sizes));

        RecyclerView recyclerView = findViewById(R.id.sellerStoreItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(SellerStoreActivity.this,LinearLayoutManager.VERTICAL,false));
        SellerStoreItemAdapter sellerStoreItemAdapter = new SellerStoreItemAdapter(items,SellerStoreActivity.this);
        recyclerView.setAdapter(sellerStoreItemAdapter);
    }
}