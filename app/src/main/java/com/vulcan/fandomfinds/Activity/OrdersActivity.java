package com.vulcan.fandomfinds.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.OrdersListAdapter;
import com.vulcan.fandomfinds.Adapter.PurchasedHistoryListAdapter;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Enum.OrderStatus;
import com.vulcan.fandomfinds.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class OrdersActivity extends AppCompatActivity {
    private LinearLayout noNewOrders,noCompletedOrders;
    private ImageView ordersBackButton;
    FirebaseFirestore firestore;
    SellerDomain seller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        initComponents();
        setListeners();
        loadUser();
        loadNewOrders();
        loadCompletedOrders();
    }
    private void loadUser() {
        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        seller = (new Gson()).fromJson(userString,SellerDomain.class);
    }
    private void setListeners() {
        ordersBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initComponents() {
        noNewOrders = findViewById(R.id.noNewOrders);
        noCompletedOrders = findViewById(R.id.noCompletedOrders);
        ordersBackButton = findViewById(R.id.ordersBackButton);

        firestore = FirebaseFirestore.getInstance();
    }
    private void loadNewOrders() {
        ArrayList<OrderDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.newOrdersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrdersActivity.this,LinearLayoutManager.VERTICAL,false));
        OrdersListAdapter adapter = new OrdersListAdapter(items,OrdersActivity.this);
        recyclerView.setAdapter(adapter);

        firestore.collection("Orders").whereEqualTo("sellerId",seller.getId()).whereEqualTo("status", OrderStatus.ONGOING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.size() == 0){
                    noNewOrders.setVisibility(View.VISIBLE);
                }else{
                    noNewOrders.setVisibility(View.GONE);
                }
                for (DocumentChange change : value.getDocumentChanges()){
                    OrderDomain order = change.getDocument().toObject(OrderDomain.class);
                    switch (change.getType()){
                        case ADDED:
                            System.out.println("loadNewOrders "+order.getId());
                            items.add(order);
                            break;
                        case MODIFIED:
                            OrderDomain old = items.stream().filter(i -> i.getId().equals(order.getId())).findFirst().orElse(null);
                            if(old != null){
                                old.setStatus(order.getStatus());
                            }
                            break;
                        case REMOVED:
                            items.remove(order);
                            break;
                    }
                }
                adapter.notifyDataSetChanged();

            }
        });
    }

    private void loadCompletedOrders() {
        ArrayList<OrderDomain> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.completedOrdersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrdersActivity.this,LinearLayoutManager.VERTICAL,false));
        OrdersListAdapter adapter = new OrdersListAdapter(items,OrdersActivity.this);
        recyclerView.setAdapter(adapter);

        firestore.collection("Orders").whereEqualTo("sellerId",seller.getId()).whereEqualTo("status", OrderStatus.COMPLETED).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.size() == 0){
                    noCompletedOrders.setVisibility(View.VISIBLE);
                }else{
                    noCompletedOrders.setVisibility(View.GONE);
                }
                for (DocumentChange change : value.getDocumentChanges()){
                    OrderDomain order = change.getDocument().toObject(OrderDomain.class);
                    switch (change.getType()){
                        case ADDED:
                            System.out.println("loadCompleteOrders added : "+order.getId());
                            items.add(order);
                            break;
                        case MODIFIED:
                            OrderDomain old = items.stream().filter(i -> i.getId().equals(order.getId())).findFirst().orElse(null);
                            if(old != null){
                                System.out.println("loadCompleteOrders "+order.getId());
                                System.out.println("loadCompleteOrders order : "+order.getStatus());
                                System.out.println("loadCompleteOrders old : "+old.getStatus());
                                old.setStatus(order.getStatus());
                                System.out.println("loadCompleteOrders "+old.getStatus());
                            }
                            break;
                        case REMOVED:
                            System.out.println("loadCompleteOrders remove : "+order.getId());
                            items.remove(order);
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}