package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Fragments.SellerAboutFragment;
import com.vulcan.fandomfinds.Fragments.SellerStoreFragment;
import com.vulcan.fandomfinds.R;

public class SellerPublicProfileActivity extends AppCompatActivity {
    FirebaseStorage firebaseStorage;
    private SellerDomain seller;
    private ImageView profileSellerImg;
    private TextView store_text,about_text,sellerName,followerCount,followButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_public_profile);

        initComponents();
        loadStreamerDetails();
        loadStore();

        about_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                loadAbout();
            }
        });

        store_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Fragment fragment : getSupportFragmentManager().getFragments()){
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                loadStore();
            }
        });
    }

    private void loadStreamerDetails() {
        Intent intent = getIntent();
        String sellerDetails = intent.getStringExtra("seller");
        seller = (new Gson()).fromJson(sellerDetails, SellerDomain.class);
        if(seller != null){
            sellerName.setText(seller.getSellerName() != null ? seller.getSellerName():"Seller Name");
            followerCount.setText(seller.getFollowers()+" followers");
            String imageUrl = seller.getProfilePicUrl();
            if(imageUrl != null && !imageUrl.isEmpty()){
                firebaseStorage.getReference("profile-images/"+imageUrl).getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get()
                                        .load(uri)
                                        .into(profileSellerImg);
                            }
                        });
            }else{
                int drawableResourceId = getResources().getIdentifier("account_default_profile_img","drawable",getPackageName());
                Glide.with(SellerPublicProfileActivity.this)
                        .load(drawableResourceId)
                        .into(profileSellerImg);
            }
        }
    }

    public void loadStore(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView5,SellerStoreFragment.class,null)
                .commit();
    }
    public void loadAbout(){
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView5,SellerAboutFragment.class,null)
                .commit();
    }
    private void initComponents() {
        store_text = findViewById(R.id.seller_public_profile_store_text);
        about_text =findViewById(R.id.seller_public_profile_about_text);
        sellerName = findViewById(R.id.seller_public_profile_streamer_name);
        followerCount = findViewById(R.id.followerCount);
        followButton = findViewById(R.id.seller_public_profile_follow);
        profileSellerImg = findViewById(R.id.publicProfileSellerImg);

        //firebaseStorage
        firebaseStorage = FirebaseStorage.getInstance();
    }
}