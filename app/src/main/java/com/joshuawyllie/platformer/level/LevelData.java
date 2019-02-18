package com.joshuawyllie.platformer.level;

public abstract class LevelData {
    public final static String NULL_SPRITE = "null_sprite";
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
