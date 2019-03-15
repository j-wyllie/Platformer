package com.joshuawyllie.platformer.display;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.joshuawyllie.platformer.R;

public class SettingsMenu implements View.OnTouchListener {
    private boolean isOpen = false;

    public SettingsMenu(View view) {
        view.findViewById(R.id.settings_button).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int id = v.getId();
        if (action == MotionEvent.ACTION_UP) {
            if (id == R.id.settings_button) {
                isOpen = !isOpen;
                Log.d("setting button", String.valueOf(isOpen));
            }
        }
        return false;
    }
}
