package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Fragments.bottomNavigation;
import com.vulcan.fandomfinds.R;

import org.w3c.dom.Text;

public class AccountActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    private ImageView accountBackButton,accountProfileImg;
    private CustomerDomain customer;
    private SellerDomain seller;
    private TextView account_username;
    private LinearLayout accountProfileInfo,accountBillingShipping,purchaseHistory,sellerStore;
    private SocialMedia socialMedia;
    LoadingDialog loadingDialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        //initializing components
        initComponents();

        if(user == null){
            Intent intent = new Intent(AccountActivity.this,SignUpInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        }else{
            loadingDialog.show();
            searchUserProcess();
        }

        loadBottomNavigationBar();

        accountBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        accountProfileInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,ProfileInformationActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                if(customer != null){
                    String customerString = (new Gson()).toJson(customer);
                    intent.putExtra("user",customerString);
                }else if(seller != null){
                    String sellerString = (new Gson()).toJson(seller);
                    String socialMediaString = (new Gson()).toJson(socialMedia);
                    intent.putExtra("user",sellerString);
                    intent.putExtra("socialMedia",socialMediaString);
                }
                startActivity(intent);
                finish();
            }
        });
        accountBillingShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,BillingShippingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        purchaseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this,PurchaseHistoryActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        sellerStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, SellerStoreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }

    private void initComponents() {
        account_username = findViewById(R.id.account_username);
        loadingDialog = new LoadingDialog(AccountActivity.this);

        accountBackButton = findViewById(R.id.accountBackButton);
        accountProfileInfo = findViewById(R.id.account_profile_info);
        accountBillingShipping = findViewById(R.id.account_payment_methods);
        purchaseHistory = findViewById(R.id.account_purchase_history);
        sellerStore = findViewById(R.id.account_seller_store);
        accountProfileImg = findViewById(R.id.account_profile_img);
    }

    private void loadBottomNavigationBar() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.accountFragment, bottomNavigation.class,null)
                .commit();
    }


    private void searchUserProcess() {
        firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            sellerStore.setVisibility(View.GONE);
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                customer = snapshot.toObject(CustomerDomain.class);
                                if(customer != null) {
                                    String imgUrl = customer.getProfilePicUrl();
                                    setProfileImg(imgUrl);
                                    if(customer.getFname() != null && customer.getLname() != null){
                                        account_username.setText(customer.getFname()+" "+customer.getLname());
                                        loadingDialog.cancel();
                                        return;
                                    }else{
                                        account_username.setText(customer.getEmail());
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
                            sellerStore.setVisibility(View.VISIBLE);
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                seller = snapshot.toObject(SellerDomain.class);
                                if(seller != null){
                                    String imgUrl = seller.getProfilePicUrl();
                                    setProfileImg(imgUrl);
                                    snapshot.getReference().collection("Social-Media").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            socialMedia = snapshot1.toObject(SocialMedia.class);
                                                            System.out.println(snapshot1.getData());
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    System.out.println("Social media document get failure");
                                                }
                                            });
                                    if(seller.getSellerName() != null){
                                        account_username.setText(seller.getSellerName());
                                        loadingDialog.cancel();
                                        return;
                                    }else{
                                        account_username.setText(seller.getEmail());
                                        loadingDialog.cancel();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    public void setProfileImg(String imgUrl){
        if(imgUrl != null){
            firebaseStorage.getReference("profile-images/"+imgUrl)
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(accountProfileImg);
                        }
                    });
        }else{
            int drawableResourceId = getResources().getIdentifier("account_default_profile_img","drawable",getPackageName());
            Glide.with(AccountActivity.this)
                    .load(drawableResourceId)
                    .into(accountProfileImg);
        }
    }
}