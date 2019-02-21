package com.joshuawyllie.platformer.level;

import android.util.SparseArray;

public class TestLevel extends LevelData {

    public final SparseArray<String> tileIdToSpriteName = new SparseArray<>();

    public TestLevel() {
        tileIdToSpriteName.put(0, "background");
        tileIdToSpriteName.put(1, PLAYER);
        tileIdToSpriteName.put(2, "zigzagsnow_icesquare");
        tileIdToSpriteName.put(3, "zigzagsnow_ice_2roundleft");
        tileIdToSpriteName.put(4, "zigzagsnow_ice_2roundright");

        tiles = new int[][] {
                {0,0,0,0,0,0,0},
                {0,0,1,0,0,0,0},
                {0,3,2,2,2,4,0},
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0}
        };
        updateLevelDimensions();
    }

    @Override
    public String getSpriteName(int tileNum) {
        final String spriteName = tileIdToSpriteName.get(tileNum);
        if (spriteName != null) {
            return spriteName;
        }
        return NULL_SPRITE;
    }
}
