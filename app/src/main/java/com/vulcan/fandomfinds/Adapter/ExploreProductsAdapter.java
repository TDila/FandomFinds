package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.SingleProductViewActivity;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ExploreProductsAdapter extends RecyclerView.Adapter<ExploreProductsAdapter.ViewHolder> {
    FirebaseStorage firebaseStorage;
    ArrayList<ProductsDomain> items;
    Context context;

    public ExploreProductsAdapter(ArrayList<ProductsDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public ExploreProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.explore_product_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreProductsAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.explore_product_title.setText(items.get(position).getTitle());
        double price = items.get(position).getPrice();
        double discount = items.get(position).getDiscount();
        holder.explore_product_price.setText("$"+String.valueOf(price));
        if(discount != 0){
            double newPrice  = price - (price * discount / 100);
            holder.explore_product_new_price.setVisibility(View.VISIBLE);
            holder.explore_product_discount.setVisibility(View.VISIBLE);
            holder.explore_product_price.setPaintFlags(holder.explore_product_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.explore_product_new_price.setText("$"+String.valueOf(newPrice));
            holder.explore_product_discount.setText(String.valueOf(discount)+"% OFF");
        }else{
            holder.explore_product_new_price.setVisibility(View.GONE);
            holder.explore_product_discount.setVisibility(View.GONE);
        }
        holder.explore_product_seller_name.setText(items.get(position).getSellerName());

        if(items.get(position).getPicUrl() != null){
            firebaseStorage.getReference("product-images/"+items.get(position).getPicUrl())
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get()
                                    .load(uri)
                                    .into(holder.explore_product_img);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int drawableId = holder.itemView.getContext().getResources()
                                    .getIdentifier("product_default","drawable",holder.itemView.getContext().getPackageName());
                            Glide.with(holder.itemView.getContext())
                                    .load(drawableId)
                                    .into(holder.explore_product_img);
                        }
                    });
        }else{
            int drawableId = holder.itemView.getContext().getResources()
                    .getIdentifier("product_default","drawable",holder.itemView.getContext().getPackageName());
            Glide.with(holder.itemView.getContext())
                    .load(drawableId)
                    .into(holder.explore_product_img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), SingleProductViewActivity.class);
                intent.putExtra("newArrival",items.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView explore_product_img;
        TextView explore_product_title,explore_product_price,explore_product_new_price
                ,explore_product_seller_name,explore_product_discount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            explore_product_img = itemView.findViewById(R.id.explore_product_img);
            explore_product_title = itemView.findViewById(R.id.explore_product_title);
            explore_product_price = itemView.findViewById(R.id.explore_product_price);
            explore_product_new_price = itemView.findViewById(R.id.explore_product_new_price);
            explore_product_seller_name = itemView.findViewById(R.id.explore_product_seller_name);
            explore_product_discount = itemView.findViewById(R.id.explore_product_discount);
        }
    }
}
