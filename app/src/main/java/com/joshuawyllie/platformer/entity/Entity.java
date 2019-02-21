package com.joshuawyllie.platformer.entity;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.joshuawyllie.platformer.Game;

public abstract class Entity {

    static final String TAG = "Entity";
    static Game _game = null; //shared ref, managed by the Game-class!
    float _x = 0;
    float _y = 0;
    float _width = 0;
    float _height = 0;

    public void update(final double dt) {}
    public void render(final Canvas canvas, final Paint paint, final Matrix viewTransform) {}
    public void onCollision(final Entity that) {}
    public void destroy() {}

    public float getX() { return _x; }
    public float getY() { return  _y; }
    public float left() {
        return _x;
    }
    public float right() {
        return _x + _width;
    }
    public float top() {
        return _y;
    }
    public float bottom() {
        return _y + _height;
    }
    public float centerX() {
        return _x + (_width * 0.5f);
    }
    public float centerY() {
        return _y + (_height * 0.5f);
    }

    public void setLeft(final float leftEdgePosition) {
        _x = leftEdgePosition;
    }
    public void setRight(final float rightEdgePosition) {
        _x = rightEdgePosition - _width;
    }
    public void setTop(final float topEdgePosition) {
        _y = topEdgePosition;
    }
    public void setBottom(final float bottomEdgePosition) {
        _y = bottomEdgePosition - _height;
    }
    public void setCenter(final float x, final float y) {
        _x = x - (_width * 0.5f);
        _y = y - (_height * 0.5f);
    }

    public boolean isColliding(final Entity that) {
        if (this == that) {
            throw new AssertionError("isColliding: You shouldn't test Entities against themselves!");
        }
        return Entity.isAABBOverlapping(this, that);
    }

    //Some good reading on bounding-box intersection tests:
    //https://gamedev.stackexchange.com/questions/586/what-is-the-fastest-way-to-work-out-2d-bounding-box-intersection
    static boolean isAABBOverlapping(final Entity a, final Entity b) {
        return !(a.right() <= b.left()
                || b.right() <= a.left()
                || a.bottom() <= b.top()
                || b.bottom() <= a.top());
    }

    public static void setGame(Game game) {
        _game = game;
    }
}
