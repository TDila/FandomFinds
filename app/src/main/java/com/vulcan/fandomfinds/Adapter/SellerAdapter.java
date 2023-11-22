package com.vulcan.fandomfinds.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.vulcan.fandomfinds.Activity.MainActivity;
import com.vulcan.fandomfinds.Activity.SellerPublicProfileActivity;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerAdapter extends RecyclerView.Adapter<SellerAdapter.ViewHolder> {
    ArrayList<SellerDomain> items;
    Context context;

    public SellerAdapter(ArrayList<SellerDomain> items){
        this.items = items;
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

        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getSellerPicUrl(),
                "mipmap",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/fir-storage-13496.appspot.com/o/unnamed%20(13)-modified.png?alt=media&token=800e71d0-6738-4e42-b7ea-c9ebf8b25727")
                .into(holder.sellerPic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), SellerPublicProfileActivity.class);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sellerName;
        ImageView sellerPic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sellerName = itemView.findViewById(R.id.homeSellerName);
            sellerPic = itemView.findViewById(R.id.homeSellerPic);
        }
    }
}
