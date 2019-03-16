package com.joshuawyllie.platformer.display;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.joshuawyllie.platformer.MainActivity;
import com.joshuawyllie.platformer.R;
import com.joshuawyllie.platformer.input.InputManager;

public class SettingsMenu implements View.OnTouchListener {
    private InputManager.Type inputMethod = MainActivity.DEFAULT_INPUT_METHOD;
    View menuLayout;
    private MainActivity context;
    private ViewGroup mainActivity;
    private boolean isOpen = false;
    private RadioGroup radioGroup;

    public SettingsMenu(MainActivity context, ViewGroup mainActivity, View settingsButton) {
        this.context = context;
        this.mainActivity = mainActivity;
        settingsButton.setOnTouchListener(this);
        menuLayout = View.inflate(context, R.layout.menu, null);
        mainActivity.addView(menuLayout);
        menuLayout.setActivated(false);
        menuLayout.setVisibility(View.INVISIBLE);
        radioGroup = menuLayout.findViewById(R.id.control_type);
        radioGroup.setOnCheckedChangeListener(new InputMethodListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int id = v.getId();
        if (action == MotionEvent.ACTION_UP) {
            if (id == R.id.settings_button) {
                isOpen = !isOpen;
                if (isOpen) {
                    handleMenu();
                    menuLayout.setActivated(true);
                    menuLayout.bringToFront();
                    menuLayout.setVisibility(View.VISIBLE);
                } else {
                    menuLayout.setActivated(false);
                    menuLayout.setVisibility(View.INVISIBLE);
                }
                Log.d("setting button", String.valueOf(isOpen));
            }
        }

        return false;
    }

    private void handleMenu() {
        int checked = R.id.virtual_joystick_radio;
        switch (inputMethod) {
            case JOYSTICK:
                checked = R.id.virtual_joystick_radio;
                break;
            case TOUCH:
                checked = R.id.touch_controls_radio;
                break;
            case ACCELEROMETER:
                checked = R.id.accelerometer_radio;
                break;
        }
        radioGroup.check(checked);
    }

    public void setInputMethod(InputManager.Type inputMethod) {
        this.inputMethod = inputMethod;
        context.setupControls(inputMethod);
        menuLayout.bringToFront();
    }

    private class InputMethodListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            InputManager.Type inputType = null;
            switch (checkedId) {
                case R.id.virtual_joystick_radio:
                    inputType = InputManager.Type.JOYSTICK;
                    break;
                case R.id.touch_controls_radio:
                    inputType = InputManager.Type.TOUCH;
                    break;
                case R.id.accelerometer_radio:
                    inputType = InputManager.Type.ACCELEROMETER;
                    break;
            }
            setInputMethod(inputType);
        }
    }
}