package com.bornander.klondike.solitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.Assets;

public class CompletedEffect {
    private static class Star
    {
        public final TextureRegion texture;
        public final Vector2 position = new Vector2();
        public final Vector2 velocity = new Vector2();
        public final float size = MathUtils.random(1f, 2f);
        public final float rotationalSpeed = MathUtils.randomBoolean() ? MathUtils.random(-180, -90) : MathUtils.random(90, 180);
        public float rotation = 0.0f;

        public Star(TextureRegion texture) {
            this.texture = texture;
            position.y = -texture.getRegionHeight();
        }
    }

    private final int count = 100;
    private final Array<Star> stars = new Array<>();

    private int width;
    private int height;
    private float sizeFactor = 1.0f;

    public CompletedEffect() {
        for(var i = 0; i < count; ++i) {
            stars.add(new Star(Assets.instance.effects.getRandomStar()));
        }

        stars.sort((a, b) -> {
            if (a.size < b.size) return -1;
            if (a.size > b.size) return 1;
            return 0;
        });
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void reset(boolean inView ) {
        for(var star : stars) {
            star.position.set(MathUtils.random(0, width), MathUtils.random(height, height * 2));
            star.velocity.x = MathUtils.random(-width*0.1f, width*0.1f);
            star.velocity.y = 0;
        }
    }

    public void update(float delta) {
        var gravity = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * 0.2f;
        for(var star : stars) {
            star.position.x += star.velocity.x * delta;
            star.position.y += star.velocity.y * delta;
            star.velocity.y -= gravity * delta * star.size * star.size;
            star.rotation += star.rotationalSpeed * delta;
        }
    }

    public void render(SpriteBatch spriteBatch) {
        var size = Gdx.graphics.getWidth() * 0.05f;
        for(var star : stars) {
            spriteBatch.draw(
                star.texture,
                star.position.x,
                star.position.y,
                size * star.size * 0.5f,
                size * star.size * 0.5f,
                size * star.size, size * star.size,
                1.0f, 1.0f,
                star.rotation);
        }
    }
}
