package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vulcan.fandomfinds.Domain.SocialMediaDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class SocialMediaAdapter extends RecyclerView.Adapter<SocialMediaAdapter.ViewHolder>{
    ArrayList<SocialMediaDomain> items;
    Context context;
    public SocialMediaAdapter(ArrayList<SocialMediaDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SocialMediaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.viewholder_social_media_links,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialMediaAdapter.ViewHolder holder,@SuppressLint("RecyclerView")  int position) {
        holder.social_media_name.setText(items.get(position).getSocialMediaName());
        holder.social_media_link.setText(items.get(position).getSocialMediaLink());
        System.out.println(position);

        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getImgUrl(),
                "drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load("https://firebasestorage.googleapis.com/v0/b/fir-storage-13496.appspot.com/o/youtube_32.png?alt=media&token=386910e1-2575-4314-b278-1b993e3768ea")
                .into(holder.social_media_img);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView social_media_name,social_media_link;
        ImageView social_media_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            social_media_name = itemView.findViewById(R.id.social_media_name);
            social_media_link = itemView.findViewById(R.id.social_media_link);
            social_media_img = itemView.findViewById(R.id.social_media_img);
        }
    }
}
