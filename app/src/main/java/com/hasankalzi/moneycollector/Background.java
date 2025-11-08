package com.hasankalzi.moneycollector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class Background {

    // coordinates
    int x = 0, y = 0;
    Bitmap background;
    Bitmap background2;

    Background(int screenX, int screenY, Resources res) {

        // Instantiates the background. BitmapFactory is the constructor for the Bitmap object
        // It takes in the resource object and its ID

        background2 = BitmapFactory.decodeResource(res, R.drawable.background2);
        background = BitmapFactory.decodeResource(res, R.drawable.background2);
        // Scales the Bitmap object to the correct size

        background2 = Bitmap.createScaledBitmap(background2, screenX, screenY, true);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, true);

    }

}
