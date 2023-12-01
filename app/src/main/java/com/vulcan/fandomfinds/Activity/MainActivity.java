package com.vulcan.fandomfinds.Activity;

import androidx.annotation.ArrayRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Adapter.SellerAdapter;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Fragments.bottomNavigation;
import com.vulcan.fandomfinds.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    FirebaseUser user;
    private String userEmail;
    private CustomerDomain customer;
    private SellerDomain seller;
    private TextView username,signUpInButton;
    private LinearLayout signUpInLayout;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        initComponents();
        signUpInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSignUpInActivity();
            }
        });

        if(user == null){
            signUpInLayout.setVisibility(View.VISIBLE);
        }else{
            signUpInLayout.setVisibility(View.GONE);
            loadingDialog.show();
            searchUserProcess();
        }

        initNewArrivalRecyclerView();
        initDealsRecyclerView();
        initSellerRecyclerView();
        loadBottomNavigationBar();
    }

    private void searchUserProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                customer = snapshot.toObject(CustomerDomain.class);
                                if(customer != null) {
                                    if(customer.getFname() != null && customer.getLname() != null){
                                        username.setText(customer.getFname()+" "+customer.getLname());
                                        loadingDialog.cancel();
                                        return;
                                    }else{
                                        username.setText(customer.getEmail());
                                        loadingDialog.cancel();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                });
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                seller = snapshot.toObject(SellerDomain.class);
                                if(seller.getSellerName() != null){
                                    username.setText(seller.getSellerName());
                                    loadingDialog.cancel();
                                    return;
                                }else{
                                    username.setText(seller.getEmail());
                                    loadingDialog.cancel();
                                    return;
                                }
                            }
                        }
                    }
                });
    }

    private void initComponents() {
        username = findViewById(R.id.username);
        signUpInButton = findViewById(R.id.signUpInButton);
        signUpInLayout = findViewById(R.id.signUpInLayout);
        loadingDialog = new LoadingDialog(MainActivity.this);
    }

    private void loadSignUpInActivity() {
        Intent intent = new Intent(MainActivity.this,SignUpInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void loadBottomNavigationBar() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView3, bottomNavigation.class,null)
                .commit();
    }

    private void initSellerRecyclerView(){
        ArrayList<SellerDomain> items = new ArrayList<>();

        RecyclerView recyclerViewSeller = findViewById(R.id.featured);
        recyclerViewSeller.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        SellerAdapter adapterSeller = new SellerAdapter(items);
        recyclerViewSeller.setAdapter(adapterSeller);

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
                adapterSeller.notifyDataSetChanged();
            }
        });
    }

    private void initNewArrivalRecyclerView(){
        String sizes[] = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","This is a T-shirt black","item_4",7,4.5,500,0,"Maniya Streams",null,"Apparel"));
        items.add(new ProductsDomain("Apple Watch","This is a Apple Watch","item_2",14,3,200,0,"Maniya Streams",sizes,"Collectibles"));
        items.add(new ProductsDomain("Mobile Phone","This is a Mobile Phone","item_3",9,2.1,1200,0,"Maniya Streams",null,"Other"));
        items.add(new ProductsDomain("Samsung TV","This is a Samsung TV","item_4",1,3.4,670,0,"Maniya Streams",sizes,"Gaming Gear"));

        RecyclerView recyclerViewNewArrival = findViewById(R.id.new_arrival);
        recyclerViewNewArrival.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        DealsAdapter dealsAdapter = new DealsAdapter(items);
        recyclerViewNewArrival.setAdapter(dealsAdapter);
    }

    private void initDealsRecyclerView(){
        String[] sizes = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",null,"Apparel"));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2,"Maniya Streams",null,"Apparel"));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2.3,"Maniya Streams",sizes,"Apparel"));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,5.7,"Maniya Streams",sizes,"Apparel"));

        RecyclerView recyclerViewDeals = findViewById(R.id.deals);
        recyclerViewDeals.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        DealsAdapter dealsAdapter = new DealsAdapter(items);
        recyclerViewDeals.setAdapter(dealsAdapter);
    }
}