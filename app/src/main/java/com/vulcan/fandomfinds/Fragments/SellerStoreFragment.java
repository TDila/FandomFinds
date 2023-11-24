package com.vulcan.fandomfinds.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerStoreFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_store,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadDeals(view);
        loadOther(view);
    }

    public void loadDeals(View view){
        String[] sizes = {"XL","M"};
        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2,"Maniya Streams",null));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,2.3,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,5.7,"Maniya Streams",null));

        RecyclerView deals_list = view.findViewById(R.id.seller_store_frag_deals_list);
        deals_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DealsAdapter adapter = new DealsAdapter(items);
        deals_list.setAdapter(adapter);
    }

    public void loadOther(View view){
        String[] sizes = {"XL","M"};

        ArrayList<ProductsDomain> items = new ArrayList<>();
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));
        items.add(new ProductsDomain("T-shirt black","","item_1",15,4,410,0,"Maniya Streams",sizes));

        RecyclerView other_list = view.findViewById(R.id.seller_store_frag_other_list);
        other_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DealsAdapter adapter = new DealsAdapter(items);
        other_list.setAdapter(adapter);
    }
}