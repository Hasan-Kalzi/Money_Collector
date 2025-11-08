package com.hasankalzi.moneycollector;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class Background {

    // kordinater
    int x = 0, y = 0;
    Bitmap background;
    Bitmap background2;

    Background(int screenX, int screenY, Resources res) {

        // Instansierar bakgrunden. BitmapFactory är alltså contructor för Bitmap objektet
        // Den tar in resource objeket och dess ID

        background2 = BitmapFactory.decodeResource(res, R.drawable.background2);
        background = BitmapFactory.decodeResource(res, R.drawable.background2);
        // Skalar Bitmap objektet till rätt storlek

        background2 = Bitmap.createScaledBitmap(background2, screenX, screenY, true);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, true);

    }

}