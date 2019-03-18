package com.joshuawyllie.platformer.display;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.joshuawyllie.platformer.Game;
import com.joshuawyllie.platformer.R;
import com.joshuawyllie.platformer.level.LevelData;

public class Hud {

    private static final float HUD_SIZE = 1f;
    private static final float HEART_SPACE = 55f;
    private static final String PREFS = "com.joshuawyllie.platformer";

    private Game game;
    private Viewport camera;
    private Context context;
    private SettingsMenu settingsMenu = null;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Bitmap fullHeart = null;
    private Bitmap emptyHeart = null;
    private int screenWidth = Game.STAGE_WIDTH;
    private int screenHeight = Game.STAGE_HEIGHT;

    public Hud(Game game, Viewport camera) {
        this.game = game;
        this.camera = camera;
        context = game.getContext();
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        editor = prefs.edit();
        fullHeart = game.pool.createBitmap(LevelData.HEART_FULL, HUD_SIZE, HUD_SIZE);
        emptyHeart = game.pool.createBitmap(LevelData.HEART_EMPTY, HUD_SIZE, HUD_SIZE);
    }

    public void render(final Canvas canvas, final Paint paint, int health, int collectablesLeft, int collectiblesCollected) {
        paint.setColor(Color.MAGENTA);
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(HUD_SIZE * camera.getPixelsPerMeterX());
        final float centerX = screenWidth / 2f;
        final float centerY = screenHeight / 2f;
        final float textMarginX = screenWidth - 100;
        final float textMarginY = 50;
        canvas.drawText(String.format(context.getString(R.string.collectables_left), collectablesLeft), textMarginX , textMarginY, paint);
        canvas.drawText(String.format(context.getString(R.string.collectibles_collected), collectiblesCollected), textMarginX, textMarginY * 2, paint);
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

    public void setSettingsMenu(SettingsMenu settingsMenu) {
        this.settingsMenu = settingsMenu;
    }
}
