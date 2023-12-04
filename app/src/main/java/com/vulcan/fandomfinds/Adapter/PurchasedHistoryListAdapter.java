package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Fragments.PurchasedItemFragment;
import com.vulcan.fandomfinds.Fragments.SellerStoreFragment;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class PurchasedHistoryListAdapter extends RecyclerView.Adapter<PurchasedHistoryListAdapter.ViewHolder> {
    ArrayList<OrderDomain> items;
    Context context;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseStorage firebaseStorage;
    public PurchasedHistoryListAdapter(ArrayList<OrderDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.user = firebaseAuth.getCurrentUser();
        this.firebaseStorage = FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public PurchasedHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.purchased_history_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedHistoryListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        firestore.collection("Orders").whereEqualTo("id",items.get(position).getId()).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                                        OrderDomain order = snapshot.toObject(OrderDomain.class);
                                        snapshot.getReference().collection("Product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()){
                                                    for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                        ProductsDomain product = snapshot1.toObject(ProductsDomain.class);
                                                        holder.phListProductTitle.setText(product.getTitle());
                                                        firebaseStorage.getReference("product-images/"+product.getPicUrl())
                                                                .getDownloadUrl()
                                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {
                                                                        Picasso.get()
                                                                                .load(uri)
                                                                                .into(holder.phProductImg);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        int drawableId = holder.itemView.getContext().getResources()
                                                                                .getIdentifier("product_default","drawable",holder.itemView.getContext().getPackageName());

                                                                        Glide.with(holder.itemView.getContext())
                                                                                .load(drawableId)
                                                                                .into(holder.phProductImg);
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
        holder.phListOrderId.setText("Oder Id: "+items.get(position).getId());
        holder.phListDateAndTime.setText(String.valueOf(items.get(position).getDateTime()));
        holder.phTotalPrice.setText("$"+String.valueOf(items.get(position).getTotalPrice()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = ((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager();
                for (Fragment fragment : manager.getFragments()){
                    manager.beginTransaction().remove(fragment).commit();
                }
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

                String orderString = (new Gson()).toJson(items.get(position));

                Bundle bundle = new Bundle();
                bundle.putString("order", orderString);

                PurchasedItemFragment purchasedItemFragment = new PurchasedItemFragment();
                purchasedItemFragment.setArguments(bundle);

                manager.beginTransaction()
                        .add(R.id.fragmentContainerView4,purchasedItemFragment)
                        .commit();

//                PurchasedItemFragment purchasedItemFragment = new PurchasedItemFragment();
//                purchasedItemFragment.setArguments(bundle);

//                manager.beginTransaction()
//                        .add(R.id.fragmentContainerView4, purchasedItemFragment)
//                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView phProductImg;
        TextView phListProductTitle,phListOrderId,phListDateAndTime,phTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phProductImg = itemView.findViewById(R.id.ph_product_img);
            phListProductTitle = itemView.findViewById(R.id.phListProductTitle);
            phListOrderId = itemView.findViewById(R.id.sellerStoreItemStatus);
            phListDateAndTime = itemView.findViewById(R.id.sellerStoreItemDiscount);
            phTotalPrice = itemView.findViewById(R.id.sellerStoreItemPrice);
        }
    }
}
