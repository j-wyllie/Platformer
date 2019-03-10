package com.joshuawyllie.platformer.entity;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.joshuawyllie.platformer.GameEvent;
import com.joshuawyllie.platformer.input.InputManager;
import com.joshuawyllie.platformer.level.LevelOne;
import com.joshuawyllie.platformer.util.Utils;

public class Player extends DynamicEntity  {
    private static final float PLAYER_RUN_SPEED = 6f;
    private static final float PLAYER_JUMP_VELOCITY = (GRAVITY / 2);
    private static final int INIT_HEALTH = 3;
    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 3;
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final float MIN_INPUT_TO_TURN = 0.05f;
    private static final int NUM_RECOVERY_FRAMES = 50;

    private int facing = LEFT;
    private int health = INIT_HEALTH;
    private float size = 0.8f;
    private int recoveryFrame = 0;
    private boolean recoveryMode = false;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    public float getSize() {
        return size;
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix viewTransform) {
        viewTransform.preScale(facing * size, size);
        if (facing == LEFT) {
            final float offsetX = _game.worldToScreenX(_width);
            viewTransform.postTranslate(offsetX, 0f);
        }
        final float offsetY = _game.worldToScreenY(1 - size);
        viewTransform.postTranslate(0f, offsetY);
        super.render(canvas, paint, viewTransform);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final float direction = controls.horizontalFactor;
        _velX = -direction * PLAYER_RUN_SPEED;
        if (controls.isJumping && isOnGround) {
            _velY = PLAYER_JUMP_VELOCITY;
            isOnGround = false;
        }
        updateFacingDirection(direction);
        updateHealth(dt);
        super.update(dt);
    }

    @Override
    public void restart() {
        super.restart();
        health = INIT_HEALTH;
    }

    private void updateHealth(final double dt) {
        health = (int) Utils.clamp(health, MIN_HEALTH, MAX_HEALTH);
        if (recoveryMode) {
            recoveryFrame++;
            if (recoveryFrame > NUM_RECOVERY_FRAMES) {
                recoveryMode = false;
                recoveryFrame = 0;
            }
        }
        if (health == 0) {
            _game.onGameEvent(GameEvent.DEATH, this);
        }
    }

    private void updateFacingDirection(final float controlDirection) {
        if (Math.abs(controlDirection) < MIN_INPUT_TO_TURN) {
            return;
        }
        if (controlDirection < 0) {
            facing = LEFT;
        } else {
            facing = RIGHT;
        }
    }

    @Override
    public void onCollision(Entity that) {
        super.onCollision(that);
        if (that.getSpriteName().equals(LevelOne.SPEAR)) {
            if (!recoveryMode) {
                health--;
                recoveryMode = true;
            }
        }
    }

    public int getHealth() { return health; }
}
