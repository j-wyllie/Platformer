package com.joshuawyllie.platformer;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.joshuawyllie.platformer.display.SettingsMenu;
import com.joshuawyllie.platformer.input.Accelerometer;
import com.joshuawyllie.platformer.input.InputManager;
import com.joshuawyllie.platformer.input.TouchController;
import com.joshuawyllie.platformer.input.VirtualJoystick;

public class MainActivity extends AppCompatActivity {

    public static final InputManager.Type DEFAULT_INPUT_METHOD = InputManager.Type.JOYSTICK;
    private Game _game;
    private ViewGroup currentControlLayout;
    private FrameLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activityMain = findViewById(R.id.activity_main);
        _game = findViewById(R.id.game);
        setFullScreen();
        setupControls(DEFAULT_INPUT_METHOD);
        setupSettingMenu();
    }

    private void setupSettingMenu() {
        SettingsMenu settingsMenu = new SettingsMenu(this, activityMain, findViewById(R.id.settings_button));
        _game.getHud().setSettingsMenu(settingsMenu);
    }

    public void setupControls(InputManager.Type controlType) {
        if (currentControlLayout != null) {
            activityMain.removeView(currentControlLayout);
            currentControlLayout = null;
        }
        InputManager controls = null;
        //_game.setUsingAccelerometer(false);
        switch (controlType) {
            case JOYSTICK:
                ViewGroup joystickLayout = (LinearLayout) View.inflate(this, R.layout.virtual_joystick, null);
                activityMain.addView(joystickLayout);
                currentControlLayout = joystickLayout;
                controls = new VirtualJoystick(findViewById(R.id.virtual_joystick));
                break;
            case TOUCH:
                RelativeLayout touchLayout = (RelativeLayout) View.inflate(this, R.layout.touch_controls, null);
                touchLayout.setGravity(Gravity.BOTTOM);
                activityMain.addView(touchLayout);
                currentControlLayout = touchLayout;
                controls = new TouchController(findViewById(R.id.touch_controls));
                break;
            case ACCELEROMETER:
              //  _game.setUsingAccelerometer(true);
                //   _game.setAccelerometer(accelerometer);
                controls = new Accelerometer(this);
                break;
        }
        _game.setControls(controls);
    }

    @Override
    protected void onResume() {
        super.onResume();
        _game.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _game.onPause();
    }

    @Override
    protected void onDestroy() {
        _game.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            return;
        }
        setFullScreen();
    }

    private void setFullScreen() {
        View decorView = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LOW_PROFILE
            );
        } else {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

}
