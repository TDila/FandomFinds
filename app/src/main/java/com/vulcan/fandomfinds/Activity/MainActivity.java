package com.vulcan.fandomfinds.Activity;

import androidx.annotation.ArrayRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Adapter.SellerAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Fragments.bottomNavigation;
import com.vulcan.fandomfinds.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNewArrivalRecyclerView();
        initDealsRecyclerView();
        initSellerRecyclerView();
        loadBottomNavigationBar();
    }

    private void loadBottomNavigationBar() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView3, bottomNavigation.class,null)
                .commit();
    }

    private void initSellerRecyclerView(){
        ArrayList<SellerDomain> items = new ArrayList<>();
        items.add(new SellerDomain("Maniya Streams","maniya_streams_foreground",444));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round",444));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round",444));
        items.add(new SellerDomain("Maniya Streams","maniya_streams_round",444));

        RecyclerView recyclerViewSeller = findViewById(R.id.featured);
        recyclerViewSeller.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        SellerAdapter adapterSeller = new SellerAdapter(items);
        recyclerViewSeller.setAdapter(adapterSeller);
    }

    private void initNewArrivalRecyclerView(){
        String sizes[] = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","This is a T-shirt black","item_4",7,4.5,500,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Apple Watch","This is a Apple Watch","item_2",14,3,200,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("Mobile Phone","This is a Mobile Phone","item_3",9,2.1,1200,0,"Maniya Streams",null));
        items.add(new ProductsDomain("Samsung TV","This is a Samsung TV","item_4",1,3.4,670,0,"Maniya Streams",sizes));

        RecyclerView recyclerViewNewArrival = findViewById(R.id.new_arrival);
        recyclerViewNewArrival.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        DealsAdapter dealsAdapter = new DealsAdapter(items);
        recyclerViewNewArrival.setAdapter(dealsAdapter);
    }

    private void initDealsRecyclerView(){
        String[] sizes = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",null));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2,"Maniya Streams",null));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2.3,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,5.7,"Maniya Streams",sizes));

        RecyclerView recyclerViewDeals = findViewById(R.id.deals);
        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        DealsAdapter dealsAdapter = new DealsAdapter(items);
        recyclerViewDeals.setAdapter(dealsAdapter);
    }
}