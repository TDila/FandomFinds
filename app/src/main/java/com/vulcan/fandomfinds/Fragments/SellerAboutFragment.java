package com.vulcan.fandomfinds.Fragments;

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
import com.vulcan.fandomfinds.Adapter.SocialMediaAdapter;
import com.vulcan.fandomfinds.Domain.SellerDomain;
import com.vulcan.fandomfinds.Domain.SocialMedia;
import com.vulcan.fandomfinds.Domain.SocialMediaDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerAboutFragment extends Fragment {
    FirebaseFirestore firestore;
    SellerDomain seller;
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
            TextView description = fragment.findViewById(R.id.seller_about_frag_desc);
            description.setText(seller.getBio());

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