package com.bornander.klondike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.solitaire.Card;
import com.bornander.klondike.solitaire.ClothType;
import com.bornander.klondike.solitaire.Rank;
import com.bornander.klondike.solitaire.Suit;

import java.util.Comparator;

public class MenuBackground {
    private static class FallingCard {
        public TextureRegion texture;
        public final Vector2 position = new Vector2();
        public float rotation;
        public float rotationalVelocity;
        public float scale = 1.0f;
        public final boolean isFaceUp;

        public FallingCard(TextureRegion texture, boolean isFaceUp) {
            this.texture = texture;
            this.isFaceUp = isFaceUp;
        }

        public void reset() {
            scale = MathUtils.random(0.5f, 1.0f);
            rotationalVelocity = MathUtils.randomBoolean() ? MathUtils.random(-120, -90) : MathUtils.random(90, 120);
            if (!isFaceUp)
                texture = Assets.instance.cards.getBack(Settings.instance.visual.backColor, Settings.instance.visual.backStyle);

            position.set(
                MathUtils.random(-texture.getRegionWidth(), Gdx.graphics.getWidth()+texture.getRegionWidth()),
                MathUtils.random(Gdx.graphics.getHeight(), Gdx.graphics.getHeight() * 2)
            );
        }

        public void update(float delta) {
            var speed = Gdx.graphics.getHeight() * 0.5f;
            rotation += rotationalVelocity *  delta;
            position.y -= speed * delta * scale * scale;
            if (position.y < -texture.getRegionHeight()*2)
                reset();
        }

        public void render(SpriteBatch spriteBatch) {
            var aspectRatio = texture.getRegionWidth() / (float)texture.getRegionHeight();
            var w = Gdx.graphics.getWidth() * 0.1f * scale;
            var h = (w / aspectRatio);
            spriteBatch.draw(
                texture,
                position.x, position.y,
                w / 2.0f, h / 2.0f,
                w, h,
                1.0f, 1.0f,
                rotation);
        }
    }

    private final Comparator<FallingCard> RenderOrderComparator = new Comparator<FallingCard>() {
        @Override
        public int compare(FallingCard a, FallingCard b) {
            if (a.scale < b.scale)
                return -1;
            if (a.scale > b.scale)
                return 1;

            return 0;
        }
    };

    private final Array<FallingCard> fallingCards = new Array<>();

    public MenuBackground() {
        for(var suit : Suit.values()) {
            for(var rank : Rank.values()) {
                var fc = new FallingCard(Assets.instance.cards.getFace(suit, rank), true);
                fc.reset();
                fallingCards.add(fc);
            }
        }
        for(var i = 0; i < 52; ++i) {
            var fc = new FallingCard(null, false);
            fc.reset();
            fallingCards.add(fc);
        }
    }

    public void update(float delta) {
        fallingCards.sort(RenderOrderComparator);
        for(var fc : fallingCards)
            fc.update(delta);
    }

    private void renderBackground(SpriteBatch spriteBatch) {
        var texture = Assets.instance.cloths.getTexture(Settings.instance.visual.clothType);
        spriteBatch.setColor(Settings.instance.visual.clothTextureBrightness);
        for(var x = 0; x < Gdx.graphics.getWidth(); x += texture.getRegionWidth()) {
            for(var y = 0; y < Gdx.graphics.getHeight(); y += texture.getRegionHeight()) {
                spriteBatch.draw(texture, x, y);
            }
        }
        spriteBatch.setColor(Color.WHITE);
    }

    public void render(SpriteBatch spriteBatch) {
        renderBackground(spriteBatch);
        for(var fc : fallingCards) {
            spriteBatch.setColor(fc.scale, fc.scale, fc.scale, 1.0f);
            fc.render(spriteBatch);
            spriteBatch.setColor(Color.WHITE);
        }
    }
}
