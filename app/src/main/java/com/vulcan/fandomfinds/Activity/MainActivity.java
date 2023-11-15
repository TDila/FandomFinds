package com.vulcan.fandomfinds.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.vulcan.fandomfinds.Adapter.NewArrivalAdapter;
import com.vulcan.fandomfinds.Domain.NewArrivalDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView.Adapter adapterNewArrival;
    private RecyclerView recyclerViewNewArrival;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRecyclerView();
    }

    private void initRecyclerView(){
        ArrayList<NewArrivalDomain> items = new ArrayList<>();
        items.add(new NewArrivalDomain("T-shirt black","","item_1",15,4,500));
        items.add(new NewArrivalDomain("T-shirt black","","item_2",15,4,500));
        items.add(new NewArrivalDomain("T-shirt black","","item_3",15,4,500));
        items.add(new NewArrivalDomain("T-shirt black","","item_4",15,4,500));

        recyclerViewNewArrival = findViewById(R.id.featured);
        recyclerViewNewArrival.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        adapterNewArrival = new NewArrivalAdapter(items);
        recyclerViewNewArrival.setAdapter(adapterNewArrival);
    }
}