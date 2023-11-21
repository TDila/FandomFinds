package com.vulcan.fandomfinds.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.vulcan.fandomfinds.Activity.CartActivity;
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.R;

public class bottomNavigation extends Fragment {
    private LinearLayout bottom_navbar_home,bottom_navbar_explore,bottom_navbar_cart,bottom_navbar_account;
    private Context context;
    private Intent intent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
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


        //combining listeners to the layouts
        bottom_navbar_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        bottom_navbar_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(getContext(),CartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
    }
}