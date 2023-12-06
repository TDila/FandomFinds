package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.NotificationAdapter;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.NotificationDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {
    ImageView backButton;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    SellerDomain seller;
    CustomerDomain customer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        
        initComponents();
        setListeners();
        loadUser();
        identifyUser();
    }

    private void initComponents() {
        backButton = findViewById(R.id.notify_back_btn);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }
    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void loadUser(){
        Intent intent = getIntent();
        String sellerString = intent.getStringExtra("seller");
        String customerString = intent.getStringExtra("customer");
        if(sellerString != null){
            seller = (new Gson()).fromJson(sellerString,SellerDomain.class);
        }else if(customerString != null){
            customer = (new Gson()).fromJson(customerString,CustomerDomain.class);
        }
    }
    private void identifyUser() {
        if(seller != null){
            loadNotification("Sellers");
        }
        if(customer != null){
            loadNotification("Customers");
        }
    }

    private void loadNotification(String collectionName){
        ArrayList<NotificationDomain> notifications = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.notificationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this,LinearLayoutManager.VERTICAL,false));
        NotificationAdapter adapter = new NotificationAdapter(notifications,NotificationsActivity.this);
        recyclerView.setAdapter(adapter);
        firestore.collection(collectionName).whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        snapshot.getReference().collection("Notifications").orderBy("dateTime", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                for (DocumentChange change : value.getDocumentChanges()){
                                    NotificationDomain notification = change.getDocument().toObject(NotificationDomain.class);
                                    switch (change.getType()){
                                        case ADDED:
                                            notifications.add(notification);
                                            break;
                                        case MODIFIED:
                                            break;
                                        case REMOVED:
                                            notifications.remove(notification);
                                            break;
                                    }
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });
    }
}