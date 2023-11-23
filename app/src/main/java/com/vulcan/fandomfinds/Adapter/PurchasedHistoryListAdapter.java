package com.vulcan.fandomfinds.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentController;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideContext;
import com.vulcan.fandomfinds.Activity.PurchaseHistoryActivity;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Fragments.PurchasedItemFragment;
import com.vulcan.fandomfinds.Fragments.PurchasedItemListFragment;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class PurchasedHistoryListAdapter extends RecyclerView.Adapter<PurchasedHistoryListAdapter.ViewHolder> {
    ArrayList<OrderDomain> items;
    Context context;

    public PurchasedHistoryListAdapter(ArrayList<OrderDomain> items, Context context) {
        this.items = items;
        this.context = context;
    }
    @NonNull
    @Override
    public PurchasedHistoryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.purchased_history_list_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchasedHistoryListAdapter.ViewHolder holder, int position) {
        holder.phListOrderId.setText("Oder Id: "+items.get(position).getId());
        holder.phListProductTitle.setText(items.get(position).getProduct().getTitle());
        holder.phListDateAndTime.setText(String.valueOf(items.get(position).getDateTime()));
        holder.phTotalPrice.setText("$"+String.valueOf(items.get(position).getTotalPrice()));

        int drawableId = holder.itemView.getContext().getResources()
                .getIdentifier(items.get(position).getProduct().getPicUrl(),"drawable",holder.itemView.getContext().getPackageName());

        Glide.with(holder.itemView.getContext())
                .load(drawableId)
                .into(holder.phProductImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = ((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager();
                for (Fragment fragment : manager.getFragments()){
                    manager.beginTransaction().remove(fragment).commit();
                }
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("items", (ArrayList<? extends Parcelable>) items);

                PurchasedItemFragment purchasedItemFragment = new PurchasedItemFragment();
                purchasedItemFragment.setArguments(bundle);

                manager.beginTransaction()
                        .add(R.id.fragmentContainerView4, purchasedItemFragment)
                        .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView phProductImg;
        TextView phListProductTitle,phListOrderId,phListDateAndTime,phTotalPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phProductImg = itemView.findViewById(R.id.ph_product_img);
            phListProductTitle = itemView.findViewById(R.id.phListProductTitle);
            phListOrderId = itemView.findViewById(R.id.phListOrderId);
            phListDateAndTime = itemView.findViewById(R.id.phListDateAndTime);
            phTotalPrice = itemView.findViewById(R.id.phTotalPrice);
        }
    }
}
