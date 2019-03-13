package com.joshuawyllie.platformer.level;

import com.joshuawyllie.platformer.entity.Coin;
import com.joshuawyllie.platformer.entity.Entity;
import com.joshuawyllie.platformer.entity.Player;
import com.joshuawyllie.platformer.entity.StaticEntity;
import com.joshuawyllie.platformer.util.BitmapPool;

import java.util.ArrayList;

public class LevelManager {

    private int levelWidth;
    private int levelHeight;

    public final ArrayList<Entity> entities = new ArrayList();
    private final ArrayList<Entity> entitiesToAdd = new ArrayList();
    private final ArrayList<Entity> entitiesToRemove = new ArrayList();
    private final ArrayList<Entity> entitiesToResetOnRestart = new ArrayList<>();

    private Player player = null;
    private BitmapPool pool = null;
    private int numCoins = 0;
    private int initNumCoins = 0;

    public LevelManager(final LevelData map, final BitmapPool pool) {
        levelWidth = map.width;
        levelHeight = map.height;
        this.pool = pool;
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
        initNumCoins = numCoins;
    }

    private void createEntity(final String spriteName, final int xPos, final int yPos) {
        Entity entity;
        switch (spriteName) {
            case LevelData.PLAYER:
                entity = new Player(spriteName, xPos, yPos);
                if (player == null) {
                    player = (Player) entity;
                }
                break;
            case LevelData.COIN_YELLOW:
                entity = new Coin(spriteName, xPos, yPos);
                numCoins++;
                break;
            default:
                entity = new StaticEntity(spriteName, xPos, yPos);
                break;
        }
        addEntity(entity);
    }

    public void update(final double dt) {
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

    public void removeEntity(final Entity entity) {
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
        pool.empty();
    }

    public void destroy() {
        cleanUp();
    }

    public Player getPlayer() {
        return player;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public void onCoinCollision(Entity collisionWith) {
        removeEntity(collisionWith);
        entitiesToResetOnRestart.add(collisionWith);
        numCoins--;
    }

    public void restart() {
        entitiesToAdd.addAll(entitiesToResetOnRestart);
        entitiesToResetOnRestart.clear();
        numCoins = initNumCoins;
    }
}
