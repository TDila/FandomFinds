package com.vulcan.fandomfinds.Domain;

import androidx.annotation.Nullable;

import com.vulcan.fandomfinds.Enum.ProductStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class ProductsDomain implements Serializable {
    private String id;
    private String title;
    private String description;
    private String picUrl;
    private int review;
    private double score;
    private int numberInCart;
    private double price;
    private double discount = 0;
    private String sellerName;
    private List<String> sizesList;
    private String selectedSize = null;
    private ProductStatus status = ProductStatus.AVAILABLE;
    private String type;
    private String sellerId;
    private List<String> titleInsensitive;

    public ProductsDomain() {
    }
    public ProductsDomain(String id, String title, String description, String picUrl, int review, double score, double price, double discount, String sellerName, List<String> sizesList, ProductStatus status, String type, String sellerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.review = review;
        this.score = score;
        this.price = price;
        this.discount = discount;
        this.sellerName = sellerName;
        this.sizesList = sizesList;
        this.status = status;
        this.type = type;
        this.sellerId = sellerId;
    }
    public ProductsDomain(String id, String title, String description, String picUrl, int review, double score, double price, double discount, String sellerName, List<String> sizesList, ProductStatus status, String type, String sellerId, List<String> titleInsensitive) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.picUrl = picUrl;
        this.review = review;
        this.score = score;
        this.price = price;
        this.discount = discount;
        this.sellerName = sellerName;
        this.sizesList = sizesList;
        this.status = status;
        this.type = type;
        this.sellerId = sellerId;
        this.titleInsensitive = titleInsensitive;
    }

    public List<String> getTitleInsensitive() {
        return titleInsensitive;
    }

    public void setTitleInsensitive(List<String> titleInsensitive) {
        this.titleInsensitive = titleInsensitive;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getNumberInCart() {
        return numberInCart;
    }

    public void setNumberInCart(int numberInCart) {
        this.numberInCart = numberInCart;
    }

    public List<String> getSizesList() {
        return sizesList;
    }

    public void setSizesList(List<String> sizesList) {
        this.sizesList = sizesList;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductsDomain otherProduct = (ProductsDomain) obj;
        return Objects.equals(id, otherProduct.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
