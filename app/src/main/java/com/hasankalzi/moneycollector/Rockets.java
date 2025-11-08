package com.hasankalzi.moneycollector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;



class Rockets {
    int speed = 1;
    int x =0 ,y;
    int width;
    private int height;
    private Bitmap rocket;
    boolean wasImpacted = true;

     Rockets(Resources res) {
        // Decode resources för all bilder
        rocket = BitmapFactory.decodeResource(res,R.drawable.rocket);



        width = rocket.getWidth();
        height = rocket.getWidth();

        // Scala om dimensioner
        width =  width /(3/2);
        height = height/(3/2);

        // Skalar om karaktären nu
        width = (int) (width * GameView.screenRatioX) ;
        height = (int) (height * GameView.screenRatioY) ;

        rocket = Bitmap.createScaledBitmap(rocket, width, height, true);
        // Start position
        y = -height;

    }

    // hämtar bilden av character, men det alternerar mellan chara1 och chara2 så det ser ut som att han rör sig
    Bitmap getRocket(){
        return rocket;
    }
    Rect getDetectCollision () {
        return new Rect( x , y, x + width/2 , y + height/2);
    }

}
