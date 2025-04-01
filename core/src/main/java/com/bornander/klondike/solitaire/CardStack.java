package com.bornander.klondike.solitaire;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.Assets;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.solitaire.events.CanDragHandler;
import com.bornander.klondike.solitaire.events.DoubleTapHandler;
import com.bornander.klondike.solitaire.events.DropHandler;
import com.bornander.klondike.solitaire.events.TapHandler;

public class CardStack {
    private final TableTop tableTop;
    public final Vector2 offset = new Vector2();
    private final Vector2 maxSize = new Vector2();
    private final int gridX;
    private final int gridY;
    private final Vector2 positionVector = new Vector2();
    public final Array<Card> cards = new Array<>();

    public StackIndicator indicator;
    public boolean acceptsDrops = true;
    public CanDragHandler canDragHandler;
    public TapHandler tapHandler;
    public DoubleTapHandler doubleTapHandler;
    public DropHandler dropHandler;

    public CardStack(TableTop tableTop, StackIndicator indicator, int gridX, int gridY, float offsetX, float offsetY, Vector2 maxSize) {
        this.tableTop = tableTop;
        this.indicator = indicator;
        this.gridX = gridX;
        this.gridY = gridY;
        offset.set(offsetX, offsetY);
        this.maxSize.set(maxSize);
    }

    public CardStack(TableTop tableTop, StackIndicator indicator, int gridX, int gridY, float offsetX, float offsetY) {
        this(tableTop, indicator, gridX, gridY, offsetX, offsetY, new Vector2(Float.MAX_VALUE, Float.MAX_VALUE));
    }

    public boolean any() {
        return !cards.isEmpty();
    }

    public void debugRender(ShapeRenderer sr) {
        sr.setColor(Color.RED);
    }

    public void add(Card card, boolean faceUp) {
        card.isFaceUp = faceUp;
        cards.add(card);
    }

    public void add(Array<Card> cardsToAdd, boolean faceUp) {
        for (var card : cardsToAdd)
            add(card, faceUp);
    }

    public void update() {
        for (var i = 0; i < cards.size; ++i) {
            var position = getPosition(tableTop, i);
            cards.get(i).setBounds(position.x, position.y, tableTop.cardSize.x, tableTop.cardSize.y);
        }
    }

    public void render(SpriteBatch spriteBatch) {
        var indicatorPosition = getPosition(tableTop, 0);

        var indicatorTexture = Assets.instance.cards.getIndicator(indicator);
        spriteBatch.draw(indicatorTexture, indicatorPosition.x, indicatorPosition.y, tableTop.cardSize.x, tableTop.cardSize.y);
        for(var card : cards)
            card.render(spriteBatch);
    }

    public boolean grab(int x, int y, Grabbed grabbed) {
        for (var i = cards.size - 1; i >= 0; --i) {
            var card = cards.get(i);
            if (card.containsPoint(x, y)) {
                if (canDragHandler != null && canDragHandler.canDrag(this, card)) {
                    Log.info("grab(%d, %d): %s", x, y, card);
                    grabbed.grab(this, i, cards.size - 1, x - card.bounds.x, y - card.bounds.y);
                    return true;
                }
            }
        }
        return false;
    }

    public Vector2 getPosition(TableTop tableTop, int index) {
        var bounds = tableTop.getCell(gridX, gridY);

        var extentX = tableTop.cardSize.x + (float)(cards.size - 1) * offset.x * tableTop.cardSize.y;
        var extentY = tableTop.cardSize.y + Math.abs((float)(cards.size - 1) * offset.y * tableTop.cardSize.y);

        var targetOffsetX = offset.x; // extentX < maxSize.x * tableTop.cardSize.x ? offset.x : (maxSize.x - tableTop.cardSize.x) / cards.size;
        var targetOffsetY = offset.y; // extentY < maxSize.y * tableTop.cardSize.y ? offset.y : (maxSize.y - tableTop.cardSize.y) / cards.size;

        return positionVector.set(
            bounds.x + index * targetOffsetX * tableTop.cardSize.x,
            bounds.y + index * targetOffsetY * tableTop.cardSize.y
        );
    }

    public Vector2 getTargetPosition(TableTop tableTop, int index) {
        return getPosition(tableTop, cards.size + index);
    }

    public boolean containsPoint(Vector2 position) {
        var anyCardContains = false;
        for (var card : cards) {
            anyCardContains |= card.containsPoint(position.x, position.y);
        }
        return tableTop.getCell(gridX, gridY).contains(position) | anyCardContains;
    }

    public boolean tap(TableTop tableTop, DropAnimator dropAnimator) {
        Log.info("Tap on stack");
        return tapHandler != null && tapHandler.handleTap(this, tableTop, dropAnimator);
    }

    public boolean doubleTap(TableTop tableTop, DropAnimator dropAnimator) {
        Log.info("Double-tap on stack");
        return doubleTapHandler != null && doubleTapHandler.handleDoubleTap(this, tableTop, dropAnimator);
    }

    public Array<Card> draw(int count) {
        var drawn = new Array<Card>();
        for(var i = 0; i < count; ++i) {
            if (!cards.isEmpty())
                drawn.add(pop());
        }
        return drawn;
    }

    public Card pop() {
        return cards.pop();
    }

    public Card top() {
        return cards.get(cards.size - 1);
    }

    public boolean handleDrop(TableTop tableTop, DropAnimator dropAnimator, CardStack source, Array<Card> cards) {
        Log.info("Drop on stack");
        return dropHandler != null && dropHandler.handleDrop(tableTop, dropAnimator, source, this, cards);
    }

    public boolean anyOfSuit(Suit suit) {
        for(var card : cards) {
            if (card.suit == suit)
                return true;
        }
        return false;
    }

}
