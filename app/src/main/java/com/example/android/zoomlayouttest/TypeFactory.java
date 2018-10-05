package com.example.android.zoomlayouttest;

import android.content.Context;
import android.graphics.Typeface;

public class TypeFactory {

    final String MONT_EXLITE_ITAL ="fonts/Montserrat-ExtraLightItalic.ttf";
    final String MONTALT_EXLITE_ITAL="fonts/MontserratAlternates-ExtraLightItalic.ttf";
    final String MONTALT_MED ="fonts/MontserratAlternates-Medium.ttf";

    Typeface titleMed;
    Typeface textMain;
    Typeface textAlt;

    public TypeFactory(Context context){
        titleMed = Typeface.createFromAsset(context.getAssets(),MONTALT_MED);
        textMain = Typeface.createFromAsset(context.getAssets(),MONTALT_EXLITE_ITAL);
        textAlt = Typeface.createFromAsset(context.getAssets(),MONT_EXLITE_ITAL);
    }

    public Typeface getTitleMed(){return titleMed;}
    public Typeface getTextMain(){return textMain;}
    public Typeface getTextAlt(){return textAlt;}
}