package com.vulcan.fandomfinds.Domain;

import androidx.annotation.Nullable;

import com.vulcan.fandomfinds.Enum.SellerProfileStatus;

import java.util.List;
import java.util.Objects;

public class SellerDomain extends BaseDomain{
    private int followers;
    private String sellerName;
    private String bio;
    private List<String> sellerNameInsensitive;
    private SellerProfileStatus profileStatus = SellerProfileStatus.INCOMPLETE;

    public SellerProfileStatus getProfileStatus() {
        return profileStatus;
    }

    public void setProfileStatus(SellerProfileStatus profileStatus) {
        this.profileStatus = profileStatus;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getSellerNameInsensitive() {
        return sellerNameInsensitive;
    }

    public void setSellerNameInsensitive(List<String> sellerNameInsensitive) {
        this.sellerNameInsensitive = sellerNameInsensitive;
    }

    public SellerDomain() {
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public SellerDomain(String sellerName, String profilePicUrl, int followers) {
        super(profilePicUrl);
        this.followers = followers;
        this.sellerName = sellerName;
    }

    public SellerDomain(String id, String email) {
        super(id, email);
    }


    public SellerDomain(String id, String email, String fname, String lname, String profilePicUrl, int followers) {
        super(id, email, fname, lname, profilePicUrl);
        this.followers = followers;
    }

    public SellerDomain(String id, String email, String fname, String lname, String profilePicUrl, int followers, String sellerName, String bio, List<String> sellerNameInsensitive) {
        super(id, email, fname, lname, profilePicUrl);
        this.followers = followers;
        this.sellerName = sellerName;
        this.bio = bio;
        this.sellerNameInsensitive = sellerNameInsensitive;
    }
}
