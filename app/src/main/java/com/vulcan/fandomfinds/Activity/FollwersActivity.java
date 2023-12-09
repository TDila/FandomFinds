package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.FollowersAdapter;
import com.vulcan.fandomfinds.Adapter.SellerStoreItemAdapter;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.Follower;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class FollwersActivity extends AppCompatActivity {

    private ImageView followersBackButton;
    private TextView followersCount;
    SellerDomain seller;
    FirebaseFirestore firestore;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follwers);

        initComponents();
        setListeners();

        loadingDialog.show();
        followersCount.setText(String.valueOf(seller.getFollowers()));
        LoadFollowers();
        updateFollowerCount();
    }

    private void updateFollowerCount() {
        firestore.collection("Sellers").whereEqualTo("id",seller.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    for(DocumentChange change : value.getDocumentChanges()){
                        SellerDomain sellerDomain = change.getDocument().toObject(SellerDomain.class);
                        switch (change.getType()){
                            case MODIFIED:
                                followersCount.setText(String.valueOf(sellerDomain.getFollowers()));
                                break;
                        }
                    }
                }
            }
        });
    }

    private void LoadFollowers() {
        ArrayList<Follower> items = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.followersList);
        recyclerView.setLayoutManager(new LinearLayoutManager(FollwersActivity.this,LinearLayoutManager.VERTICAL,false));
        FollowersAdapter followersAdapter = new FollowersAdapter(items,FollwersActivity.this);
        recyclerView.setAdapter(followersAdapter);
        firestore.collection("Sellers").whereEqualTo("id",seller.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                        snapshot.getReference().collection("Followers").addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if(value != null){
                                    for(DocumentChange change : value.getDocumentChanges()){
                                        Follower follower = change.getDocument().toObject(Follower.class);
                                        switch (change.getType()){
                                            case ADDED:
                                                items.add(follower);
                                                break;
                                            case REMOVED:
                                                items.remove(follower);
                                                break;
                                        }
                                    }
                                    followersAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                    loadingDialog.cancel();
                }
            }
        });
    }

    private void setListeners() {
        followersBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initComponents() {

        Intent intent = getIntent();
        String userString = intent.getStringExtra("user");
        seller = (new Gson()).fromJson(userString, SellerDomain.class);

        followersBackButton = findViewById(R.id.followersBackButton);
        followersCount = findViewById(R.id.followersCount);

        firestore = FirebaseFirestore.getInstance();
        loadingDialog = new LoadingDialog(FollwersActivity.this);

    }
}