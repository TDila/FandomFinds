package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class SellerDomain implements Serializable {
    private String sellerName;
    private String sellerPicUrl;
    private int followers;

    public SellerDomain() {
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
}
