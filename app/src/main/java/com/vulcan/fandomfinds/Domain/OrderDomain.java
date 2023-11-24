package com.vulcan.fandomfinds.Domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.vulcan.fandomfinds.Enum.OrderStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public class OrderDomain implements Serializable, Parcelable {
    private String id;
    private ProductsDomain product;
    private SellerDomain seller;
    private CustomerDomain customer;
    private String dateTime;
    private String totalPrice;
    private int itemCount;
    private String address;
    private int postalCode;
    private OrderStatus status = OrderStatus.ONGOING;

    public OrderDomain(String id, ProductsDomain product, SellerDomain seller, CustomerDomain customer, String dateTime, String totalPrice, int itemCount, String address, int postalCode) {
        this.id = id;
        this.product = product;
        this.seller = seller;
        this.customer = customer;
        this.dateTime = dateTime;
        this.totalPrice = totalPrice;
        this.itemCount = itemCount;
        this.address = address;
        this.postalCode = postalCode;
    }

    protected OrderDomain(Parcel in) {
        id = in.readString();
        dateTime = in.readString();
        totalPrice = in.readString();
        itemCount = in.readInt();
        address = in.readString();
        postalCode = in.readInt();
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

    public ProductsDomain getProduct() {
        return product;
    }

    public void setProduct(ProductsDomain product) {
        this.product = product;
    }

    public SellerDomain getSeller() {
        return seller;
    }

    public void setSeller(SellerDomain seller) {
        this.seller = seller;
    }

    public CustomerDomain getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDomain customer) {
        this.customer = customer;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
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
        dest.writeString(totalPrice);
        dest.writeInt(itemCount);
        dest.writeString(address);
        dest.writeInt(postalCode);
    }
}
