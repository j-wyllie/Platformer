package com.joshuawyllie.platformer.input;

public class InputManager {
    public float verticalFactor = 0.0f;
    public float horizontalFactor = 0.0f;
    public boolean isJumping = false;

    public enum Type {
        JOYSTICK,
        TOUCH,
        ACCELEROMETER
    }

    public void update(float dt) {}
    public void onStart() {}
    public void onStop() {}
    public void onPause() {}
    public void onResume() {}
}
