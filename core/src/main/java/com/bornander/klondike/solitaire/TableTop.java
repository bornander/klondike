package com.bornander.klondike.solitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bornander.klondike.Assets;
import com.bornander.klondike.Settings;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.libgdx.RectangleExtensions;

public class TableTop {

    public static class LayoutParameters {
        public final int align;
        public final int horizontalStacks;
        public final int verticalStacks;
        public final float horizontalPadding;
        public final float verticalPadding;

        public LayoutParameters(int align, int horizontalStacks, int verticalStacks, float horizontalPadding, float verticalPadding) {
            this.align = align;
            this.horizontalStacks = horizontalStacks;
            this.verticalStacks = verticalStacks;
            this.horizontalPadding = horizontalPadding;
            this.verticalPadding = verticalPadding;
        }
    }

    private final Vector2 calculatedCardSize = new Vector2();
    public final ObjectMap<String, CardStack> stacks = new ObjectMap<>();
    public final Rectangle[][] layoutBounds;
    public final LayoutParameters layout;
    public final Vector2 cardSize = new Vector2();

    public TableTop(LayoutParameters layout) {
        this.layout = layout;
        layoutBounds = new Rectangle[layout.horizontalStacks][layout.verticalStacks];
        for(var x = 0; x < layout.horizontalStacks; ++x) {
            for(var y = 0; y < layout.verticalStacks; ++y) {
                layoutBounds[x][y] = new Rectangle();
            }
        }
    }

    public CardStack createStack(String name, StackIndicator indicator, int gridX, int gridY, float offsetX, float offsetY, Vector2 maxSize) {
        var cardStack = new CardStack(this, indicator, gridX, gridY, offsetX, offsetY, maxSize);
        stacks.put(name, cardStack);
        return cardStack;
    }

    public CardStack createStack(String name, StackIndicator indicator, int gridX, int gridY, float offsetX, float offsetY) {
        return createStack(name, indicator, gridX, gridY, offsetX, offsetY, new Vector2(Float.MAX_VALUE, Float.MAX_VALUE));
    }

    public CardStack getStack(String name) {
        return stacks.get(name);
    }

    private Vector2 calculateCardSize(Rectangle bounds, float requiredCardWidths, float requiredCardHeights) {
        var preferredWidth = bounds.width / requiredCardWidths;
        var preferredHeight = bounds.height / requiredCardHeights;
        var inferredHeight = preferredWidth / Assets.instance.cards.aspectRatio;
        var inferredWidth = preferredHeight * Assets.instance.cards.aspectRatio;
        if (inferredHeight < preferredHeight)
            return calculatedCardSize.set(preferredWidth, inferredHeight);
        else
            return calculatedCardSize.set(inferredWidth, preferredHeight);
    }

    public void updateLayoutBounds(Rectangle bounds) {
        var requiredCardWidths = layout.horizontalStacks + layout.horizontalPadding;
        var requiredCardHeights = layout.verticalStacks + layout.verticalPadding;
        cardSize.set(calculateCardSize(bounds, requiredCardWidths, requiredCardHeights));

        var fittedBounds = new Rectangle(bounds.x, bounds.y, cardSize.x * requiredCardWidths, cardSize.y * requiredCardHeights);
        if (Align.isCenterHorizontal(layout.align))
            fittedBounds.x = bounds.x + (bounds.width - fittedBounds.width) / 2.0f;

        if (Align.isTop(layout.align))
            fittedBounds.y = bounds.y + bounds.height - fittedBounds.height;

        var horizontalStacks = 7;
        var verticalStacks = 4;
        var gridCellWidth = fittedBounds.width / horizontalStacks;
        var gridCellHeight = fittedBounds.height / verticalStacks;

        for(var x = 0; x < horizontalStacks; ++x) {
            for(var y = 0; y < verticalStacks; ++y) {
                var cell = new Rectangle(fittedBounds.x + x * gridCellWidth, fittedBounds.y + y * gridCellHeight, gridCellWidth, gridCellHeight);
                var cardBounds = layoutBounds[x][y];
                cardBounds.setSize(cardSize.x, cardSize.y);
                RectangleExtensions.centerIn(cardBounds, cell, 0);
            }
        }
    }

    public Rectangle getCell(int x, int y) {
        return layoutBounds[x][y];
    }

    public Rectangle getBounds() {
        var bounds = new Rectangle(layoutBounds[0][0]);
        var r = layoutBounds[layoutBounds.length - 1];
        var k = r[r.length - 1];
        bounds.merge(k);
        return bounds;
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


    public void update() {
        for(var stack : stacks.values())
            stack.update();
    }

    public void render(SpriteBatch spriteBatch) {
        renderBackground(spriteBatch);
        for(var stack : stacks.values()) {
            stack.render(spriteBatch);
        }
    }

    public void debugRender(ShapeRenderer sr) {
        sr.setColor(Color.MAGENTA);
        for(var x = 0; x < layout.horizontalStacks; ++x) {
            for(var y = 0; y < layout.verticalStacks; ++y) {
                var cardBounds = layoutBounds[x][y];
                sr.rect(cardBounds.x, cardBounds.y, cardBounds.width, cardBounds.height);
            }
        }

        for(var cardStack : stacks) {
            cardStack.value.debugRender(sr);
        }

        sr.setColor(Color.YELLOW);
        var b = getBounds();
        sr.rect(b.x, b.y, b.width, b.height);
    }

    public boolean grab(int x, int y, Grabbed grabbed) {
        for(var stack : stacks.values()) {
            if (stack.grab(x, y, grabbed)) {
                return true;
            }
        }
        return false;
    }

    public CardStack getStackAt(Vector2 position) {
        for(var cardStackEntry : stacks) {
            var cardStack = cardStackEntry.value;
            if (cardStack.containsPoint(position)) {
                Log.info("Dropping cards on %s", cardStackEntry.key);
                return cardStack;
            }
        }
        Log.info("No card stack");
        return null;
    }

    public Array<CardStack> getStacks(String namePrefix) {
        var matches = new Array<CardStack>();
        for(var entry : stacks) {
            if (entry.key.startsWith(namePrefix))
                matches.add(entry.value);
        }
        return matches;
    }

    public Array<Card> getAllCards() {
        var cards = new Array<Card>();
        for(var stack : stacks.values())
            cards.addAll(stack.cards);
        return cards;
    }
}
