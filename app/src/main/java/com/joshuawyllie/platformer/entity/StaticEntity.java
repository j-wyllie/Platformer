package com.joshuawyllie.platformer.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.joshuawyllie.platformer.util.BitmapUtils;

public class StaticEntity extends Entity {

    private static final float DEFAULT_DIMENTIONS = 1.0f;

    protected Bitmap _bitmap = null;

    public StaticEntity(final String spriteName, final int xPos, final int yPos) {
        _width = DEFAULT_DIMENTIONS;
        _height = DEFAULT_DIMENTIONS;
        _x = xPos * _game.worldToScreenX(_width);
        _y = yPos * _game.worldToScreenY(_height);
        loadBitmap(spriteName, 100, 100);
    }

    protected void loadBitmap(final String spriteName, final int xPos, final int yPos) {
        destroy();
        final int widthPixels = _game.worldToScreenX(_width);
        final int heightPixels = _game.worldToScreenY(_height);
        try {
            _bitmap = BitmapUtils.loadScaledBitmap(_game.getContext(), spriteName, widthPixels, heightPixels);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
    @Override
    public void render(Canvas canvas, Paint paint, Matrix viewTransform) {
        canvas.drawBitmap(_bitmap, _x, _y, paint);
    }

    @Override
    public void destroy() {
        if (_bitmap != null) {
            _bitmap.recycle();
            _bitmap = null;
        }
    }
}
