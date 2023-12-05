package com.vulcan.fandomfinds.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.OrderViewActivity;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Fragments.PurchasedItemFragment;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.ViewHolder> {
    ArrayList<OrderDomain> items;
    Context context;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;

    public OrdersListAdapter(ArrayList<OrderDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public OrdersListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.purchased_history_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersListAdapter.ViewHolder holder, int position) {
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
                String orderString = (new Gson()).toJson(items.get(position));
                Intent intent = new Intent(holder.itemView.getContext(), OrderViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("order",orderString);
                holder.itemView.getContext().startActivity(intent);
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
