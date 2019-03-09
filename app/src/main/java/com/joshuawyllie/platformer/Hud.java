package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Hud {

    private static final int HUD_SIZE = 1;
    private static final int HUD_MARGIN = 4;
    private static final int HEART_SPACE = 55;
    private static final String PREFS = "com.joshuawyllie.platformer";
    private static final String LONGEST_DIST = "longest_distance";


    private static Game game = null;
    private static Context context = null;
    private SharedPreferences _prefs = null;
    private SharedPreferences.Editor _editor = null;
    private static int _distanceTraveled = 0;
    private static int _maxDistanceTraveled = 0;
    private Bitmap fullHeart = null;
    private Bitmap emptyHeart = null;

    public Hud(Game game) {
        context = game.getContext();
        _prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        _editor = _prefs.edit();
        fullHeart = game.pool.createBitmap("lifeheart_full", HUD_SIZE, HUD_SIZE);
        emptyHeart = game.pool.createBitmap("lifeheart_empty", HUD_SIZE, HUD_SIZE);
    }

    public void render(final Canvas canvas, final Paint paint, int health) {
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(HUD_SIZE);
        final float centerX = Game.STAGE_WIDTH / 2f;
        final float centerY = Game.STAGE_HEIGHT / 2f;
        canvas.drawText("collectibles left", 1, 1, paint);

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

    }

    public void gameOver() {
    }
}
