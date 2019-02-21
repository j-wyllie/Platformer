package com.joshuawyllie.platformer.entity;

import com.joshuawyllie.platformer.util.Random;
import com.joshuawyllie.platformer.util.Utils;

public class DynamicEntity extends StaticEntity {

    private static final float MAX_DELTA = 0.48f;
    static final float GRAVITY = 40f;

    float _velX = 0;
    float _velY = 0;
    float gravity = GRAVITY;

    public DynamicEntity(String spriteName, int xPos, int yPos) {
        super(spriteName, xPos, yPos);
    }

    @Override
    public void update(double dt) {
        _x += Utils.clamp((float) (_velX * dt), -MAX_DELTA, MAX_DELTA);
        _velY += gravity * dt;
        _y += Utils.clamp((float) (_velY * dt), -MAX_DELTA, MAX_DELTA);
        if (_y > _game.getHeight()) {
            _y = Random.between(-4f, 0f);
        }
    }

    @Override
    public void onCollision(Entity that) {
        super.onCollision(that);
    }
}
