package com.joshuawyllie.platformer.level;

import android.util.SparseArray;

public class TestLevel extends LevelData {

    private final static String PLAYER = "LightBlue_Front1";

    public final SparseArray<String> tileIdToSpriteName = new SparseArray<>();

    public TestLevel() {
        tileIdToSpriteName.put(0, "background");
        tileIdToSpriteName.put(1, PLAYER);
        tileIdToSpriteName.put(2, "ZigzagSnow_IceSquare");
        tileIdToSpriteName.put(3, "ZigzagSnow_Ice_2RoundLeft");
        tileIdToSpriteName.put(4, "ZigzagSnow_Ice_2RoundRight");

        tiles = new int[][] {
                {0,0,0,0,0,0,0},
                {0,0,1,0,0,0,0},
                {0,3,2,2,2,4,0},
                {0,0,0,0,0,0,0},
                {0,0,0,0,0,0,0}
        };
        updateLevelDimentions();
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
