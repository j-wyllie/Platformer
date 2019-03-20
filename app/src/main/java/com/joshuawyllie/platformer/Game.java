package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.joshuawyllie.platformer.display.Hud;
import com.joshuawyllie.platformer.display.Viewport;
import com.joshuawyllie.platformer.entity.Entity;
import com.joshuawyllie.platformer.input.Accelerometer;
import com.joshuawyllie.platformer.input.InputManager;
import com.joshuawyllie.platformer.level.LevelData;
import com.joshuawyllie.platformer.level.LevelManager;
import com.joshuawyllie.platformer.level.LevelOne;
import com.joshuawyllie.platformer.util.BitmapPool;

import java.util.ArrayList;

public class Game extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    public static final String TAG = "Game";
    public static final int STAGE_WIDTH = 1280;
    public static final int STAGE_HEIGHT = 720;
    private static final float METERS_TO_SHOW_X = 0f; //set the value you want fixed
    private static final float METERS_TO_SHOW_Y = 16f;  //the other is calculated at runtime!
    public static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    private RectF worldEdges = null;

    private static Matrix viewTransform = new Matrix();
    private static final PointF cameraPosition = new PointF();

    private Thread _gameThread;
    private volatile boolean _isRunning = false;
    private volatile boolean adjustRes = false;
    private int width;
    private int height;

    private SurfaceHolder holder;
    private Paint paint;
    private Canvas canvas;

    MainActivity activity = null;
    public BitmapPool pool = null;
    private ArrayList<Entity> visibleEntities = new ArrayList<>();
    private Viewport camera = null;
    private LevelManager level = null;
    private LevelData currentLevel = null;
    private InputManager controls = new InputManager();
    private Hud hud = null;
    private Jukebox jukebox = null;
    private Accelerometer accelerometer = null;
    private boolean usingAccelerometer = false;

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

    public void setAccelerometer(Accelerometer accelerometer) {
        this.accelerometer.onPause();
        this.accelerometer.onResume();
        this.accelerometer = accelerometer;
        this.controls = accelerometer;
    }

    public void setUsingAccelerometer(boolean usingAccelerometer) {
        this.usingAccelerometer = usingAccelerometer;
    }

    private void init() {
        Entity.setGame(this);
        holder = getHolder();
        holder.addCallback(this);
        holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        paint = new Paint();
        camera = new Viewport(STAGE_WIDTH, STAGE_HEIGHT , METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        pool = new BitmapPool(this);
        currentLevel = new LevelOne(getContext());
        level = new LevelManager(currentLevel, pool);
        worldEdges = new RectF(-1f, 0f, currentLevel.getWidth(), currentLevel.getHeight());
        camera.setBounds(worldEdges);
        hud = new Hud(this, camera);
        activity = (MainActivity) getContext();
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        jukebox = new Jukebox(activity);
        jukebox.resumeBgMusic();
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
        controls.update(dt);
        for (Entity entity : level.entities) {
            entity.update(dt);
        }
        if (adjustRes) {
            adjustRes = false;
            onSizeChange(width, height);
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
            hud.render(canvas, paint, level.getPlayer().getHealth(), level.getNumCoins(), level.getPlayer().getNumbeOfCollectables());
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

    public void onGameEvent(final GameEvent event) {
        jukebox.playSoundForGameEvent(event);
        switch (event.getType()) {
            case DEATH:
                restart();
                break;
            case COIN_COLLISON:
                level.onCoinCollision(event.getEntity());
                level.getPlayer().onCoinCollision();
                break;
        }
    }

    private void restart() {
        level.restart();
        level.getPlayer().restart();
        hud.restart();
    }

    // Below: Executing on the UI thread

    public void onResume() {
        Log.d(TAG, "onResume");
        _isRunning = true;
        _gameThread = new Thread(this);
        controls.onResume();
        jukebox.resumeBgMusic();
    }

    public void onPause() {
        Log.d(TAG, "onPause");
        jukebox.pauseBgMusic();
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
            adjustRes = true;
            this.width = width;
            this.height = height;
            if (isPortrait(getContext())) {
                holder.setFixedSize(STAGE_HEIGHT, STAGE_WIDTH);
            } else {
                holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
            }
            Log.d(TAG, "Game thread started");
            _gameThread.start();
        }
    }

    private void onSizeChange(int width, int height) {
        camera = new Viewport(width, height, METERS_TO_SHOW_X, METERS_TO_SHOW_Y);
        camera.setBounds(worldEdges);
        for (Entity entity : level.entities) {
            entity.onSizeChange();
        }
    }

    public boolean isPortrait(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return true;
            case Surface.ROTATION_90:
                return false;
            case Surface.ROTATION_180:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface destroyed");
    }

    public Hud getHud() {
        return hud;
    }

    public RectF getWorldEdges() {
        return worldEdges;
    }
}
