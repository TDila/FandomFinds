package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.vulcan.fandomfinds.R;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter AdapterFeatured;
    private RecyclerView recyclerViewFeatured;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    private void initRecyclerView(){

    }
}