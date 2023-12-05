package com.vulcan.fandomfinds.Fragments;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.vulcan.fandomfinds.Activity.BillingShippingActivity;
import com.vulcan.fandomfinds.Adapter.SocialMediaAdapter;
import com.vulcan.fandomfinds.Domain.BillingShippingDomain;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Domain.SocialMediaDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerAboutFragment extends Fragment {
    FirebaseFirestore firestore;
    SellerDomain seller;
    TextView description,sellerAboutPhone,sellerAboutEmail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_about,container,false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
        firestore = FirebaseFirestore.getInstance();

        String sellerString = getArguments().getString("seller");
        seller = (new Gson()).fromJson(sellerString,SellerDomain.class);

        if(seller != null){
            description = fragment.findViewById(R.id.seller_about_frag_desc);
            sellerAboutPhone = fragment.findViewById(R.id.sellerAboutPhone);
            sellerAboutEmail = fragment.findViewById(R.id.sellerAboutEmail);

            description.setText(seller.getBio() != null ? seller.getBio() : "NONE");
            sellerAboutEmail.setText(seller.getEmail() != null ? seller.getEmail() : "NONE");
            sellerAboutEmail.setPaintFlags(sellerAboutEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            sellerAboutEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.fromParts("mailto",seller.getEmail(), null));
                    if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                        startActivity(Intent.createChooser(intent, "Choose an email app"));
                    }
                }
            });

            ArrayList<SocialMediaDomain> items = new ArrayList<>();

            RecyclerView recyclerView = fragment.findViewById(R.id.social_media_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
            SocialMediaAdapter socialMediaAdapter = new SocialMediaAdapter(items);
            recyclerView.setAdapter(socialMediaAdapter);

            firestore.collection("Sellers").whereEqualTo("id",seller.getId()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                for (QueryDocumentSnapshot snapshot : task.getResult()){
                                    snapshot.getReference().collection("Social-Media").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()){
                                                        for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                            SocialMedia socialMedia = snapshot1.toObject(SocialMedia.class);
                                                            if(socialMedia.getYoutube() != null && !socialMedia.getYoutube().isEmpty()){
                                                                items.add(new SocialMediaDomain("youtube_32","YouTube",socialMedia.getYoutube()));
                                                            }
                                                            if(socialMedia.getTwitter() != null && !socialMedia.getTwitter().isEmpty()){
                                                                items.add(new SocialMediaDomain("twitter_32","YouTube",socialMedia.getTwitter()));
                                                            }
                                                            if(socialMedia.getFacebook() != null && !socialMedia.getFacebook().isEmpty()){
                                                                items.add(new SocialMediaDomain("facebook_32","YouTube",socialMedia.getFacebook()));
                                                            }
                                                            if(socialMedia.getInstagram() != null && !socialMedia.getInstagram().isEmpty()){
                                                                items.add(new SocialMediaDomain("instagram_32","YouTube",socialMedia.getInstagram()));
                                                            }
                                                            if(socialMedia.getTwitch() != null && !socialMedia.getTwitch().isEmpty()){
                                                                items.add(new SocialMediaDomain("twitch_32","YouTube",socialMedia.getTwitch()));
                                                            }
                                                        }
                                                        socialMediaAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(),"Seller Details Loading Failed!",Toast.LENGTH_LONG).show();
                                                }
                                            });

                                    snapshot.getReference().collection("Billing-Shipping").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if(task.isSuccessful()){
                                                        if(task.getResult().size() == 0){
                                                            sellerAboutPhone.setText("NONE");
                                                        }else{
                                                            for (QueryDocumentSnapshot snapshot1 : task.getResult()){
                                                                BillingShippingDomain billingShipping = snapshot1.toObject(BillingShippingDomain.class);
                                                                String mobileNumber = billingShipping.getMobileNumber();
                                                                if(mobileNumber != null){
                                                                    sellerAboutPhone.setText(mobileNumber);
                                                                    sellerAboutPhone.setOnClickListener(new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View v) {
                                                                            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                                                                            dialIntent.setData(Uri.parse("tel:" + mobileNumber));
                                                                            startActivity(dialIntent);

                                                                        }
                                                                    });
                                                                }else{
                                                                    sellerAboutPhone.setText("NONE");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getContext(),"Details Loading Failed! Try Again Later.",Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Seller Details Loading Failed!",Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}