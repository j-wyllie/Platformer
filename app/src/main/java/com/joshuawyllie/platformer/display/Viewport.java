package com.joshuawyllie.platformer.display;

import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.joshuawyllie.platformer.entity.Entity;

public class Viewport {
    private final static float BUFFER = 2f; //overdraw, to avoid visual gaps
    private final PointF mLookAt = new PointF(0f, 0f);
    private int mPixelsPerMeterX; //viewport "density"
    private int mPixelsPerMeterY;
    private int mScreenWidth; //resolution
    private int mScreenHeight;
    private int mScreenCenterY; //center screen
    private int mScreenCenterX;
    private float mMetersToShowX; //Field of View
    private float mMetersToShowY;
    private float mHalfDistX; //cached value (0.5*FOV)
    private float mHalfDistY;
    private RectF worldEdges = null;

    public Viewport(final int screenWidth, final int screenHeight, final float metersToShowX, final float metersToShowY) {
        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
        mScreenCenterX = mScreenWidth / 2;
        mScreenCenterY = mScreenHeight / 2;
        mLookAt.x = 0.0f;
        mLookAt.y = 0.0f;
        setMetersToShow(metersToShowX, metersToShowY);
    }

    //setMetersToShow calculates the number of physical pixels per meters
    //so that we can translate our game world (meters) to the screen (pixels)
    //provide the dimension(s) you want to lock. The viewport will automatically
    // size the other axis to fill the screen perfectly.
    private void setMetersToShow(float metersToShowX, float metersToShowY) {
        if (metersToShowX <= 0f && metersToShowY <= 0f)
            throw new IllegalArgumentException("One of the dimensions must be provided!");
        //formula: new height = (original height / original width) x new width
        mMetersToShowX = metersToShowX;
        mMetersToShowY = metersToShowY;
        if (metersToShowX == 0f || metersToShowY == 0f) {
            if (metersToShowY > 0f) { //if Y is configured, calculate X
                mMetersToShowX = ((float) mScreenWidth / mScreenHeight) * metersToShowY;
            } else { //if X is configured, calculate Y
                mMetersToShowY = ((float) mScreenHeight / mScreenWidth) * metersToShowX;
            }
        }
        mHalfDistX = (mMetersToShowX + BUFFER) * 0.5f;
        mHalfDistY = (mMetersToShowY + BUFFER) * 0.5f;
        mPixelsPerMeterX = (int) (mScreenWidth / mMetersToShowX);
        mPixelsPerMeterY = (int) (mScreenHeight / mMetersToShowY);
    }

    public float getHorizontalView() {
        return mMetersToShowX;
    }

    public float getVerticalView() {
        return mMetersToShowY;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public int getPixelsPerMeterX() {
        return mPixelsPerMeterX;
    }

    public int getPixelsPerMeterY() {
        return mPixelsPerMeterY;
    }

    public boolean inView(final Entity e) {
        final float maxX = (mLookAt.x + mHalfDistX);
        final float minX = (mLookAt.x - mHalfDistX) - e.getWidth();
        final float maxY = (mLookAt.y + mHalfDistY);
        final float minY = (mLookAt.y - mHalfDistY) - e.getHeight();
        return (e.getX() > minX && e.getX() < maxX)
                && (e.getY() > minY && e.getY() < maxY);
    }

    public boolean inView(final RectF bounds) {
        final float right = (mLookAt.x + mHalfDistX);
        final float left = (mLookAt.x - mHalfDistX);
        final float bottom = (mLookAt.y + mHalfDistY);
        final float top = (mLookAt.y - mHalfDistY);
        return (bounds.left < right && bounds.right > left)
                && (bounds.top < bottom && bounds.bottom > top);
    }

    public void lookAt(final float x, final float y) {
        PointF offset = worldBoundsOffset(x, y);
        if (offset == null) {
            mLookAt.x = x;
            mLookAt.y = y;
        } else {
            mLookAt.x = x + offset.x;
            mLookAt.y = y + offset.y;
        }
    }

    public void lookAt(final Entity obj) {
        lookAt(obj.centerX(), obj.centerY());
    }

    public void lookAt(final PointF pos) {
        lookAt(pos.x, pos.y);
    }

    public void worldToScreen(final float worldPosX, final float worldPosY, PointF screenPos) {
        screenPos.x = ((float) mScreenCenterX - ((mLookAt.x - worldPosX) * (float) mPixelsPerMeterX));
        screenPos.y = ((float) mScreenCenterY - ((mLookAt.y - worldPosY) * (float) mPixelsPerMeterY));
    }

    public void worldToScreen(final PointF worldPos, PointF screenPos) {
        worldToScreen(worldPos.x, worldPos.y, screenPos);
    }

    public void worldToScreen(final Entity e, final PointF screenPos) {
        worldToScreen(e.getX(), e.getY(), screenPos);
    }

    public void setBounds(final RectF worldEdges) {
        this.worldEdges = worldEdges;
    }

    private PointF worldBoundsOffset(final float x, final float y) {
        if (worldEdges.isEmpty()) { return null; }
        PointF offset = new PointF();
        final float right = (x + mHalfDistX);
        final float left = (x - mHalfDistX);
        final float bottom = (y + mHalfDistY);
        final float top = (y - mHalfDistY);
        if (worldEdges.left <= left && worldEdges.right >= right && worldEdges.top <= top && worldEdges.bottom >= bottom) {
            return null;
        }
        if (worldEdges.left - left > 0) {
            offset.x = worldEdges.left - left;
        } else if (worldEdges.right - right < 0) {
            offset.x = worldEdges.right - right;
        }
        if (worldEdges.top - top > 0) {
            offset.y = worldEdges.top - top;
        } else if (worldEdges.bottom - bottom < 0) {
            offset.y = worldEdges.bottom - bottom;
        }
        return offset;
    }
}
