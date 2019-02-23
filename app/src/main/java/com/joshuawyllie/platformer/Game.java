package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.joshuawyllie.platformer.entity.Entity;
import com.joshuawyllie.platformer.level.LevelManager;
import com.joshuawyllie.platformer.level.TestLevel;

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    public static final int STAGE_WIDTH = 1280;
    public static final int STAGE_HEIGHT = 720;
    Viewport camera = null;
    private static final float METERS_TO_SHOW_X = 16f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 0f;  //the other is calculated at runtime!

    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    private static Matrix viewTransform = new Matrix();

    private Thread _gameThread;
    private volatile boolean _isRunning = false;

    private SurfaceHolder holder;
    private Paint paint;
    private Canvas _canvas;

    private ArrayList<Entity> visibleEntities = new ArrayList<>();
    private LevelManager level = null;

    public Game(Context context) {
        super(context);
        Entity.setGame(this);
        holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        paint = new Paint();
        camera = new Viewport(1280, 720, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        level = new LevelManager(new TestLevel());
    }

    public int worldToScreenX(float worldMeters) {
        return (int) (worldMeters * camera.getPixelsPerMeterX());
    }
    public int worldToScreenY(float worldMeters) {
        return (int) (worldMeters * camera.getPixelsPerMeterY());
    }
    public float screenToWorldX(float pixelDistance) {
        return pixelDistance / camera.getPixelsPerMeterX();
    }
    public float screenToWorldY(float pixelDistance) {
        return pixelDistance / camera.getPixelsPerMeterY();
    }
    public float getWorldWidth() { return level.getWidth(); }
    public float getWorldHeight() { return level.getHeight(); }

    public static int getScreenWidth() { return Resources.getSystem().getDisplayMetrics().widthPixels; }
    public static int getScreenHeight() { return Resources.getSystem().getDisplayMetrics().heightPixels; }


    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        while (_isRunning) {
            final double dt = (lastFrame - System.nanoTime()) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            update(dt);
            buildVisibleSet();
            render(visibleEntities);
        }
    }

    private void update(final double dt) {
        camera.lookAt(level.getWidth() / 2, level.getHeight() / 2);
        level.update(dt);
        for (Entity entity : level.entities) {
            entity.update(dt);
        }
    }

    private void buildVisibleSet() {
        visibleEntities.clear();
        for (final Entity entity : level.entities) {
            if (camera.inView(entity)) {
                visibleEntities.add(entity);
            }
        }
    }

    private static final Point position = new Point();
    private void render(final ArrayList<Entity> visibleEntities) {
        if (!acquireAndLockCanvas()) {
            return;
        }
        try {
            _canvas.drawColor(Color.CYAN);
            for (final Entity entity : visibleEntities) {
                viewTransform.reset();
                camera.worldToScreen(entity, position);
                viewTransform.postTranslate(position.x, position.y);
                entity.render(_canvas, paint, viewTransform);
            }
        } finally {
            holder.unlockCanvasAndPost(_canvas);
        }
    }



    private boolean acquireAndLockCanvas() {
        if (!holder.getSurface().isValid()) {
            return false;
        }
        _canvas = holder.lockCanvas();
        return (_canvas != null);
    }


    public void onGameEvent(final GameEvent event, final Entity e /*can be null!*/) {

    }

    // Below: Executing on the UI thread

    public void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _gameThread = new Thread(this);
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        while (true) {
            try {
                _gameThread.join();
                return;
            } catch (InterruptedException e) {
                Log.d(TAG, Log.getStackTraceString(e.getCause()));
            }
        }
    }

    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        for (Entity entity : visibleEntities) {
            entity.destroy();
        }

        if (level != null) {
            level.destroy();
            level = null;
        }

        _gameThread = null;
        Entity.setGame(null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "Surface created");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "Surface changed");
        Log.d(TAG, "\t Width: " + width + " Height: " + height);
        if (_gameThread != null && _isRunning) {
            Log.d(TAG, "Game thread started");
            _gameThread.start();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
    }
}
