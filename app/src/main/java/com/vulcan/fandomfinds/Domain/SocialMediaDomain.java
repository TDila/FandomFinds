package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class SocialMediaDomain implements Serializable {
    private String imgUrl;
    private String socialMediaName;
    private String socialMediaLink;

    public SocialMediaDomain(String imgUrl, String socialMediaName, String socialMediaLink) {
        this.imgUrl = imgUrl;
        this.socialMediaName = socialMediaName;
        this.socialMediaLink = socialMediaLink;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSocialMediaName() {
        return socialMediaName;
    }

    public void setSocialMediaName(String socialMediaName) {
        this.socialMediaName = socialMediaName;
    }

    public String getSocialMediaLink() {
        return socialMediaLink;
    }

    public void setSocialMediaLink(String socialMediaLink) {
        this.socialMediaLink = socialMediaLink;
    }
}
