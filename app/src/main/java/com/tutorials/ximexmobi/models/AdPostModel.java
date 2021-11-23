package com.tutorials.ximexmobi.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class AdPostModel {
    private @ServerTimestamp
    Date timestamp;
    public String uid, posteddate, img1, img2, img3, img4, img5, img6, itemname, description, price,
            condition, totalviews, availability,adid;

    public AdPostModel() {
    }

    public AdPostModel(Date timestamp, String uid, String posteddate, String img1, String img2,
                       String img3, String img4, String img5, String img6, String itemname,
                       String description, String price, String condition, String totalviews,
                       String availability, String adid) {
        this.timestamp = timestamp;
        this.uid = uid;
        this.posteddate = posteddate;
        this.img1 = img1;
        this.img2 = img2;
        this.img3 = img3;
        this.img4 = img4;
        this.img5 = img5;
        this.img6 = img6;
        this.itemname = itemname;
        this.description = description;
        this.price = price;
        this.condition = condition;
        this.totalviews = totalviews;
        this.availability = availability;
        this.adid = adid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPosteddate() {
        return posteddate;
    }

    public void setPosteddate(String posteddate) {
        this.posteddate = posteddate;
    }

    public String getImg1() {
        return img1;
    }

    public void setImg1(String img1) {
        this.img1 = img1;
    }

    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
    }

    public String getImg3() {
        return img3;
    }

    public void setImg3(String img3) {
        this.img3 = img3;
    }

    public String getImg4() {
        return img4;
    }

    public void setImg4(String img4) {
        this.img4 = img4;
    }

    public String getImg5() {
        return img5;
    }

    public void setImg5(String img5) {
        this.img5 = img5;
    }

    public String getImg6() {
        return img6;
    }

    public void setImg6(String img6) {
        this.img6 = img6;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getTotalviews() {
        return totalviews;
    }

    public void setTotalviews(String totalviews) {
        this.totalviews = totalviews;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }
}
