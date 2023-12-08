package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vulcan.fandomfinds.Activity.MapActivity;
import com.vulcan.fandomfinds.Domain.StoreLocationDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class StoreLocationAdapter extends RecyclerView.Adapter<StoreLocationAdapter.ViewHolder> {
    ArrayList<StoreLocationDomain> items;
    Context context;

    public StoreLocationAdapter(ArrayList<StoreLocationDomain> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public StoreLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.stores_location_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreLocationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.locationName.setText(items.get(position).getName());

        holder.locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), MapActivity.class);
                intent.putExtra("lat",String.valueOf(items.get(position).getLat()));
                intent.putExtra("lng",String.valueOf(items.get(position).getLng()));
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView locationName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            locationName = itemView.findViewById(R.id.locationName);
        }
    }
}
