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
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.vulcan.fandomfinds.Activity.MainActivity;
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
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(holder.sellerPic);
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
