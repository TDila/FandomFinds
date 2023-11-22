package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.vulcan.fandomfinds.Activity.SingleProductViewActivity;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class DealsAdapter extends RecyclerView.Adapter<DealsAdapter.ViewHolder> {
    ArrayList<ProductsDomain> items;
    Context context;

    public DealsAdapter(ArrayList<ProductsDomain> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public DealsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflater = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_deals_list,parent,false);
        return new ViewHolder(inflater);
    }

    @Override
    public void onBindViewHolder(@NonNull DealsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        double oldPrice = items.get(position).getPrice();
        double discount = items.get(position).getDiscount();
        double newPrice = oldPrice - (oldPrice * discount/100);

        holder.titleTxt.setText(items.get(position).getTitle());
        holder.scoreTxt.setText(""+items.get(position).getScore());
        holder.sellerTxt.setText("#"+items.get(position).getSellerName());
        if(discount == 0){
            holder.feeTxtOld.setText("$"+String.valueOf(oldPrice));
            holder.feeTxtNew.setVisibility(View.GONE);
            holder.deals_dis_percentage.setVisibility(View.GONE);
        }else{
            holder.feeTxtOld.setText("$"+String.valueOf(oldPrice));
            holder.feeTxtOld.setPaintFlags(holder.feeTxtOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.feeTxtNew.setVisibility(View.VISIBLE);
            holder.deals_dis_percentage.setVisibility(View.VISIBLE);
            holder.feeTxtNew.setText("$"+String.valueOf(newPrice));
            holder.deals_dis_percentage.setText(discount+"% OFF");
        }
        int drawableResourceId = holder.itemView.getResources().getIdentifier(items.get(position).getPicUrl(),
                "drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,0,0))
                .into(holder.pic);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), SingleProductViewActivity.class);
                intent.putExtra("newArrival",items.get(position));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleTxt,feeTxtOld,feeTxtNew,scoreTxt,sellerTxt,deals_dis_percentage;
        ImageView pic;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTxt = itemView.findViewById(R.id.dealsTitleTxt);
            feeTxtOld = itemView.findViewById(R.id.dealsFeeTxtOld);
            feeTxtNew = itemView.findViewById(R.id.dealsFeeTxtNew);
            scoreTxt = itemView.findViewById(R.id.dealsScoreTxt);
            sellerTxt = itemView.findViewById(R.id.dealsSellertxt);
            deals_dis_percentage = itemView.findViewById(R.id.deals_dis_percentage);

            pic = itemView.findViewById(R.id.dealsPic);
        }
    }
}
