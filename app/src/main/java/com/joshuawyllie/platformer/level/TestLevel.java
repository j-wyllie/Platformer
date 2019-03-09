package com.joshuawyllie.platformer.level;

import android.util.SparseArray;

public class TestLevel extends LevelData {

    public static final String SPEAR = "spearsup_brown";
    public static final String BACKGROUND = "background";
    public static final String ICE_SQUARE = "zigzagsnow_icesquare";
    public static final String ICE_ROUND_LEFT = "zigzagsnow_ice_2roundleft";
    public static final String ICE_ROUND_RIGHT = "zigzagsnow_ice_2roundright";
    public final SparseArray<String> tileIdToSpriteName = new SparseArray<>();

    public TestLevel() {
        tileIdToSpriteName.put(0, BACKGROUND);
        tileIdToSpriteName.put(1, PLAYER);
        tileIdToSpriteName.put(2, ICE_SQUARE);
        tileIdToSpriteName.put(3, ICE_ROUND_LEFT);
        tileIdToSpriteName.put(4, ICE_ROUND_RIGHT);
        tileIdToSpriteName.put(5, SPEAR);

        tiles = new int[][] {
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,1,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,3,2,2,2,2,2,2,2,5,5,4,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},

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
