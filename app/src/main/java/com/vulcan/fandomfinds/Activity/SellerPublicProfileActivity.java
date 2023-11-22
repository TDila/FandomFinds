package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.vulcan.fandomfinds.Fragments.SellerAboutFragment;
import com.vulcan.fandomfinds.Fragments.SellerStoreFragment;
import com.vulcan.fandomfinds.R;

public class SellerPublicProfileActivity extends AppCompatActivity {

    TextView store_text,about_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_public_profile);

        initTexts();
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
    private void initTexts() {
        store_text = findViewById(R.id.seller_public_profile_store_text);
        about_text =findViewById(R.id.seller_public_profile_about_text);
    }
}