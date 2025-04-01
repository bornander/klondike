package com.bornander.klondike.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.bornander.klondike.Settings;

public class SoundAssets implements Disposable {
    private Sound button;
    private Sound shuffle;
    private Sound[] slides;


    public SoundAssets() {
        button = Gdx.audio.newSound(Gdx.files.internal("sounds/button.ogg"));
        shuffle = Gdx.audio.newSound(Gdx.files.internal("sounds/shuffle.ogg"));
        slides = new Sound[] {
            Gdx.audio.newSound(Gdx.files.internal("sounds/slide_1.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/slide_2.ogg")),
            Gdx.audio.newSound(Gdx.files.internal("sounds/slide_3.ogg"))
        };
    }

    private void play(Sound sound, float volume) {
        if (Settings.instance.sound.enabled && sound != null) {
            var id = sound.play();
            sound.setVolume(id, volume);
        }
    }

    private void play(Sound sound) {
        play(sound, 1.0f);
    }

    public void playButton() {
        play(button, 0.2f);
    }

    public void playShuffle() {
        play(shuffle);
    }

    public void playSlide() {
        play(slides[MathUtils.random(0, slides.length - 1)]);
    }

    private Sound dispose(Sound sound) {
        if (sound != null)
            sound.dispose();

        return null;
    }

    @Override
    public void dispose() {
        button = dispose(button);
        shuffle = dispose(shuffle);
        for(var sound : slides)
            dispose(sound);
    }
}
