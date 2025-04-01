 package com.bornander.klondike.solitaire;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.Assets;

 public class Grabbed {
    public final Array<Card> cards = new Array<>();
    private final Vector2 position = new Vector2();
    private final Vector2 grabOffset = new Vector2();
    private final Vector2 offset = new Vector2();

    public CardStack sourceStack;

    public void grab(CardStack sourceStack, int start, int end, float offsetX, float offsetY) {
        this.sourceStack = sourceStack;
        this.offset.set(sourceStack.offset);
        grabOffset.set(offsetX, offsetY);
        for(var i = start; i < end + 1; ++i) {
            cards.add(sourceStack.cards.get(i));
        }
        for(var card : cards) {
            sourceStack.cards.removeValue(card, true);
        }
    }

    public void update(TableTop tableTop) {
        var x = position.x - grabOffset.x;
        var y = position.y - grabOffset.y;
        for(var card : cards) {
            x += offset.x * tableTop.cardSize.x;
            y += offset.y * tableTop.cardSize.y;
            card.setPosition(x, y);
        }
    }

    public void render(SpriteBatch spriteBatch) {
        if (cards.isEmpty())
            return;
        var shadowBounds = new Rectangle(cards.get(0).bounds);
        for(var card : cards)
            shadowBounds.merge(card.bounds);

        var shadowSize = 0.15f;
        spriteBatch.draw(Assets.instance.cards.dropShadow, shadowBounds.x + shadowBounds.width * shadowSize , shadowBounds.y - shadowBounds.height * shadowSize, shadowBounds.width, shadowBounds.height);

        for(var card : cards)
            card.render(spriteBatch, 0.1f);
    }

    public void update(Vector2 touchPosition) {
        position.set(touchPosition);
    }

    public boolean hasAny() {
        return cards.size > 0;
    }

    public Array<Card> release() {
        var toRelease = new Array<>(cards);
        cards.clear();
        return toRelease;
    }
}
