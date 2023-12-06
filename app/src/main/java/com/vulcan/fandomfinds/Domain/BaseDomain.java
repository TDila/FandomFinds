package com.vulcan.fandomfinds.Domain;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Objects;

public class BaseDomain implements Serializable {
    private String id;
    private String email;
    private String fname;
    private String lname;
    private String profilePicUrl;

    public BaseDomain() {
    }

    public BaseDomain(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public BaseDomain(String id, String email) {
        this.id = id;
        this.email = email;
    }

    public BaseDomain(String id, String email, String fname, String lname, String profilePicUrl) {
        this.id = id;
        this.email = email;
        this.fname = fname;
        this.lname = lname;
        this.profilePicUrl = profilePicUrl;
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

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BaseDomain otherUser = (BaseDomain) obj;
        return Objects.equals(id, otherUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
