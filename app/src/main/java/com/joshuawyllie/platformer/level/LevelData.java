package com.joshuawyllie.platformer.level;

public abstract class LevelData {
    public final static String NULL_SPRITE = "null_sprite";
    public final static String PLAYER = "lightblue_front1";
    public final static int NO_TILE = 0;
    int[][] tiles;
    int width;
    int height;

    public int getTile(final int x, final int y) {
        return tiles[x][y];
    }

    void updateLevelDimentions() {
        width = tiles.length;
        height = tiles[0].length;
    }

    abstract public String getSpriteName(final int tileNum);
}
