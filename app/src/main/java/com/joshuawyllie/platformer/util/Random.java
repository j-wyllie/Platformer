package com.joshuawyllie.platformer.util;

public abstract class Random {
    private final static java.util.Random RNG = new java.util.Random();

    public static boolean coinFlip() {
        return RNG.nextFloat() > 0.5;
    }

    public static float nextFloat() {
        return RNG.nextFloat();
    }

    public static int nextInt(final int max) {
        return RNG.nextInt(max);
    }

    public static int between(final int min, final int max) {
        return RNG.nextInt(max - min) + min;
    }

    public static float between(final float min, final float max) {
        return min + RNG.nextFloat() * (max - min);
    }
}
