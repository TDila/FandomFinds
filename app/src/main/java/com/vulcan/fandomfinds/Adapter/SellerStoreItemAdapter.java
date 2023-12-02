package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.SellerStoreSaveUpdateActivity;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Enum.ProductStatus;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerStoreItemAdapter extends RecyclerView.Adapter<SellerStoreItemAdapter.ViewHolder> {
    ArrayList<ProductsDomain> items;
    Context context;
    FirebaseStorage firebaseStorage;

    public SellerStoreItemAdapter(ArrayList<ProductsDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public SellerStoreItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.seller_store_item_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerStoreItemAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.sellerStoreItemTitle.setText(items.get(position).getTitle());
        if(items.get(position).getStatus() == ProductStatus.AVAILABLE){
            holder.sellerStoreItemStatus.setText("Available");
        }else if (items.get(position).getStatus() == ProductStatus.OUT_OF_STOCK){
            holder.sellerStoreItemStatus.setText("Out of Stock");
        }
        holder.sellerStoreItemDiscount.setText("Discount : "+items.get(position).getDiscount()+"%");
        holder.sellerStoreItemPrice.setText("$"+String.valueOf(items.get(position).getPrice()));

        if(items.get(position).getPicUrl() != null){
            firebaseStorage.getReference("product-images/"+items.get(position).getPicUrl())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(holder.sellerStoreItemImg);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int drawableId = holder.itemView.getContext().getResources()
                                    .getIdentifier("product_default","drawable",holder.itemView.getContext().getPackageName());
                            Glide.with(holder.itemView.getContext())
                                    .load(drawableId)
                                    .into(holder.sellerStoreItemImg);
                        }
                    });
        }else{
            int drawableId = holder.itemView.getContext().getResources()
                    .getIdentifier("product_default","drawable",holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(drawableId)
                    .into(holder.sellerStoreItemImg);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SellerStoreSaveUpdateActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("item",items.get(position));
                holder.itemView.getContext().startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView sellerStoreItemImg;
        TextView sellerStoreItemTitle,sellerStoreItemStatus,sellerStoreItemDiscount,sellerStoreItemPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sellerStoreItemImg = itemView.findViewById(R.id.sellerStoreItemImg);
            sellerStoreItemTitle = itemView.findViewById(R.id.sellerStoreItemTitle);
            sellerStoreItemStatus = itemView.findViewById(R.id.sellerStoreItemStatus);
            sellerStoreItemDiscount = itemView.findViewById(R.id.sellerStoreItemDiscount);
            sellerStoreItemPrice = itemView.findViewById(R.id.sellerStoreItemPrice);
        }
    }
}
