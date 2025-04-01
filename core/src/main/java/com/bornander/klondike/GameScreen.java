package com.bornander.klondike;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.bornander.klondike.libgdx.Log;

public abstract class GameScreen implements Screen {
    public final KlondikeGame game;
    public static long frameCount = 0;

    public GameScreen(KlondikeGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        Log.debug("Showing screen %s", this);
    }

    public abstract void update(float delta);
    public abstract void render();

    @Override
    public void render(float delta) {
        ++frameCount;
        update(delta);
        render();
    }

    @Override
    public void resize(int width, int height) {
        Log.debug("Resizing screen %s to (%d, %d)", this, width, height);
        Assets.instance.resize(width, height);

    }

    @Override
    public void pause() {
        Log.debug("Pause screen %s", this);
    }

    @Override
    public void resume() {
        Log.debug("Resume screen %s", this);
    }

    @Override
    public void hide() {
        Log.debug("Hide screen %s", this);
    }

    @Override
    public void dispose() {
        Log.debug("Dispose screen %s", this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
