package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class SellerDomain implements Serializable {
    private String id;
    private String email;
    private String sellerName;
    private String sellerPicUrl;
    private int followers;

    public SellerDomain(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public SellerDomain(String sellerName, String sellerPicUrl, int followers) {
        this.sellerName = sellerName;
        this.sellerPicUrl = sellerPicUrl;
        this.followers = followers;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerPicUrl() {
        return sellerPicUrl;
    }

    public void setSellerPicUrl(String sellerPicUrl) {
        this.sellerPicUrl = sellerPicUrl;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
