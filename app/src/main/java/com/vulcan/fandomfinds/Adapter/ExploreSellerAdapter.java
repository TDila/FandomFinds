package com.vulcan.fandomfinds.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class ExploreSellerAdapter extends RecyclerView.Adapter<ExploreSellerAdapter.ViewHolder> {
    ArrayList<SellerDomain> items;
    Context context;

    public ExploreSellerAdapter(ArrayList<SellerDomain> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ExploreSellerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_seller_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreSellerAdapter.ViewHolder holder, int position) {
        holder.explore_seller_name.setText(items.get(position).getSellerName());
        holder.explore_seller_followers.setText(items.get(position).getFollowers()+" followers");

        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getSellerPicUrl(),
                "drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/fir-storage-13496.appspot.com/o/unnamed%20(13)-modified.png?alt=media&token=800e71d0-6738-4e42-b7ea-c9ebf8b25727")
                .into(holder.explore_seller_img);
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
