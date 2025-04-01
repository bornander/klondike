package com.bornander.klondike;

import com.badlogic.gdx.Game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Collections;
import com.bornander.klondike.screens.MenuScreen;


public class KlondikeGame extends Game {
    @Override
    public void create() {
        Collections.allocateIterators = false;
        Assets.instance.initialize(new AssetManager());
        Settings.instance.load();
        Statistics.instance.load();
        setScreen(new MenuScreen(this));
    }

    public void pause() {
        Statistics.instance.save();
    }

    public void resume() {
        Statistics.instance.load();
    }
}
