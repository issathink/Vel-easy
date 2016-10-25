package com.veleasy.veleasy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

/**
 * Created by issa on 14/10/2016.
 */

public class Tools {

    private static final String FAV_COUNT = "veleasy.com.FAV_COUNT";
    private static final String FAV_POS1 = "veleasy.com.FAV_POS1";
    private static final String FAV_POS2 = "veleasy.com.FAV_POS2";
    private static final String FAV_POS3 = "veleasy.com.FAV_POS3";
    private static final String FAV_POS4 = "veleasy.com.FAV_POS4";
    private static final String FAV_POS5 = "veleasy.com.FAV_POS5";
    private static final String FAV_POS1_NUM = "veleasy.com.FAV_POS1_NUM";
    private static final String FAV_POS2_NUM = "veleasy.com.FAV_POS2_NUM";
    private static final String FAV_POS3_NUM = "veleasy.com.FAV_POS3_NUM";
    private static final String FAV_POS4_NUM = "veleasy.com.FAV_POS4_NUM";
    private static final String FAV_POS5_NUM = "veleasy.com.FAV_POS5_NUM";

    public static Bitmap writeTextOnDrawable(Context context, int drawableId, String text) {

        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.BOLD);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(context, 11));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if(textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(context, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 0;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) - 25;

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }

    public static int convertToPixels(Context context, int nDP){
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f) ;
    }

}
