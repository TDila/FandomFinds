package com.vulcan.fandomfinds.Domain;

import java.io.Serializable;

public class CustomerDomain extends BaseDomain{
    public CustomerDomain() {
    }

    public CustomerDomain(String id, String email) {
        super(id, email);
    }

    public CustomerDomain(String id, String email, String fname, String lname, String profilePicUrl) {
        super(id, email, fname, lname, profilePicUrl);
    }
}
