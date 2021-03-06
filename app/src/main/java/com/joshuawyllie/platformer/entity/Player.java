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
    private static final float WIDTH = 0.8f;
    private static final float HEIGHT = 0.8f;

    private int facing = LEFT;
    private int health = INIT_HEALTH;
    private int recoveryFrame = 0;
    private boolean recoveryMode = false;
    private int namCollectibles = 0;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos, WIDTH, HEIGHT);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix viewTransform) {
        viewTransform.preScale(facing, 1f);
        if (facing == LEFT) {
            final float offsetX = game.worldToScreenX(_width);
            viewTransform.postTranslate(offsetX, 0f);
        }
        super.render(canvas, paint, viewTransform);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = game.getControls();
        final float direction = controls.horizontalFactor;
        velX = -direction * PLAYER_RUN_SPEED;
        if (controls.isJumping && isOnGround) {
            game.onGameEvent(new GameEvent(GameEvent.Type.JUMP));
            velY = PLAYER_JUMP_VELOCITY;
            isOnGround = false;
        }
        if (_y > game.getWorldEdges().bottom) {
            game.onGameEvent(new GameEvent(GameEvent.Type.DEATH));
        }
        updateFacingDirection(direction);
        updateHealth(dt);
        super.update(dt);
    }

    @Override
    public void restart() {
        super.restart();
        health = INIT_HEALTH;
        namCollectibles = 0;
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
            game.onGameEvent(new GameEvent(GameEvent.Type.DEATH));
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
        switch (that.getSpriteName()) {
            case LevelOne.SPEAR:
                damageTaken();
                break;
        }
    }

    private void damageTaken() {
        if (!recoveryMode) {
            game.onGameEvent(new GameEvent(GameEvent.Type.DAMAGE));
            health--;
            recoveryMode = true;
        }
    }

    public void onCoinCollision() {
        namCollectibles++;
    }

    public int getNumbeOfCollectables() {
        return namCollectibles;
    }

    public int getHealth() { return health; }
}
