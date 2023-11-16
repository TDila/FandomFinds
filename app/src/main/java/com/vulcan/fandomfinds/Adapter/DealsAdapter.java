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
import com.vulcan.fandomfinds.Domain.DealsDomain;
import com.vulcan.fandomfinds.Domain.NewArrivalDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    ArrayList<DealsDomain> items;
    Context context;

    public DealsAdapter(ArrayList<DealsDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_deals_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull DealsAdapter.ViewHolder holder, int position) {
        holder.titleTxt.setText(items.get(position).getTitle());
        holder.feeTxtOld.setText("$"+String.valueOf(items.get(position).getOldPrice()));
        holder.feeTxtNew.setText("$"+String.valueOf(items.get(position).getNewPrice()));
        holder.scoreTxt.setText(""+items.get(position).getScore());
        holder.sellerTxt.setText("#"+items.get(position).getSellerName());

        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getPicUrl(),
                "drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(holder.pic);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt,feeTxtOld,feeTxtNew,scoreTxt,sellerTxt;
        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.dealsTitleTxt);
            feeTxtOld = itemView.findViewById(R.id.dealsFeeTxtOld);
            feeTxtNew = itemView.findViewById(R.id.dealsFeeTxtNew);
            scoreTxt = itemView.findViewById(R.id.dealsScoreTxt);
            sellerTxt = itemView.findViewById(R.id.dealsSellertxt);

            pic = itemView.findViewById(R.id.dealsPic);
        }
    }
}
