package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class BillingShippingDomain implements Serializable {
    private String shippingAddress;
    private int postalCode;
    private String mobileNumber;
    private String paymentMethod;

    public BillingShippingDomain() {
    }

    public BillingShippingDomain(String shippingAddress, int postalCode, String mobileNumber, String paymentMethod) {
        this.shippingAddress = shippingAddress;
        this.postalCode = postalCode;
        this.mobileNumber = mobileNumber;
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
