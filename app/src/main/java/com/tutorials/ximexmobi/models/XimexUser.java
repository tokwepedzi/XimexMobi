package com.tutorials.ximexmobi.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class XimexUser {
    private GeoPoint geoPoint;
    private @ServerTimestamp
    Date timestamp;
    public String fullname, email, whatsappnumber,usersince,uid,callsnumber,
            totalitemsbought,usertype,address,surburb;

    public XimexUser() {
    }

    public XimexUser(GeoPoint geoPoint, Date timestamp, String fullname, String email,
                     String whatsappnumber, String usersince, String uid, String callsnumber,
                     String totalitemsbought, String usertype, String address, String surburb) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.fullname = fullname;
        this.email = email;
        this.whatsappnumber = whatsappnumber;
        this.usersince = usersince;
        this.uid = uid;
        this.callsnumber = callsnumber;
        this.totalitemsbought = totalitemsbought;
        this.usertype = usertype;
        this.address = address;
        this.surburb = surburb;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsappnumber() {
        return whatsappnumber;
    }

    public void setWhatsappnumber(String whatsappnumber) {
        this.whatsappnumber = whatsappnumber;
    }

    public String getUsersince() {
        return usersince;
    }

    public void setUsersince(String usersince) {
        this.usersince = usersince;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCallsnumber() {
        return callsnumber;
    }

    public void setCallsnumber(String callsnumber) {
        this.callsnumber = callsnumber;
    }

    public String getTotalitemsbought() {
        return totalitemsbought;
    }

    public void setTotalitemsbought(String totalitemsbought) {
        this.totalitemsbought = totalitemsbought;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSurburb() {
        return surburb;
    }

    public void setSurburb(String surburb) {
        this.surburb = surburb;
    }
}
