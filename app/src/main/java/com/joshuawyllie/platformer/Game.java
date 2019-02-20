package com.joshuawyllie.platformer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public static final int STAGE_HEIGHT = 720 ;


    private Thread _gameThread;
    private volatile boolean _isRunning = false;

    private SurfaceHolder _holder;
    private Paint _paint;
    private Canvas _canvas;

    private ArrayList<Entity> _entities = new ArrayList<>();
    private LevelManager level = null;

    public Game(Context context) {
        super(context);
        Entity.setGame(this);
        _holder = getHolder();
        _holder.addCallback(this);
        _holder.setFixedSize(STAGE_WIDTH, STAGE_HEIGHT);
        _paint = new Paint();
        level = new LevelManager(new TestLevel());
    }

    public int worldToScreenX(float widthMeters) {
        return (int) widthMeters * 50;
    }     //todo

    public int worldToScreenY(float heightMeters) {
        return (int) heightMeters * 50;
    }


    private void restart() {
        for (Entity entity : _entities) {
            entity.respawn();
        }
    }

    @Override
    public void run() {
        while (_isRunning) {
            update();
            render();
        }
    }

    private void update() {

    }


    private void render() {
        if (!acquireAndLockCanvas()) return;
        _canvas.drawColor(Color.BLACK);
        for (Entity entity : _entities) {
            entity.render(_canvas, _paint);
        }
        _holder.unlockCanvasAndPost(_canvas);
    }



    private boolean acquireAndLockCanvas() {
        if (!_holder.getSurface().isValid()) {
            return false;
        }
        _canvas = _holder.lockCanvas();
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

        for (Entity entity : _entities) {
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
