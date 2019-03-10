package com.joshuawyllie.platformer.level;

import android.content.Context;
import android.util.SparseArray;

import com.joshuawyllie.platformer.R;

public class LevelOne extends LevelData {

    public static final String SPEAR = "spearsup_brown";
    public static final String BACKGROUND = "background";
    public static final String ICE_SQUARE = "zigzagsnow_icesquare";
    public static final String ICE_ROUND_LEFT = "zigzagsnow_ice_2roundleft";
    public static final String ICE_ROUND_RIGHT = "zigzagsnow_ice_2roundright";

    private Context context = null;
    private SparseArray<String> tileIdToSpriteName = new SparseArray<>();
    private String levelString;
    public LevelOne(Context context) {
        this.context = context;

        levelString = context.getResources().getString(R.string.levelOne);

        tileIdToSpriteName.put(0, BACKGROUND);
        tileIdToSpriteName.put(1, PLAYER);
        tileIdToSpriteName.put(2, ICE_SQUARE);
        tileIdToSpriteName.put(3, ICE_ROUND_LEFT);
        tileIdToSpriteName.put(4, ICE_ROUND_RIGHT);
        tileIdToSpriteName.put(5, SPEAR);

        setup(levelString);
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
