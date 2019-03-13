package com.joshuawyllie.platformer;

import com.joshuawyllie.platformer.entity.Entity;

public class GameEvent {

    private Type type;
    private Entity entity1 = null;
    private Entity entity2 = null;

    public enum Type {
        LEVEL_START,
        COLLISION,
        COIN_COLLISON,
        DEATH,
        BOOST
    }

    public GameEvent(Type type, Entity entity1, Entity entity2) {
        this.type = type;
        this.entity1 = entity1;
        this.entity2 = entity2;
    }

    public GameEvent(Type type, Entity entity) {
        this(type, entity, null);
    }

    public GameEvent(Type type) {
        this(type, null, null);
    }

    public Type getType() {
        return type;
    }

    public Entity getEntity() {
        return entity1;
    }

    public Entity getEntity1() {
        return entity1;
    }

    public Entity getEntity2() {
        return entity2;
    }
}
