package com.example.android.zoomlayouttest;

import android.app.Application;
import android.graphics.Typeface;
import android.provider.SyncStateContract;

public class CustomApp extends Application {

    private static CustomApp mInstance;
    private TypeFactory mFontFactory;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized  CustomApp getApp() {return mInstance;}

    public Typeface getTypeFace(int type){
        if(mFontFactory == null)
            mFontFactory = new TypeFactory(this);

        switch (type){
            case Constants.TITLE : return mFontFactory.getTitleMed();

            case Constants.TEXTMAIN : return mFontFactory.getTextMain();

            case Constants.TEXTALT : return mFontFactory.getTextAlt();

            default: return mFontFactory.getTextMain();
        }
    }

    public interface Constants{
        int TITLE = 1,
            TEXTMAIN = 2,
            TEXTALT = 3;
    }
}
