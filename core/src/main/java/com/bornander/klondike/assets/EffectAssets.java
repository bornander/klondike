package com.bornander.klondike.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import static com.bornander.klondike.Assets.loadTexture;

public class EffectAssets {
    private final ObjectMap<String, TextureRegion> stars = new ObjectMap<>();

    public EffectAssets() {
        stars.put("blue", loadStar("blue"));
        stars.put("green", loadStar("green"));
        stars.put("red", loadStar("red"));
        stars.put("white", loadStar("white"));
        stars.put("yellow", loadStar("yellow"));
    }

    private TextureRegion loadStar(String color) {
        return loadTexture(String.format("graphics/effects/stars/%s.png", color));
    }

    public TextureRegion getRandomStar() {
        var keys = stars.keys().toArray();
        return stars.get(keys.random());
    }
}
