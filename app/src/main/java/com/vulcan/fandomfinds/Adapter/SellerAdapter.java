package com.vulcan.fandomfinds.Adapter;

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
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.Activity.ProfileInformationActivity;
import com.vulcan.fandomfinds.Activity.SellerPublicProfileActivity;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ViewHolder> {
    ArrayList<SellerDomain> items;
    Context context;
    FirebaseStorage firebaseStorage;

    public SellerAdapter(ArrayList<SellerDomain> items){
        this.items = items;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public SellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View inflate = inflater.inflate(R.layout.viewholder_sellers,parent,false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull SellerAdapter.ViewHolder holder, int position) {
        holder.sellerName.setText(items.get(position).getSellerName());
        holder.followingCount.setText(items.get(position).getFollowers()+" followers");

        if(items.get(position).getProfilePicUrl() != null){
            firebaseStorage.getReference("profile-images/"+items.get(position).getProfilePicUrl())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(holder.sellerPic);
                        }
                    });
        }else{
            int drawableResourceId = holder.itemView.getResources().getIdentifier("account_default_profile_img","drawable",
                    holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(drawableResourceId)
                    .into(holder.sellerPic);
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
        TextView sellerName,followingCount;
        ImageView sellerPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sellerName = itemView.findViewById(R.id.homeSellerName);
            sellerPic = itemView.findViewById(R.id.homeSellerPic);
            followingCount = itemView.findViewById(R.id.follower_count);
        }
    }
}
