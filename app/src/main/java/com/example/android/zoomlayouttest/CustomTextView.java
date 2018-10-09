package com.example.android.zoomlayouttest;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Custom textview class, used to implement the FontData Class
 * */

public class CustomTextView extends AppCompatTextView {

    private int typefaceType;

    public CustomTextView(Context context, AttributeSet attrs){
        super(context, attrs);
        TypedArray array = context.getTheme().obtainStyledAttributes(
                attrs,
               R.styleable.CustomTextView,
                0,0);
        try{
            typefaceType = array.getInteger(R.styleable.CustomTextView_font_name,0);

        }finally {
            array.recycle();
        }
        if(!isInEditMode()){
            setTypeface(FontData.getApp().getTypeFace(typefaceType));
        }
    }
}
