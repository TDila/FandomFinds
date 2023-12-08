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
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.List;

public class SellerStoreFragment extends Fragment {
    FirebaseFirestore firestore;
    SellerDomain seller;
    ImageView noDealsImage,noOtherImage;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_store,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String sellerString = getArguments().getString("seller");
        seller = (new Gson()).fromJson(sellerString, SellerDomain.class);

        noDealsImage = view.findViewById(R.id.noDealsImage);
        noOtherImage = view.findViewById(R.id.noOtherImage);
        noDealsImage.setVisibility(View.GONE);
        noOtherImage.setVisibility(View.GONE);

        firestore = FirebaseFirestore.getInstance();
        loadDeals(view);
        loadOther(view);
    }

    public void loadDeals(View view){
        ArrayList<ProductsDomain> items = new ArrayList<>();

        RecyclerView deals_list = view.findViewById(R.id.seller_store_frag_deals_list);
        deals_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DealsAdapter adapter = new DealsAdapter(items);
        deals_list.setAdapter(adapter);

        firestore.collection("Products").whereEqualTo("sellerId",seller.getId()).whereGreaterThan("discount",0).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    if(value.size() == 0){
                        noDealsImage.setVisibility(View.VISIBLE);
                    }
                    for (DocumentChange change : value.getDocumentChanges()){
                        ProductsDomain product = change.getDocument().toObject(ProductsDomain.class);
                        switch (change.getType()){
                            case ADDED:
                                items.add(product);
                            case MODIFIED:
                                ProductsDomain old = items.stream().filter(i -> i.getId().equals(product.getId())).findFirst().orElse(null);
                                if(old != null){
                                    old.setTitle(product.getTitle());
                                    old.setDescription(product.getDescription());
                                    old.setPicUrl(product.getPicUrl());
                                    old.setReview(product.getReview());
                                    old.setPrice(product.getPrice());
                                    old.setDiscount(product.getDiscount());
                                    old.setSellerName(product.getSellerName());
                                    old.setSizesList(product.getSizesList());
                                    old .setStatus(product.getStatus());
                                    old.setType(product.getType());
                                    old.setTitleInsensitive(product.getTitleInsensitive());
                                }
                                break;
                            case REMOVED:
                                items.remove(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    noDealsImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public void loadOther(View view){
        ArrayList<ProductsDomain> items = new ArrayList<>();

        RecyclerView other_list = view.findViewById(R.id.seller_store_frag_other_list);
        other_list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        DealsAdapter adapter = new DealsAdapter(items);
        other_list.setAdapter(adapter);

        firestore.collection("Products").whereEqualTo("sellerId",seller.getId()).whereEqualTo("discount",0).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    if(value.size() == 0){
                        noDealsImage.setVisibility(View.VISIBLE);
                    }
                    for (DocumentChange change : value.getDocumentChanges()){
                        ProductsDomain product = change.getDocument().toObject(ProductsDomain.class);
                        switch (change.getType()){
                            case ADDED:
                                items.add(product);
                            case MODIFIED:
                                ProductsDomain old = items.stream().filter(i -> i.getId().equals(product.getId())).findFirst().orElse(null);
                                if(old != null){
                                    old.setTitle(product.getTitle());
                                    old.setDescription(product.getDescription());
                                    old.setPicUrl(product.getPicUrl());
                                    old.setReview(product.getReview());
                                    old.setPrice(product.getPrice());
                                    old.setDiscount(product.getDiscount());
                                    old.setSellerName(product.getSellerName());
                                    old.setSizesList(product.getSizesList());
                                    old .setStatus(product.getStatus());
                                    old.setType(product.getType());
                                    old.setTitleInsensitive(product.getTitleInsensitive());
                                }
                                break;
                            case REMOVED:
                                items.remove(product);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }else{
                    noDealsImage.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}