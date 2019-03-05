package com.joshuawyllie.platformer.entity;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.joshuawyllie.platformer.input.InputManager;

public class Player extends DynamicEntity  {
    private static final float PLAYER_RUN_SPEED = 6f;
    private static final float PLAYER_JUMP_VELOCITY = (GRAVITY / 2);
    private static final int LEFT = -1;
    private static final int RIGHT = 1;
    private static final float MIN_INPUT_TO_TURN = 0.05f;
    private static int facing = LEFT;

    public Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
    }

    @Override
    public void render(Canvas canvas, Paint paint, Matrix viewTransform) {
        viewTransform.preScale(facing, 1.0f);
        if (facing == RIGHT) {
            final float offset = -_game.worldToScreenX(_width);
            viewTransform.postTranslate(offset, 0f);
        }
        super.render(canvas, paint, viewTransform);
    }

    @Override
    public void update(final double dt) {
        final InputManager controls = _game.getControls();
        final float direction = controls._horizontalFactor;
        _velX = -direction * PLAYER_RUN_SPEED;
        if (controls._isJumping && isOnGround) {
            _velY = PLAYER_JUMP_VELOCITY;
            isOnGround = false;
        }
        updateFacingDirection(direction);
        super.update(dt);
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
}
