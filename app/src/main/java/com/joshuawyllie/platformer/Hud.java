package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class Hud {

    private static final float HUD_SIZE = 1f;
    private static final float HUD_MARGIN = 1f;
    private static final float HEART_SPACE = 55f;
    private static final float TEXT_X = 5f;
    private static final String PREFS = "com.joshuawyllie.platformer";
    private static final String LONGEST_DIST = "longest_distance";

    private Game game = null;
    private Viewport camera = null;
    private Context context = null;
    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;
    private Bitmap fullHeart = null;
    private Bitmap emptyHeart = null;
    private int screenWidth = Game.STAGE_WIDTH;
    private int screenHeight = Game.STAGE_HEIGHT;

    public Hud(Game game, Viewport camera) {
        this.game = game;
        this.camera = camera;
        context = game.getContext();
        _prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        _editor = _prefs.edit();
        fullHeart = game.pool.createBitmap("lifeheart_full", HUD_SIZE, HUD_SIZE);
        emptyHeart = game.pool.createBitmap("lifeheart_empty", HUD_SIZE, HUD_SIZE);
    }

    public void render(final Canvas canvas, final Paint paint, int health, int collectablesLeft, int collectiblesCollected) {
        paint.setColor(Color.MAGENTA);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(HUD_SIZE * camera.getPixelsPerMeterX());
        final float centerX = screenWidth / 2f;
        final float centerY = screenHeight / 2f;
        canvas.drawText("collectibles left: " + collectablesLeft, screenWidth - 50 , 50, paint);
        canvas.drawText("collectibles collected: " + collectiblesCollected, screenWidth - 50, 100, paint);
        switch (health) {
            case 0:
                canvas.drawBitmap(emptyHeart, 4, 4, paint);
                canvas.drawBitmap(emptyHeart, HEART_SPACE + 4, 4, paint);
                canvas.drawBitmap(emptyHeart, 2 * HEART_SPACE + 4, 4, paint);
                break;
            case 1:
                canvas.drawBitmap(fullHeart, 4, 4, paint);
                canvas.drawBitmap(emptyHeart, HEART_SPACE + 4, 4, paint);
                canvas.drawBitmap(emptyHeart, 2 * HEART_SPACE + 4, 4, paint);
                break;
            case 2:
                canvas.drawBitmap(fullHeart, 4, 4, paint);
                canvas.drawBitmap(fullHeart, HEART_SPACE + 4, 4, paint);
                canvas.drawBitmap(emptyHeart, 2 * HEART_SPACE + 4, 4, paint);
                break;
            case 3:
                canvas.drawBitmap(fullHeart, 4, 4, paint);
                canvas.drawBitmap(fullHeart, HEART_SPACE + 4, 4, paint);
                canvas.drawBitmap(fullHeart, 2 * HEART_SPACE + 4, 4, paint);
                break;
        }
    }

    public void restart() {
    }

    public void update(double dt) {
        if (game.isPortrait(context)) {
            screenWidth = Game.STAGE_HEIGHT;
            screenHeight = Game.STAGE_WIDTH;
        }
    }

    public void gameOver() {
    }
}
