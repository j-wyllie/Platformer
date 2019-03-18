package com.joshuawyllie.platformer;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class Jukebox {
    private static final String TAG = "Jukebox";
    private static final int MAX_STREAMS = 3;
    private static final float DEFAULT_SFX_VOLUME = 0.5f;
    private static final float DEFAULT_MUSIC_VOLUME = 0.5f;
    private static final String SOUNDS_PREF_KEY = "sounds_pref_key";
    private static final String MUSIC_PREF_KEY = "music_pref_key";

    private boolean mSoundEnabled;
    private boolean mMusicEnabled;
    private Context context;
    private HashMap<GameEvent.Type, Integer> soundsMap;
    private SoundPool soundPool = null;
    private MediaPlayer mBgPlayer = null;

    public Jukebox(Context context) {
        this.context = context;
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(context);
        mSoundEnabled = prefs.getBoolean(SOUNDS_PREF_KEY, true);
        mMusicEnabled = prefs.getBoolean(MUSIC_PREF_KEY, true);
        loadIfNeeded();
    }

    private void loadIfNeeded() {
        if (mSoundEnabled) {
            loadSounds();
        }
        if (mMusicEnabled) {
            loadMusic();
        }
    }

    private void loadSounds() {
        createSoundPool();
        soundsMap = new HashMap<>();
        loadEventSound(GameEvent.Type.DEATH, context.getString(R.string.death_sound));
        loadEventSound(GameEvent.Type.COIN_COLLISON, context.getString(R.string.coin_sound));
        loadEventSound(GameEvent.Type.DAMAGE, context.getString(R.string.hit));
        loadEventSound(GameEvent.Type.JUMP, context.getString(R.string.jump_sound));
    }

    public void playSoundForGameEvent(GameEvent event) {
        if (!mSoundEnabled) {
            return;
        }
        final float leftVolume = DEFAULT_SFX_VOLUME;
        final float rightVolume = DEFAULT_SFX_VOLUME;
        final int priority = 1;
        final int loop = 0; //-1 loop forever, 0 play once
        final float rate = 1.0f;
        final Integer soundID = soundsMap.get(event.getType());
        if (soundID != null) {
            soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
        }
    }

    @SuppressWarnings("deprecation")
    private void createSoundPool() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        } else {
            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(MAX_STREAMS)
                    .build();
        }
    }

    private void loadEventSound(final GameEvent.Type event, final String fileName) {
        try {
            AssetFileDescriptor afd = context.getAssets().openFd(fileName);
            int soundId = soundPool.load(afd, 1);
            soundsMap.put(event, soundId);
        } catch (IOException e) {
            Log.e(TAG, "loadEventSound: error loading sound " + e.toString());
        }
    }

    private void loadMusic() {
        try {
            mBgPlayer = new MediaPlayer();
            AssetFileDescriptor afd = context
                    .getAssets().openFd(context.getString(R.string.music_file));
            mBgPlayer.setDataSource(
                    afd.getFileDescriptor(),
                    afd.getStartOffset(),
                    afd.getLength());
            mBgPlayer.setLooping(true);
            mBgPlayer.setVolume(DEFAULT_MUSIC_VOLUME, DEFAULT_MUSIC_VOLUME);
            mBgPlayer.prepare();
        } catch (IOException e) {
            mBgPlayer = null;
            mMusicEnabled = false;
            Log.e(TAG, e.getMessage());
        }
    }

    public void toggleSoundStatus() {
        mSoundEnabled = !mSoundEnabled;
        if (mSoundEnabled) {
            loadSounds();
        } else {
            unloadSounds();
        }
        PreferenceManager
                .getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(SOUNDS_PREF_KEY, mSoundEnabled)
                .putBoolean(MUSIC_PREF_KEY, mMusicEnabled)
                .commit();
    }

    private void unloadMusic() {
        if (mBgPlayer != null) {
            mBgPlayer.stop();
            mBgPlayer.release();
        }
    }

    public void pauseBgMusic() {
        if (mMusicEnabled) {
            mBgPlayer.pause();
        }
    }

    public void resumeBgMusic() {
        if (mMusicEnabled) {
            mBgPlayer.start();
        }
    }

    private void unloadSounds() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            soundsMap.clear();
        }
    }
}
