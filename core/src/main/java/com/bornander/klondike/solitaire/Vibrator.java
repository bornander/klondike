package com.bornander.klondike.solitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.bornander.klondike.Settings;

public class Vibrator {
    public static void button() {
        if (Settings.instance.input.tactileFeedback)
            Gdx.input.vibrate(128);
    }

    public static void card() {
        if (Settings.instance.input.tactileFeedback)
            Gdx.input.vibrate(50 + MathUtils.random(0, 25));
    }
}
