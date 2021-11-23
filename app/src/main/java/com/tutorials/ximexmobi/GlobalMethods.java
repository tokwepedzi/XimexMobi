package com.tutorials.ximexmobi;

import android.content.Context;
import android.content.Intent;

public class GlobalMethods {
    Context mContext;

    //constructor
    public GlobalMethods(Context context){
        this.mContext = context;
    }

    //Send user to dashboard method
    public void sendUserToDashboard(){
        Intent intent = new Intent(mContext, Dashboard.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /*
    https://www.youtube.com/watch?v=VI960wtBUo0
    https://www.youtube.com/watch?v=UMZZHHJ37bo
    https://www.youtube.com/watch?v=fGcMLu1GJEc

     */
}
