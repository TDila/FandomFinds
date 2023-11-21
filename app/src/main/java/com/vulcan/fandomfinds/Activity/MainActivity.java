package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Adapter.NewArrivalAdapter;
import com.vulcan.fandomfinds.Adapter.SellerAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterNewArrival;
    private RecyclerView recyclerViewNewArrival;
    private RecyclerView.Adapter adapterDeals;
    private RecyclerView recyclerViewDeals;

    private RecyclerView.Adapter adapterSeller;
    private RecyclerView recyclerViewSeller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNewArrivalRecyclerView();
        initDealsRecyclerView();
        initSellerRecyclerView();
        bottomNavigation();
    }

    private void bottomNavigation() {
        LinearLayout home_buttom = findViewById(R.id.bottom_nav_home);
        LinearLayout cart_buttom = findViewById(R.id.bottom_nav_cart);
        LinearLayout cart_account = findViewById(R.id.bottom_nav_account);

        home_buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });

        cart_buttom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CartActivity.class));
            }
        });

        cart_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SignUpInActivity.class));
            }
        });
    }

    private void initSellerRecyclerView(){
        ArrayList<SellerDomain> items = new ArrayList<>();
        items.add(new SellerDomain("Maniya Streams","maniya_streams_foreground"));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round"));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round"));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round"));

        recyclerViewSeller = findViewById(R.id.featured);
        recyclerViewSeller.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterSeller = new SellerAdapter(items);
        recyclerViewSeller.setAdapter(adapterSeller);
    }

    private void initNewArrivalRecyclerView(){
        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","This is a T-shirt black","item_4",7,4.5,500,0,"Maniya Streams"));
        items.add(new ProductsDomain("Apple Watch","This is a Apple Watch","item_2",14,3,200,0,"Maniya Streams"));
        items.add(new ProductsDomain("Mobile Phone","This is a Mobile Phone","item_3",9,2.1,1200,0,"Maniya Streams"));
        items.add(new ProductsDomain("Samsung TV","This is a Samsung TV","item_4",1,3.4,670,0,"Maniya Streams"));

        recyclerViewNewArrival = findViewById(R.id.new_arrival);
        recyclerViewNewArrival.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterNewArrival = new NewArrivalAdapter(items);
        recyclerViewNewArrival.setAdapter(adapterNewArrival);
    }

    private void initDealsRecyclerView(){
        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams"));

        recyclerViewDeals = findViewById(R.id.deals);
        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterDeals = new DealsAdapter(items);
        recyclerViewDeals.setAdapter(adapterDeals);
    }
}