package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.vulcan.fandomfinds.Fragments.PurchasedItemListFragment;
import com.vulcan.fandomfinds.R;

public class PurchaseHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_history);

        ImageView ph_backButton = findViewById(R.id.ph_backButton);
        ph_backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainerView4, PurchasedItemListFragment.class,null)
                .commit();
    }
}