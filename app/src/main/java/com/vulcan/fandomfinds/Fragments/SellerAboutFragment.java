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

import com.vulcan.fandomfinds.Adapter.SocialMediaAdapter;
import com.vulcan.fandomfinds.Domain.SocialMediaDomain;
import com.vulcan.fandomfinds.R;

import java.util.ArrayList;

public class SellerAboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_about,container,false);

        ArrayList<SocialMediaDomain> items = new ArrayList<>();
        items.add(new SocialMediaDomain("youtube_32","YouTube","www.youtube.com"));
        items.add(new SocialMediaDomain("youtube_32","YouTube","www.youtube.com"));
        items.add(new SocialMediaDomain("youtube_32","YouTube","www.youtube.com"));

        RecyclerView recyclerView = view.findViewById(R.id.social_media_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        SocialMediaAdapter socialMediaAdapter = new SocialMediaAdapter(items);
        recyclerView.setAdapter(socialMediaAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View fragment, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(fragment, savedInstanceState);
    }
}