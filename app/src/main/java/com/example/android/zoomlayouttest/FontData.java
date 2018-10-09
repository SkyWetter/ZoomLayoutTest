package com.example.android.zoomlayouttest;

import android.app.Application;
import android.graphics.Typeface;


/***
 * Custom class for custom fact implementation
 */

public class FontData extends Application {

    private static FontData mInstance;
    private TypeFactory mFontFactory;

    @Override
    public void onCreate(){
        super.onCreate();
        mInstance = this;
    }

    public static synchronized FontData getApp() {return mInstance;}

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
