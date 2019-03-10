package com.joshuawyllie.platformer.level;

import android.util.SparseArray;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;

public abstract class LevelData {
    public final static String NULL_SPRITE = "null_sprite";
    public final static String PLAYER = "lightblue_right1";
    public final static int NO_TILE = 0;
    private ArrayList<ArrayList<Integer>> tiles = new ArrayList<ArrayList<Integer>>();
    int width;
    int height;

    void setup(String levelString) {
        parseLevelString(levelString);
        updateLevelDimensions();
    }

    public int getTile(final int x, final int y) {
        return tiles.get(y).get(x);
    }

    void updateLevelDimensions() {
        width = tiles.get(0).size();
        height = tiles.size();
    }

    void parseLevelString(String levelString) {
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
