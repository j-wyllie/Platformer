package com.joshuawyllie.platformer.entity;

import com.joshuawyllie.platformer.util.Random;
import com.joshuawyllie.platformer.util.Utils;

public class DynamicEntity extends StaticEntity {

    private static final float MAX_DELTA = 0.48f;
    static final float GRAVITY = 55f;

    float init_x;
    float init_y;
    float _velX = 0;
    float _velY = 0;
    float gravity = GRAVITY;
    boolean isOnGround = false;

    public DynamicEntity(String spriteName, int xPos, int yPos) {
        super(spriteName, xPos, yPos);
        init(xPos, yPos);
    }

    public DynamicEntity(String spriteName, float xPos, float yPos, float width, float height) {
        super(spriteName, xPos, yPos, width, height);
        init(xPos, yPos);
    }

    private void init(float xPos, float yPos) {
        init_x = xPos;
        init_y = yPos;
    }

    @Override
    public void update(double dt) {
        _x += Utils.clamp((float) (_velX * dt), -MAX_DELTA, MAX_DELTA);
        if (!isOnGround) {
            _velY += gravity * dt;
        }
        _y += Utils.clamp((float) (_velY * dt), -MAX_DELTA, MAX_DELTA);
        if (_y > _game.getHeight()) {
            _y = Random.between(-4f, 0f);
        }
        isOnGround = false;
    }

    @Override
    public void onCollision(Entity that) {
        if (!collidable || !that.collidable) {
            return;
        }
        Entity.getOverlap(this, that, Entity.overlap);
        _x += Entity.overlap.x;
        _y += Entity.overlap.y;
        if (Entity.overlap.y != 0f) {
            _velY = 0;
            if (Entity.overlap.y < 0f) {
                _velY = 0;
                isOnGround = true;
            }
        }
    }

    @Override
    public void restart() {
        _x = init_x;
        _y = init_y;
    }
}
