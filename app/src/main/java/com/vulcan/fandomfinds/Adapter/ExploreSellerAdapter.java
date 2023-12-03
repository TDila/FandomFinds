package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.SellerPublicProfileActivity;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class ExploreSellerAdapter extends RecyclerView.Adapter<ExploreSellerAdapter.ViewHolder> {
    FirebaseStorage firebaseStorage;
    ArrayList<SellerDomain> items;
    Context context;

    public ExploreSellerAdapter(ArrayList<SellerDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ExploreSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_seller_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreSellerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.explore_seller_name.setText(items.get(position).getSellerName());
        holder.explore_seller_followers.setText(items.get(position).getFollowers()+" followers");

        if(items.get(position).getProfilePicUrl() != null){
            firebaseStorage.getReference("profile-images/"+items.get(position).getProfilePicUrl())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(holder.explore_seller_img);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int drawableResourceId = holder.itemView.getResources().getIdentifier("account_default_profile_img","drawable",
                                    holder.itemView.getContext().getPackageName());
                            Glide.with(holder.itemView.getContext())
                                    .load(drawableResourceId)
                                    .into(holder.explore_seller_img);
                        }
                    });
        }else{
            int drawableResourceId = holder.itemView.getResources().getIdentifier("account_default_profile_img","drawable",
                    holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(drawableResourceId)
                    .into(holder.explore_seller_img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), SellerPublicProfileActivity.class);
                String sellerString = (new Gson()).toJson(items.get(position));
                intent.putExtra("seller",sellerString);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView explore_seller_img;
        TextView explore_seller_name,explore_seller_followers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            explore_seller_img = itemView.findViewById(R.id.explore_seller_img);
            explore_seller_name = itemView.findViewById(R.id.explore_seller_name);
            explore_seller_followers = itemView.findViewById(R.id.explore_seller_followers);
        }
    }
}
