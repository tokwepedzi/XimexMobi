package com.tutorials.ximexmobi;

import android.app.Application;

import com.tutorials.ximexmobi.models.XimexUser;

public class GlobalFields extends Application {
    private XimexUser ximexUser= null;

    public XimexUser getXimexUser() {
        return ximexUser;
    }

    public void setXimexUser(XimexUser ximexUser) {
        this.ximexUser = ximexUser;
    }
}
