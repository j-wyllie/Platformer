package com.joshuawyllie.platformer.entity;

public class Coin extends StaticEntity {
    private static final float WIDTH = 0.5f;
    private static final float HEIGHT = 0.5f;
    private static final float FLOATING_HEIGHT = 0.0f;

    public Coin(String spriteName, float xPos, float yPos) {
        super(spriteName, xPos, yPos - FLOATING_HEIGHT, WIDTH, HEIGHT);
    }
}