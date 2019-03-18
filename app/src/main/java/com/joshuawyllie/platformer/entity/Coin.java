package com.joshuawyllie.platformer.entity;

import com.joshuawyllie.platformer.GameEvent;
import com.joshuawyllie.platformer.level.LevelOne;

public class Coin extends StaticEntity {
    private static final float WIDTH = 0.5f;
    private static final float HEIGHT = 0.5f;
    private static final float FLOATING_HEIGHT = 0.25f;
    private static final float SWAY_DISTANCE = 0.5f;
    private float init_x;
    private float init_y;

    public Coin(String spriteName, float xPos, float yPos) {
        super(spriteName, xPos + FLOATING_HEIGHT, yPos + FLOATING_HEIGHT, WIDTH, HEIGHT);
        init_x = xPos;
        init_y = yPos;
        collidable = false;
    }

    @Override
    public void onCollision(final Entity that) {
        if (that.getSpriteName().equals(LevelOne.PLAYER)) {
            game.onGameEvent(new GameEvent(GameEvent.Type.COIN_COLLISON, this));
        }
    }

    private int direction = 1;
    @Override
    public void update(final double dt) {
        if (_y < init_y - SWAY_DISTANCE / 2) {
            direction = -1;
        } else if (_y > init_y + SWAY_DISTANCE / 2) {
            direction = 1;
        }
        _y += direction * dt;
    }

    @Override
    public void restart() {
        super.restart();
        _x = init_x;
        _y = init_y;
    }
}