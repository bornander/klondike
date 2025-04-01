package com.bornander.klondike;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.bornander.klondike.assets.*;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.solitaire.CardStack;

public class Assets implements Disposable {

    public static Assets instance = new Assets();

    private AssetManager assetManager;

    public CardAssets cards;
    public ClothAssets cloths;
    public HudAssets hud;
    public FontAssets fonts;
    public EffectAssets effects;
    public MenuAssets menu;
    public SoundAssets sounds;
    public CursorAssets cursors;

    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
        fonts = new FontAssets();
        cards = new CardAssets();
        cloths = new ClothAssets();
        effects = new EffectAssets();
        menu = new MenuAssets();
        sounds = new SoundAssets();
        cursors = new CursorAssets();
    }

    @Override
    public void dispose() {
        fonts.dispose();
        sounds.dispose();
        assetManager.dispose();
    }

    public void resize(int width, int height) {
        fonts.update(width, height);
        hud = new HudAssets();
        menu = new MenuAssets();

    }

    public static TextureRegion loadTexture(String asset) {
        Log.info("Loading asset: %s", asset);
        Assets.instance.assetManager.load(asset, Texture.class);
        Assets.instance.assetManager.finishLoading();
        var texture = Assets.instance.assetManager.get(asset, Texture.class);
        texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        return new TextureRegion(texture);
    }

    public static Pixmap loadPixmap(String asset) {
        Log.info("Loading asset: %s", asset);
        Assets.instance.assetManager.load(asset, Pixmap.class);
        Assets.instance.assetManager.finishLoading();
        return Assets.instance.assetManager.get(asset, Pixmap.class);
    }

    public static <T> T safeDispose(T object) {
        if (object != null)
            ((Disposable)object).dispose();
        return null;
    }
}
