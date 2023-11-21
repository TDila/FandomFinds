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
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Helper.ChangeNumberitemsListener;
import com.vulcan.fandomfinds.Helper.ManagementCart;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    ArrayList<ProductsDomain> items;
    private ManagementCart managementCart;
    ChangeNumberitemsListener changeNumberitemsListener;
    Context context;

    public CartAdapter(ArrayList<ProductsDomain> items, Context context,ChangeNumberitemsListener changeNumberitemsListener) {
        this.items = items;
        this.changeNumberitemsListener = changeNumberitemsListener;
        managementCart = new ManagementCart(context);
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_product_view_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.cart_product_title.setText(items.get(position).getTitle());

        double price = items.get(position).getPrice();

        holder.cart_product_price.setText("$"+String.valueOf(price));

        double discount =items.get(position).getDiscount();
        double total_value;
        if(discount != 0){
            double newPrice = price - (price * discount / 100);
            total_value = Math.round((items.get(position).getNumberInCart()*newPrice));
        }else{
            total_value = Math.round((items.get(position).getNumberInCart()*price));
        }

        holder.cart_total_product_price.setText("$"+String.valueOf(total_value));
        holder.cart_product_count.setText(String.valueOf(items.get(position).getNumberInCart()));

        int drawableResourceId = holder.itemView.getContext().getResources()
                .getIdentifier(items.get(position).getPicUrl(),"drawable",holder.itemView.getContext().getPackageName());
        Glide.with(holder.itemView.getContext())
                .load(drawableResourceId)
                .transform(new GranularRoundedCorners(30,30,30,30))
                .into(holder.cart_product_img);

        holder.cart_plus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.plusNumberItem(items, position, new ChangeNumberitemsListener() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberitemsListener.change();
                    }
                });
            }
        });

        holder.cart_minus_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                managementCart.minusNumberItem(items, position, new ChangeNumberitemsListener() {
                    @Override
                    public void change() {
                        notifyDataSetChanged();
                        changeNumberitemsListener.change();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cart_product_title,cart_total_product_price,cart_minus_button,cart_plus_button,cart_product_price,cart_product_count;
        ImageView cart_product_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cart_product_title = itemView.findViewById(R.id.cart_product_title);
            cart_total_product_price = itemView.findViewById(R.id.cart_total_product_price);
            cart_minus_button = itemView.findViewById(R.id.cart_minus_button);
            cart_plus_button = itemView.findViewById(R.id.cart_plus_button);
            cart_product_price = itemView.findViewById(R.id.cart_product_price);
            cart_product_count = itemView.findViewById(R.id.cart_product_count);
            cart_product_img = itemView.findViewById(R.id.cart_product_img);
        }
    }
}
