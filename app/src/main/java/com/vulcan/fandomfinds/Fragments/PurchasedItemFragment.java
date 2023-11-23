package com.vulcan.fandomfinds.Fragments;

import android.app.FragmentManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vulcan.fandomfinds.Domain.OrderDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class PurchasedItemFragment extends Fragment {
    private ImageView phItemProductImg,phItemSellerImg;
    TextView phItemProductTitle,phItemProductCount,phItemTotalPrice,phItemPurchasedDate,phItemOrderId,phItemDeliveryAddress,phItemSellerName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_purchased_item,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        ArrayList<OrderDomain> items = null;

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
        if(args != null){
            items = args.<OrderDomain>getParcelableArrayList("items");
            OrderDomain orderDomain = items.get(0);

            phItemProductTitle.setText(orderDomain.getProduct().getTitle());
            phItemProductCount.setText("Item Count : "+orderDomain.getItemCount());
            phItemTotalPrice.setText("$"+orderDomain.getTotalPrice());
            phItemPurchasedDate.setText(orderDomain.getDateTime());
            phItemOrderId.setText(orderDomain.getId());
            phItemDeliveryAddress.setText(orderDomain.getAddress());
            phItemSellerName.setText(orderDomain.getSeller().getSellerName());

            int productPicId = view.getContext().getResources()
                    .getIdentifier(orderDomain.getProduct().getPicUrl(),"drawable",view.getContext().getPackageName());
            Glide.with(view.getContext())
                    .load(productPicId)
                    .into(phItemProductImg);

            int sellerPicId = view.getContext().getResources()
                    .getIdentifier(orderDomain.getSeller().getSellerPicUrl(),"drawable",view.getContext().getPackageName());
            Glide.with(view.getContext())
                    .load("https://firebasestorage.googleapis.com/v0/b/fir-storage-13496.appspot.com/o/unnamed%20(13)-modified.png?alt=media&token=800e71d0-6738-4e42-b7ea-c9ebf8b25727")
                    .into(phItemSellerImg);
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
    }
}