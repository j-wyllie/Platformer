package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.joshuawyllie.platformer.entity.Entity;
import com.joshuawyllie.platformer.input.InputManager;
import com.joshuawyllie.platformer.level.LevelManager;
import com.joshuawyllie.platformer.level.LevelOne;
import com.joshuawyllie.platformer.util.BitmapPool;

import java.util.ArrayList;

import static com.joshuawyllie.platformer.GameEvent.*;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    public static final int STAGE_WIDTH = 1280;
    public static final int STAGE_HEIGHT = 720;
    private static final float METERS_TO_SHOW_X = 0f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 16f;  //the other is calculated at runtime!

    private static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    private static Matrix viewTransform = new Matrix();
    private static final PointF cameraPosition = new PointF();

    private Thread _gameThread;
    private volatile boolean _isRunning = false;

    private SurfaceHolder holder;
    private Paint paint;
    private Canvas canvas;

    public BitmapPool pool = null;
    private ArrayList<Entity> visibleEntities = new ArrayList<>();
    private Viewport camera = null;
    private LevelManager level = null;
    private InputManager controls = new InputManager();
    private Hud hud = null;

    public Game(Context context) {
        super(context);
        init();
    }

    public Game(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Game(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public InputManager getControls() {
        return controls;
    }

    public void setControls(final InputManager controls) {
        this.controls.onPause();
        this.controls.onStop();
        this.controls = controls;
    }

    private void init() {
        Entity.setGame(this);
        holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        paint = new Paint();
        camera = new Viewport(1280, 720, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        pool = new BitmapPool(this);
        level = new LevelManager(new LevelOne(getContext()), pool);
        hud = new Hud(this);
        Log.d(TAG, String.format("resolution: %d : %d", STAGE_WIDTH, STAGE_HEIGHT));
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

    public float getWorldWidth() {
        return level.getWidth();
    }

    public float getWorldHeight() {
        return level.getHeight();
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public void run() {
        long lastFrame = System.nanoTime();
        while (_isRunning) {
            final double dt = (lastFrame - System.nanoTime()) * NANOS_TO_SECONDS;
            lastFrame = System.nanoTime();
            update(dt);
            buildVisibleSet();
            render(camera, visibleEntities);
        }
    }

    private void update(final double dt) {
        camera.lookAt(level.getPlayer());
        level.update(dt);
        hud.update(dt);
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

    private void render(final Viewport camera, final ArrayList<Entity> visibleEntities) {
        if (!acquireAndLockCanvas()) {
            return;
        }
        try {
            canvas.drawColor(Color.CYAN);
            for (final Entity entity : visibleEntities) {
                viewTransform.reset();
                camera.worldToScreen(entity, cameraPosition);
                viewTransform.postTranslate(cameraPosition.x, cameraPosition.y);
                entity.render(canvas, paint, viewTransform);
            }
            hud.render(canvas, paint, level.getPlayer().getHealth());
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }


    private boolean acquireAndLockCanvas() {
        if (!holder.getSurface().isValid()) {
            return false;
        }
        canvas = holder.lockCanvas();
        return (canvas != null);
    }

    public void onGameEvent(final GameEvent event, final Entity e /*can be null!*/) {
        switch (event) {
            case DEATH:
                restart();
        }
    }

    private void restart() {
        level.getPlayer().restart();
        hud.restart();
    }

    // Below: Executing on the UI thread

    public void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _gameThread = new Thread(this);
        controls.onResume();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        _isRunning = false;
        controls.onPause();
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
        controls = null;
        _gameThread = null;
        Entity.setGame(null);
        if (pool != null) {
            pool.empty();
            pool = null;
        }
        holder.removeCallback(this);
        hud = null;
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
