package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class SellerDomain implements Serializable {
    private String sellerName;
    private String sellerPicUrl;

    public SellerDomain() {
    }

    public SellerDomain(String sellerName, String sellerPicUrl) {
        this.sellerName = sellerName;
        this.sellerPicUrl = sellerPicUrl;
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
}
