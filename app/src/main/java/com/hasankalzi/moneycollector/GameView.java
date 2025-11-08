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

// Surface view är när man måste ändra saker på skärmen jätte fort
@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {
    private final Random random;
    private Thread thread;

    // Två background så att det ser ut som att den rör sig
    private Background background1, background2;
    private int screenX; int screenY;
    static public float screenRatioX, screenRatioY;
    // isPlaying måste bröja från false, annars startas den ej.
    private boolean isPlaying = false;
    //char
    private Char chara;
    //Rockets
    private Rockets[] rockets;
    //Coins
    private Coins[] coins;
    // Paint för Canvas
    private Paint paint;
    // Score
    int score=0;

    //The media players för spelets ljud
    static MediaPlayer gameOnsound;
    static MediaPlayer gameOnsound2;
    static MediaPlayer collectedCoin;
    static MediaPlayer gameOver;
    private static int mediaLooper = 1;

    float touchY =Char.y;
    boolean gameIsOver;


    public  GameView(Context context, int screenX, int screenY){
        super(context);

        //initializing  media players för spelets ljud
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        gameOnsound2 = MediaPlayer.create(context,R.raw.gameon2);
        collectedCoin= MediaPlayer.create(context,R.raw.coin_collect);
        gameOver= MediaPlayer.create(context,R.raw.gameover);

        //initializing the Backgrounds för spelets  Canvas.
        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        // background rör sig bara i x-led
        // vi placerar background2 efter background1
        background2.x = screenX;


        // Skapar char obj
        chara = new Char(getResources());

        //initializing the coin class object
        coins = new Coins[4];

        for (int i = 0;i < 4;i++) {

            Coins coin = new Coins(getResources());
            coins[i] = coin;

        }
        random = new Random();
        //initializing the rocket class object
        rockets = new Rockets[1];

        for (int i = 0;i < 1;i++) {

            Rockets rocket = new Rockets(getResources());
            rockets[i] = rocket;

        }


        this.paint = new Paint();


    }

    @Override
    public void run(){

        while(isPlaying) {
            // Loopar mellan två music filer
            if (!gameOnsound.isPlaying()&&!gameOnsound2.isPlaying()){
                if (mediaLooper==1){
                    gameOnsound.start();
                    mediaLooper=2;
                }
                else if (mediaLooper==2){
                    gameOnsound2.start();
                    mediaLooper=1;
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
            // pause the music on the game pause
            if (gameOnsound.isPlaying()){
                mediaLooper=1;
                gameOnsound.pause();
            }
            else if (gameOnsound2.isPlaying()){
                mediaLooper=2;
                gameOnsound2.pause();
            }
            isPlaying=false;
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
            if(Rect.intersects(chara.getDetectCollision(),coin.getDetectCollision())){
                score++;
                coin.x=-1500;
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
            if (Char.y+chara.height/2f-touchY<-50)
                Char.y += 20 * screenRatioY;
            else if (Char.y+chara.height/2f>touchY)
                Char.y -= 20 * screenRatioY;

        if (Char.y < 0) {
            Char.y = 0;
        }
        if (Char.y > screenY-chara.height*screenRatioY ) {
            Char.y = screenY-chara.height;
        }

    }

    private void draw() {
        if(getHolder().getSurface().isValid())   {

            // Returner canvasen som ska visas på skärmen
            // Canvas
            Canvas canvas = getHolder().lockCanvas();

            // "Ritar" vår bakgrund på canvasen
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background2, background2.x, background2.y, paint);

            // Ritar nu character
            canvas.drawBitmap(chara.getChar(), Char.x, Char.y, paint);


            //Ritar coins image
            for (Coins coin : coins)
                canvas.drawBitmap(coin.getCoin(), coin.x, coin.y, paint);

            //Ritar rocket image
            for (Rockets rocket : rockets)
                canvas.drawBitmap(rocket.getRocket(), rocket.x, rocket.y, paint);

            // Ritar score table
            paint.setTextSize(70);
            paint.setColor(Color.WHITE);
            canvas.drawText("Score: " + score, 1300*screenRatioX, 50 * screenRatioY, paint);


            if (gameIsOver) {
                isPlaying = false;
                GameView.stopMusic();
                canvas.drawBitmap(chara.boom, Char.x, Char.y, paint);
                paint.setTextSize(100);
                paint.setColor(Color.BLACK);
                canvas.drawText("Game Over: " + score, 600*screenRatioX, 600 * screenRatioY, paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            // Nu är canvasen klar så vi måste bara visa den på skärmen
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
                touchY=event.getY();
                chara.notTouched = false;
                break;
        }
        //if the game's over, tappin on game Over screen sends you to MainActivity
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