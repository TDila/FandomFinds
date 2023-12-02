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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.vulcan.fandomfinds.Adapter.DealsAdapter;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.List;

public class SellerStoreFragment extends Fragment {
    FirebaseFirestore firestore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_store,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

        firestore.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        ProductsDomain product = snapshot.toObject(ProductsDomain.class);
                        if(product != null){
                            if(product.getDiscount() != 0){
                                if(product.getType().equals("Apparel")){
                                    List<String> sizes = product.getSizesList();
                                    items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                            ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),sizes,product.getStatus(),product.getType(),product.getSellerId()));
                                }else{
                                    items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                            ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),product.getSizesList(),product.getStatus(),product.getType(),product.getSellerId()));
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
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

        firestore.collection("Products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                        ProductsDomain product = snapshot.toObject(ProductsDomain.class);
                        if(product != null){
                            if(product.getDiscount() == 0){
                                if(product.getType().equals("Apparel")){
                                    List<String> sizes = product.getSizesList();
                                    items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                            ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),sizes,product.getStatus(),product.getType(),product.getSellerId()));
                                }else{
                                    items.add(new ProductsDomain(product.getId(),product.getTitle(),product.getDescription(),product.getPicUrl(),product.getReview()
                                            ,product.getScore(),product.getPrice(),product.getDiscount(),product.getSellerName(),product.getSizesList(),product.getStatus(),product.getType(),product.getSellerId()));
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }
}