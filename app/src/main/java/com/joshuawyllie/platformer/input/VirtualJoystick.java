package com.joshuawyllie.platformer.input;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.joshuawyllie.platformer.R;
import com.joshuawyllie.platformer.util.Utils;

public class VirtualJoystick extends InputManager {

    private static final String TAG = "Virtual Joystick";
    private static final int MIN_HIT_TARGET = 48;

    private int maxDistance;

    public VirtualJoystick(View view) {
        view.findViewById(R.id.joystick_region)
                .setOnTouchListener(new JoystickTouchListener());
        view.findViewById(R.id.button_region)
                .setOnTouchListener(new ActionButtonTouchListener());
        maxDistance = Utils.dpToPx(MIN_HIT_TARGET * 2); //48dp = minimum hit target. maxDistance is in pixels.
        Log.d(TAG, "MaxDistance (pixels): " + maxDistance);
    }

    private class ActionButtonTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                isJumping = true;
            } else if (action == MotionEvent.ACTION_UP) {
                isJumping = false;
            }
            return true;
        }
    }

    private class JoystickTouchListener implements View.OnTouchListener {
        private float startingPositionX = 0;
        private float startingPositionY = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getActionMasked();
            if (action == MotionEvent.ACTION_DOWN) {
                startingPositionX = event.getX(0);
                startingPositionY = event.getY(0);
            } else if (action == MotionEvent.ACTION_UP) {
                horizontalFactor = 0.0f;
                verticalFactor = 0.0f;
            } else if (action == MotionEvent.ACTION_MOVE) {
                //get the proportion to the maxDistance
                horizontalFactor = (event.getX(0) - startingPositionX) / maxDistance;
                horizontalFactor = Utils.clamp(horizontalFactor, -1.0f, 1.0f);

                verticalFactor = (event.getY(0) - startingPositionY) / maxDistance;
                verticalFactor = Utils.clamp(verticalFactor, -1.0f, 1.0f);
            }
            return true;
        }
    }
}
