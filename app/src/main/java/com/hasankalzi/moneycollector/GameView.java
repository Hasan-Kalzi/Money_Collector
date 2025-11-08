package com.hasankalzi.moneycollector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceView;
import java.util.Random;

// SurfaceView is used when you need to update the screen very quickly
@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {
    private final Random random;
    private Thread thread;

    // Two backgrounds to make it look like it’s moving
    private Background background1, background2;
    private int screenX; int screenY;
    static public float screenRatioX, screenRatioY;
    // isPlaying must start as false, otherwise it won’t start
    private boolean isPlaying = false;
    // Character
    private Char chara;
    // Rockets
    private Rockets[] rockets;
    // Coins
    private Coins[] coins;
    // Paint for Canvas
    private Paint paint;
    // Score
    int score=0;

    // Media players for game sounds
    static MediaPlayer gameOnsound;
    static MediaPlayer gameOnsound2;
    static MediaPlayer collectedCoin;
    static MediaPlayer gameOver;
    private static int mediaLooper = 1;

    float touchY = Char.y;
    boolean gameIsOver;


    public GameView(Context context, int screenX, int screenY){
        super(context);

        // Initialising media players for the game’s sounds
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        gameOnsound2 = MediaPlayer.create(context,R.raw.gameon2);
        collectedCoin = MediaPlayer.create(context,R.raw.coin_collect);
        gameOver = MediaPlayer.create(context,R.raw.gameover);

        // Initialising the backgrounds for the game’s canvas
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        // Background moves only along the X-axis
        // We position background2 after background1
        background2.x = screenX;

        // Create character object
        chara = new Char(getResources());

        // Initialising the coin class objects
        coins = new Coins[4];

        for (int i = 0; i < 4; i++) {
            Coins coin = new Coins(getResources());
            coins[i] = coin;
        }

        random = new Random();

        // Initialising the rocket class objects
        rockets = new Rockets[1];

        for (int i = 0; i < 1; i++) {
            Rockets rocket = new Rockets(getResources());
            rockets[i] = rocket;
        }

        this.paint = new Paint();
    }

    @Override
    public void run(){

        while(isPlaying) {
            // Loops between two music files
            if (!gameOnsound.isPlaying() && !gameOnsound2.isPlaying()){
                if (mediaLooper == 1){
                    gameOnsound.start();
                    mediaLooper = 2;
                }
                else if (mediaLooper == 2){
                    gameOnsound2.start();
                    mediaLooper = 1;
                }
            }
            draw();
            update();
            sleep();
        }

    }
    void resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    void pause(){
        try {
            // Pause the music when the game is paused
            if (gameOnsound.isPlaying()){
                mediaLooper = 1;
                gameOnsound.pause();
            }
            else if (gameOnsound2.isPlaying()){
                mediaLooper = 2;
                gameOnsound2.pause();
            }
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    private void update() {

        background1.x -= 8 * screenRatioX;
        background2.x -= 8 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 120){
            background1.x = screenX;
        }
        if (background2.x + background2.background.getWidth() < 120){
            background2.x = screenX;
        }

        for (Coins coin : coins) {
            coin.x -= coin.speed;
            if (coin.x + coin.width < 0) {

                if (!coin.wasCollected) {
                    coin.x = -1500;
                }
                int bound = (int) (30 * screenRatioX);
                coin.speed = random.nextInt(bound);

                if (coin.speed < 10 * screenRatioX)
                    coin.speed = (int) (10 * screenRatioX);

                coin.x = screenX;
                coin.y = random.nextInt(screenY - coin.height);

                coin.wasCollected = false;
            }
            if(Rect.intersects(chara.getDetectCollision(), coin.getDetectCollision())){
                score++;
                coin.x = -1500;
                collectedCoin.start();
            }
            for (Rockets rocket : rockets) {
                rocket.x -= rocket.speed;
                if (rocket.x + rocket.width < 0) {

                    if (!rocket.wasImpacted) {
                        rocket.x = -1500;
                    }
                    int bound = (int) (10 * screenRatioX);
                    rocket.speed = random.nextInt(bound);

                    if (rocket.speed < 3 * screenRatioX)
                        rocket.speed = (int) (3 * screenRatioX);

                    rocket.x = screenX;
                    rocket.y = random.nextInt(screenY - coin.height);

                    rocket.wasImpacted = false;
                }
                if (Rect.intersects(rocket.getDetectCollision(), chara.getDetectCollision())) {
                    gameIsOver = true;
                    gameOver.start();
                }
            }
        }

        if (!chara.notTouched)
            if (Char.y + chara.height / 2f - touchY < -50)
                Char.y += 20 * screenRatioY;
            else if (Char.y + chara.height / 2f > touchY)
                Char.y -= 20 * screenRatioY;

        if (Char.y < 0) {
            Char.y = 0;
        }
        if (Char.y > screenY - chara.height * screenRatioY) {
            Char.y = screenY - chara.height;
        }

    }

    private void draw() {
        if(getHolder().getSurface().isValid())   {

            // Returns the canvas that will be displayed on screen
            // Canvas
            Canvas canvas = getHolder().lockCanvas();

            // “Draws” our background on the canvas
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background2, background2.x, background2.y, paint);

            // Draws the character
            canvas.drawBitmap(chara.getChar(), Char.x, Char.y, paint);

            // Draws coin images
            for (Coins coin : coins)
                canvas.drawBitmap(coin.getCoin(), coin.x, coin.y, paint);

            // Draws rocket images
            for (Rockets rocket : rockets)
                canvas.drawBitmap(rocket.getRocket(), rocket.x, rocket.y, paint);

            // Draws the score table
            paint.setTextSize(70);
            paint.setColor(Color.WHITE);
            canvas.drawText("Score: " + score, 1300 * screenRatioX, 50 * screenRatioY, paint);

            if (gameIsOver) {
                isPlaying = false;
                GameView.stopMusic();
                canvas.drawBitmap(chara.boom, Char.x, Char.y, paint);
                paint.setTextSize(100);
                paint.setColor(Color.BLACK);
                canvas.drawText("Game Over: " + score, 600 * screenRatioX, 600 * screenRatioY, paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            // The canvas is now ready, so we just need to display it on the screen
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void sleep() {
        try {
            // 50 fps
            Thread.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        gameOnsound.stop();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                chara.notTouched = true;
                break;
            case MotionEvent.ACTION_DOWN:
                touchY = event.getY();
                chara.notTouched = false;
                break;
        }
        // If the game is over, tapping on the Game Over screen sends you back to MainActivity
        if(gameIsOver) {
            try {
                Thread.sleep(3000);
                GameActivity activity = new GameActivity();
                activity.startActivity(new Intent(activity, FullscreenActivity.class));
                activity.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
