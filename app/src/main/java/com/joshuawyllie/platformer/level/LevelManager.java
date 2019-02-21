package com.joshuawyllie.platformer.level;

import com.joshuawyllie.platformer.entity.Entity;
import com.joshuawyllie.platformer.entity.Player;
import com.joshuawyllie.platformer.entity.StaticEntity;

import java.util.ArrayList;

public class LevelManager {

    private int levelWidth;
    private int levelHeight;

    public final ArrayList<Entity> entities = new ArrayList();
    private final ArrayList<Entity> entitiesToAdd = new ArrayList();
    private final ArrayList<Entity> entitiesToRemove = new ArrayList();


    public LevelManager(final LevelData map) {
        levelWidth = map.width;
        levelHeight = map.height;
        loadMapAssets(map);
    }

    private void loadMapAssets(LevelData map) {
        cleanUp();
        for (int x = 0; x < map.width; x++) {
            for (int y = 0; y < map.height; y++) {
                int tileId = map.getTile(x, y);
                if (tileId == LevelData.NO_TILE) { continue; }
                String spriteName = map.getSpriteName(tileId);
                createEntity(spriteName, x, y);
            }
        }
    }

    private void createEntity(final String spriteName, final int xPos, final int yPos) {
        Entity entity;
        if (spriteName == LevelData.PLAYER) {
            entity = new Player(spriteName, xPos, yPos);
        } else {
            entity = new StaticEntity(spriteName, xPos, yPos);
        }
        addEntity(entity);
    }

    public void update(final double dt) {
        for (Entity entity : entities) {
            entity.update(dt);
        }
        refreshEntities();
    }

    private void refreshEntities() {
        for (Entity entity : entitiesToRemove) {
            entities.remove(entity);
        }
        for (Entity entity : entitiesToAdd) {
            entities.add(entity);
        }
        entitiesToRemove.clear();
        entitiesToAdd.clear();
    }

    private void addEntity(final Entity entity) {
        if (entity != null) {
            entitiesToAdd.add(entity);
        }
    }

    private void removeEntity(final Entity entity) {
        if (entity != null) {
            entitiesToRemove.add(entity);
        }
    }

    private void cleanUp() {
        refreshEntities();
        for (Entity entity : entities) {
            entity.destroy();
        }
        entities.clear();
    }

    public void destroy() {
        cleanUp();
    }
}
