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

import com.vulcan.fandomfinds.Activity.PurchaseHistoryActivity;
import com.vulcan.fandomfinds.Adapter.PurchasedHistoryListAdapter;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PurchasedItemListFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchased_item_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] sizes = {"XL","M"};
        SellerDomain sellerDomain = new SellerDomain("ManiYa Streams","maniya_streams_round",444);
        ProductsDomain productsDomain = new ProductsDomain("T-shirt black","","item_1",15,4,410,10,"Maniya Streams",sizes);
        CustomerDomain customerDomain = new CustomerDomain("CUS_574865","customer@gmail.com");

        ArrayList<OrderDomain> items = new ArrayList<>();

        LocalDateTime localDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            localDateTime = LocalDateTime.now();
        }
        items.add(new OrderDomain("OD_756485",productsDomain,sellerDomain,customerDomain, String.valueOf(localDateTime),"15000",1,"104/9,Nedurupitiya,Kandana",11320));
        items.add(new OrderDomain("OD_756485",productsDomain,sellerDomain,customerDomain, String.valueOf(localDateTime),"15000",1,"104/9,Nedurupitiya,Kandana",11320));
        items.add(new OrderDomain("OD_756485",productsDomain,sellerDomain,customerDomain, String.valueOf(localDateTime),"15000",1,"104/9,Nedurupitiya,Kandana",11320));

        RecyclerView recyclerView = view.findViewById(R.id.purchasedHistoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        PurchasedHistoryListAdapter adapter = new PurchasedHistoryListAdapter(items,view.getContext());
        recyclerView.setAdapter(adapter);
    }
}