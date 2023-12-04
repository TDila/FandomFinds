package com.vulcan.fandomfinds.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.vulcan.fandomfinds.Enum.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderDomain implements Serializable, Parcelable {
    private String id;
    private String dateTime;
    private double totalPrice;
    private int itemCount;
    private int postalCode;
    private String status = String.valueOf(OrderStatus.ONGOING);
    private String customerEmail;
    private String sellerId;

    public OrderDomain() {
    }

    public OrderDomain(String id, String dateTime, double totalPrice, int itemCount, int postalCode, String customerEmail, String sellerId) {
        this.id = id;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
        this.postalCode = postalCode;
        this.customerEmail = customerEmail;
        this.sellerId = sellerId;
    }

    protected OrderDomain(Parcel in) {
        id = in.readString();
        dateTime = in.readString();
        totalPrice = in.readDouble();
        itemCount = in.readInt();
        postalCode = in.readInt();
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public static final Creator<OrderDomain> CREATOR = new Creator<OrderDomain>() {
        @Override
        public OrderDomain createFromParcel(Parcel in) {
            return new OrderDomain(in);
        }

        @Override
        public OrderDomain[] newArray(int size) {
            return new OrderDomain[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(dateTime);
        dest.writeDouble(totalPrice);
        dest.writeInt(itemCount);
        dest.writeInt(postalCode);
    }
}
