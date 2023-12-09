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
import com.vulcan.fandomfinds.Domain.Follower;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ViewHolder> {
    ArrayList<Follower> items;
    Context context;

    public FollowersAdapter(ArrayList<Follower> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public FollowersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.followers_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FollowersAdapter.ViewHolder holder, int position) {
        holder.follower_email.setText(items.get(position).getEmail());

        int drawableResourceId = holder.itemView.getResources().getIdentifier("follower","drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .into(holder.follower_img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView follower_img;
        TextView follower_email;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            follower_img = itemView.findViewById(R.id.follower_img);
            follower_email = itemView.findViewById(R.id.follower_email);

        }
    }
}
