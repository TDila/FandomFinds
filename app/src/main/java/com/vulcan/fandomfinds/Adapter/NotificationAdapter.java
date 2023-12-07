package com.vulcan.fandomfinds.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Activity.PurchaseHistoryActivity;
import com.vulcan.fandomfinds.Domain.NotificationDomain;
import com.vulcan.fandomfinds.Enum.NotifyType;
import com.vulcan.fandomfinds.Fragments.PurchasedItemListFragment;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    ArrayList<NotificationDomain> items;
    Context context;
    FirebaseStorage firebaseStorage;
    FirebaseFirestore firestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    public NotificationAdapter(ArrayList<NotificationDomain> items, Context context) {
        this.items = items;
        this.context = context;
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.user = firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.notificationTitle.setText(items.get(position).getTitle());
        holder.notificationMessage.setText(items.get(position).getMessage());
        holder.notificationDatetime.setText(items.get(position).getDateTime());

        firebaseStorage.getReference("product-images/"+items.get(position).getPicUrl())
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get()
                                .load(uri)
                                .into(holder.notificationImg);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int drawableResourceId = holder.itemView.getResources().getIdentifier("notification","drawable",holder.itemView.getContext().getPackageName());
                        Glide.with(context)
                                .load(drawableResourceId)
                                .into(holder.notificationImg);
                    }
                });

        holder.closeButtonImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("Customers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Notifications").whereEqualTo("id",items.get(position).getId())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                                                        snapshot.getReference().delete();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
                firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                snapshot.getReference().collection("Notifications").whereEqualTo("id",items.get(position).getId())
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if(task.isSuccessful()){
                                                    for(QueryDocumentSnapshot snapshot : task.getResult()){
                                                        snapshot.getReference().delete();
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.get(position).getType().equals(NotifyType.NEW_ORDER)){
                    Intent intent = new Intent(holder.itemView.getContext(), PurchaseHistoryActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    holder.itemView.getContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView notificationImg,closeButtonImg;
        TextView notificationTitle,notificationMessage,notificationDatetime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationImg = itemView.findViewById(R.id.notificationImg);
            closeButtonImg = itemView.findViewById(R.id.closeButtonImg);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationDatetime = itemView.findViewById(R.id.notificationDatetime);
        }
    }
}
