package com.vulcan.fandomfinds.Fragments;

import android.app.FragmentManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.Domain.CustomerDomain;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.Domain.ProductsDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class PurchasedItemFragment extends Fragment {
    private ImageView phItemProductImg,phItemSellerImg;
    private OrderDomain order;
    FirebaseFirestore firestore;
    FirebaseStorage firebaseStorage;
    TextView phItemProductTitle,phItemProductCount,phItemTotalPrice,phItemPurchasedDate,phItemOrderId
            ,phItemDeliveryAddress,phItemSellerName,phItemOrderStatus,phItemOrderSizeValue;
    LinearLayout phItemSizeLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchased_item,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

//        Bundle args = getArguments();
//        ArrayList<OrderDomain> items = null;
        String orderString = getArguments().getString("order");
        order = (new Gson()).fromJson(orderString, OrderDomain.class);

        view.findViewById(R.id.phItemBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Fragment fragment : getActivity().getSupportFragmentManager().getFragments()){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragmentContainerView4, PurchasedItemListFragment.class,null)
                        .commit();
            }
        });

        initComponents(view);
        if(order != null){
            firestore.collection("Orders").whereEqualTo("id",order.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    OrderDomain order = snapshot.toObject(OrderDomain.class);
                                    snapshot.getReference().collection("Product").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()){
                                                for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                    ProductsDomain product = snapshot1.toObject(ProductsDomain.class);
                                                    phItemProductTitle.setText(product.getTitle());
                                                    firebaseStorage.getReference("product-images/"+product.getPicUrl())
                                                            .getDownloadUrl()
                                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                @Override
                                                                public void onSuccess(Uri uri) {
                                                                    Picasso.get()
                                                                            .load(uri)
                                                                            .into(phItemProductImg);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    int drawableId = getResources()
                                                                            .getIdentifier("product_default","drawable",getContext().getPackageName());

                                                                    Glide.with(getContext())
                                                                            .load(drawableId)
                                                                            .into(phItemProductImg);
                                                                }
                                                            });
                                                }
                                            }
                                        }
                                    });
                                    snapshot.getReference().collection("Customer").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            snapshot1.getReference().collection("Delivery-Payment").get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                            if(task.isSuccessful()){
                                                                                for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                                                    BillingShippingDomain billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                                                                    phItemDeliveryAddress.setText(billingShipping.getShippingAddress());
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                    snapshot.getReference().collection("Seller").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            SellerDomain seller = snapshot1.toObject(SellerDomain.class);
                                                            phItemSellerName.setText(seller.getSellerName());

                                                            firebaseStorage.getReference("profile-images/"+seller.getProfilePicUrl())
                                                                    .getDownloadUrl()
                                                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                        @Override
                                                                        public void onSuccess(Uri uri) {
                                                                            Picasso.get()
                                                                                    .load(uri)
                                                                                    .into(phItemSellerImg);
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            int drawableId = getResources()
                                                                                    .getIdentifier("account_default_profile_img","drawable",getContext().getPackageName());

                                                                            Glide.with(getContext())
                                                                                    .load(drawableId)
                                                                                    .into(phItemSellerImg);
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    });
            if(order.getSelectedSize() != null){
                phItemSizeLayout.setVisibility(View.VISIBLE);
                phItemOrderSizeValue.setText(order.getSelectedSize());
            }else{
                phItemSizeLayout.setVisibility(View.GONE);
            }
            phItemProductCount.setText("Item Count : "+order.getItemCount());
            phItemTotalPrice.setText("$"+order.getTotalPrice());
            phItemPurchasedDate.setText(order.getDateTime());
            phItemOrderId.setText(order.getId());
            phItemOrderStatus.setText(String.valueOf(order.getStatus()));
        }
    }

    private void initComponents(View view) {
        phItemProductImg = view.findViewById(R.id.phItemProductImg);//
        phItemProductTitle = view.findViewById(R.id.phItemProductTitle);//
        phItemProductCount = view.findViewById(R.id.phItemProductCount);//
        phItemTotalPrice = view.findViewById(R.id.phItemTotalPrice);//
        phItemPurchasedDate = view.findViewById(R.id.phItemPurchasedDate);//
        phItemOrderId = view.findViewById(R.id.phItemOrderId);//
        phItemDeliveryAddress = view.findViewById(R.id.phItemDeliveryAddress);//
        phItemSellerImg = view.findViewById(R.id.phItemSellerImg);
        phItemSellerName = view.findViewById(R.id.phItemSellerName);//
        phItemOrderStatus= view.findViewById(R.id.phItemOrderStatus);
        phItemSizeLayout = view.findViewById(R.id.phItemSizeLayout);
        phItemOrderSizeValue = view.findViewById(R.id.phItemOrderSizeValue);
    }
}