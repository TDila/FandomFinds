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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
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
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchased_item_list,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        ArrayList<OrderDomain> items = new ArrayList<>();

        RecyclerView recyclerView = view.findViewById(R.id.purchasedHistoryList);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        PurchasedHistoryListAdapter adapter = new PurchasedHistoryListAdapter(items,view.getContext());
        recyclerView.setAdapter(adapter);

        firestore.collection("Orders").whereEqualTo("customerEmail",user.getEmail()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange change : value.getDocumentChanges()){
                    OrderDomain order = change.getDocument().toObject(OrderDomain.class);
                    switch (change.getType()){
                        case ADDED:
                            items.add(order);
                        case MODIFIED:
                            OrderDomain old = items.stream().filter(i -> i.getId().equals(order.getId())).findFirst().orElse(null);
                            if(old != null){
                                old.setStatus(order.getStatus());
                            }
                            break;
                        case REMOVED:
                            items.remove(order);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}