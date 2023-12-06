package com.vulcan.fandomfinds.Domain;

import androidx.annotation.Nullable;

import com.vulcan.fandomfinds.Enum.NotifyType;

import java.io.Serializable;
import java.util.Objects;

public class NotificationDomain implements Serializable {
    private String id;
    private NotifyType type;
    private String title;
    private String message;
    private String picUrl;
    private String dateTime;

    public NotificationDomain() {
    }

    public NotificationDomain(String id, NotifyType type, String title, String message, String picUrl, String dateTime) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.message = message;
        this.picUrl = picUrl;
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public NotifyType getType() {
        return type;
    }

    public void setType(NotifyType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        NotificationDomain otherNotify = (NotificationDomain) obj;
        return Objects.equals(id, otherNotify.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
