package com.hasankalzi.moneycollector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

class Char {

    boolean notTouched = true;
    static public float x, y;
    int height; private int width;
    private int count;
    private Bitmap[] chara;
    Bitmap boom;
    Char(Resources res) {


        Integer[] coin_images = {
                R.drawable.char1,
                R.drawable.char2,
                R.drawable.char3,
                R.drawable.char4,
                R.drawable.char5,
        };
        chara = new Bitmap[5];
        // Decode resources för all bilder
        for(int x=0;x<5;x++) {
            chara[x] = BitmapFactory.decodeResource(res,coin_images[x]);
        }

        width = chara[0].getWidth();
        height = chara[0].getWidth();

        // Scala om dimensioner
        width /= 2;
        height /= 3;

        // Skalar om karaktären nu
        width = (int) (width * GameView.screenRatioX) ;
        height = (int) (height * GameView.screenRatioY) ;

        for(int x=0;x<5;x++) {
            chara[x] = Bitmap.createScaledBitmap(chara[x], width, height, true);
        }

        boom = BitmapFactory.decodeResource(res, R.drawable.boom);
        boom = Bitmap.createScaledBitmap(boom, width, height, true);
        // Start position
        y = height;
        x = 0;

        count = 0;
    }

    // hämtar bilden av character, men det alternerar mellan chara1 och chara2 så det ser ut som att han rör sig
    Bitmap getChar(){
        count++;
        if (count==4){
            count = 0;
            return chara[4];
        }
        return chara[count];
    }
    Rect getDetectCollision () {
        return new Rect((int)x,(int) y, (int)x + width/2 , (int)y + height/2);
    }

}