package com.vulcan.fandomfinds.Domain;

public class SocialMedia {
    String youtube;
    String twitter;
    String facebook;
    String instagram;
    String twitch;

    public SocialMedia() {
    }

    public SocialMedia(String youtube, String twitter, String facebook, String instagram, String twitch) {
        this.youtube = youtube;
        this.twitter = twitter;
        this.facebook = facebook;
        this.instagram = instagram;
        this.twitch = twitch;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }

    public String getTwitch() {
        return twitch;
    }

    public void setTwitch(String twitch) {
        this.twitch = twitch;
    }

}
