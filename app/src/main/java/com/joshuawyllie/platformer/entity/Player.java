package com.joshuawyllie.platformer.entity;

import com.joshuawyllie.platformer.Game;
import com.joshuawyllie.platformer.util.Utils;

public class Player extends StaticEntity {

    private final static int tagetHeight = 100;
    private final static int STARTING_POS = 40;
    private final static int STARTING_HEALTH = 3;
    private final static float ACC = 1.1f;
    private final static float MIN_VEL = 1f;
    private final static float MAX_VEL = 10f;
    private final static float GRAVITY = 1.1f;
    private final static float LIFT = -2f;
    private final static float DRAG = 0.97f;
    private final static int NUM_RECOVERY_FRAMES = 5;

    int _health = STARTING_HEALTH;
    private boolean _recovery = false;
    private int _framesPast = 0;

    Player(final String spriteName, final int xPos, final int yPos) {
        super(spriteName, xPos, yPos);
        //loadBitmap(R.drawable.player_ship, tagetHeight);
        respawn();
    }

    @Override
    public void update() {
        _y += _velY;

        _framesPast++;
    }


    @Override
    public void onCollision(Entity that) {
        if (_framesPast > NUM_RECOVERY_FRAMES) {
            _recovery = false;
            _framesPast = 0;
        }

      if (!_recovery) {
            _recovery = true;
            _health--;
            _framesPast = 0;
        }
    }

}
