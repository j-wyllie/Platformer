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

    private Player player = null;


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
            if (player == null) {
                player = (Player) entity;
            }
        } else {
            entity = new StaticEntity(spriteName, xPos, yPos);
        }
        addEntity(entity);
    }

    public void update(final double dt) {
        for (Entity entity : entities) {
            entity.update(dt);
        }
        checkCollisions();
        refreshEntities();
    }

    private void checkCollisions() {
        final int count = entities.size();
        Entity a, b;
        for (int i = 0;  i < count - 1; i++) {
            a = entities.get(i);
            for (int j = i + 1; j < count; j++) {
                b = entities.get(j);
                if (a.isColliding(b)) {
                    a.onCollision(b);
                    b.onCollision(a);
                }
            }
        }
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

    public int getWidth() {
        return levelWidth;
    }

    public int getHeight() {
        return  levelHeight;
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
        player = null;
    }

    public void destroy() {
        cleanUp();
    }
}
