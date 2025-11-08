package com.hasankalzi.moneycollector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.hasankalzi.moneycollector.GameView.screenRatioX;
import static com.hasankalzi.moneycollector.GameView.screenRatioY;

class Coins {

    int speed = 5;
    boolean wasCollected = true;
    int x = 0, y, width, height;
    private int coinCounter = 0;
    private final Bitmap[] coin;

    Coins(Resources res) {
        // Put all "int" references for the coin images into an array
        Integer[] coin_images = {
                R.drawable.coin0,
                R.drawable.coin1,
                R.drawable.coin2,
                R.drawable.coin3,
                R.drawable.coin4,
                R.drawable.coin5,
                R.drawable.coin6,
                R.drawable.coin7,
                R.drawable.coin8,
                R.drawable.coin9,
                R.drawable.coin10,
                R.drawable.coin11,
                R.drawable.coin12,
                R.drawable.coin13,
                R.drawable.coin14,
                R.drawable.coin15,
                R.drawable.coin16,
                R.drawable.coin17,
                R.drawable.coin18,
                R.drawable.coin19,
                R.drawable.coin20,
                R.drawable.coin21,
                R.drawable.coin22,
                R.drawable.coin23,
                R.drawable.coin24,
        };
        coin = new Bitmap[25];
        // Decode resources for all images
        for(int x=0;x<25;x++) {
            coin[x] = BitmapFactory.decodeResource(res,coin_images[x]);
        }

        // Get the dimensions of one of the images
        width = coin[0].getWidth();
        height = coin[0].getHeight();
        // Resize dimensions
        width /=8;
        height /=8;

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);
        // Create the coin images
        for(int x=0;x<25;x++) {
            coin[x] = Bitmap.createScaledBitmap(coin[x], width, height, true);
        }

        y = -height;
    }

    Bitmap getCoin() {
        coinCounter++;
        if (coinCounter==24){
            coinCounter = 0;
            return coin[24];
        }
        return coin[coinCounter];
    }

    Rect getDetectCollision () {
        return new Rect(x, y, x + width, y + height);
    }
}
