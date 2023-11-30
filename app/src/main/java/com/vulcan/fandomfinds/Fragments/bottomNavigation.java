package com.vulcan.fandomfinds.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Activity.AccountActivity;
import com.vulcan.fandomfinds.Activity.CartActivity;
import com.vulcan.fandomfinds.Activity.ExploreActivity;
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

public class bottomNavigation extends Fragment {
    private LinearLayout bottom_navbar_home,bottom_navbar_explore,bottom_navbar_cart,bottom_navbar_account;
    private ImageView home_img,explore_img,cart_img,account_img;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottom_navigation,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);

        //initiating linear layouts
        bottom_navbar_home = fragment.findViewById(R.id.bottom_navbar_home);
        bottom_navbar_explore = fragment.findViewById(R.id.bottom_navbar_explore);
        bottom_navbar_cart = fragment.findViewById(R.id.bottom_navbar_cart);
        bottom_navbar_account = fragment.findViewById(R.id.bottom_navbar_account);

        //initiating images
        home_img = fragment.findViewById(R.id.bottom_navbar_img_home);
        explore_img = fragment.findViewById(R.id.bottom_navbar_img_explore);
        cart_img = fragment.findViewById(R.id.bottom_navbar_img_cart);
        account_img = fragment.findViewById(R.id.bottom_navbar_img_account);


        //combining listeners to the layouts
        bottom_navbar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        bottom_navbar_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(),CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        bottom_navbar_explore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), ExploreActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        bottom_navbar_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), AccountActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}