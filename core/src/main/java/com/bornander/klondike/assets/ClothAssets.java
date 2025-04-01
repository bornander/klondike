package com.bornander.klondike.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.ObjectMap;
import com.bornander.klondike.solitaire.ClothType;
import static com.bornander.klondike.Assets.loadTexture;

public class ClothAssets {
    private final ObjectMap<ClothType, TextureRegion> textures = new ObjectMap<>();

    public ClothAssets() {
        for(var type : ClothType.getTextures())
            textures.put(type, load(type));
    }

    private static TextureRegion load(ClothType type) {
        return loadTexture(String.format("graphics/cloths/%s.png", type.getAssetName()));
    }

    public TextureRegion getTexture(ClothType type) {
        return textures.get(type);
    }
}
