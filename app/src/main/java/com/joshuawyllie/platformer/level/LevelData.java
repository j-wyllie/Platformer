package com.joshuawyllie.platformer.level;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public abstract class LevelData {
    public static final String NULL_SPRITE = "null_sprite";
    public static final String PLAYER = "lightblue_right1";
    public static final String SPEAR = "spearsup_brown";
    public static final String BACKGROUND = "background";
    public static final String ICE_SQUARE = "zigzagsnow_icesquare";
    public static final String ICE_ROUND_LEFT = "zigzagsnow_ice_2roundleft";
    public static final String ICE_ROUND_RIGHT = "zigzagsnow_ice_2roundright";
    public static final String COIN_YELLOW = "coinyellow_shade";
    public static final String HEART_FULL = "lifeheart_full";
    public static final String HEART_EMPTY = "lifeheart_empty";
    public static final int NO_TILE = 0;

    private ArrayList<ArrayList<Integer>> tiles = new ArrayList<>();
    int width;
    int height;

    void setup(String levelString) {
        parseLevelString(levelString);
        updateLevelDimensions();
    }

    int getTile(final int x, final int y) {
        return tiles.get(y).get(x);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    private void updateLevelDimensions() {
        width = tiles.get(0).size();
        height = tiles.size();
    }

    private void parseLevelString(String levelString) {
        CharacterIterator iter = new StringCharacterIterator(levelString);
        int row = 0;
        tiles.add(new ArrayList<Integer>());
        while (iter.current() != StringCharacterIterator.DONE) {
            char c = iter.current();
            switch (c) {
                case ' ':
                    break;
                case '\n':
                    row++;
                    tiles.add(new ArrayList<Integer>());
                    break;
                default:
                    int id = Integer.parseInt(String.valueOf(c));
                    tiles.get(row).add(id);
            }
            iter.next();
        }
        tiles.remove(tiles.size() - 1);
    }

    abstract public String getSpriteName(final int tileNum);
}
