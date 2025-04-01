package com.bornander.klondike.solitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.solitaire.events.PostDropHandler;

public class DropAnimator {

    private final Array<Card> cards = new Array<>();
    private final ObjectMap<Card, Vector2> currentPositions = new ObjectMap<>();
    private final ObjectMap<Card, Vector2> sourcePositions = new ObjectMap<>();
    private final ObjectMap<Card, Vector2> targetPositions = new ObjectMap<>();
    private CardStack source;
    private CardStack target;

    private float elapsed;

    public PostDropHandler postDropHandler;

    public void dropOntoStack(TableTop tableTop, CardStack source, CardStack target, Array<Card> cardsToRestore) {
        Log.info("dropOntoStack %s", cardsToRestore.get(0));
        elapsed = 0.0f;
        this.source = source;
        this.target = target;
        cards.addAll(cardsToRestore);
        for(var i = 0; i < cards.size; ++i) {
            var card = cards.get(i);
            currentPositions.put(card, new Vector2(card.bounds.x, card.bounds.y));
            sourcePositions.put(card, new Vector2(card.bounds.x, card.bounds.y));

            var targetPosition = target.getTargetPosition(tableTop, i);
            targetPositions.put(card, targetPosition);
        }
    }

    public void dropOntoStack(TableTop tableTop, CardStack source, CardStack target, Card card) {
        dropOntoStack(tableTop, source, target, new Array<>(new Card[] { card }));
    }

    public void update(float delta) {
        if (cards.isEmpty())
            return;
        elapsed += delta;

        var st = sourcePositions.get(cards.get(0));
        var tt = targetPositions.get(cards.get(0));
        var distance = Vector2.dst(st.x, st.y, tt.x, tt.y);
        var maxDistance = Vector2.dst(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        var distanceFactor = distance / maxDistance;
        var alpha = Math.min(1.0f, elapsed / (1.0f * distanceFactor));
        for(var card : cards) {
            var c = currentPositions.get(card);
            var s = sourcePositions.get(card);
            var t = targetPositions.get(card);
            c.set(s);
            c.interpolate(t, alpha, Interpolation.sineOut);
            card.setPosition(c.x, c.y);
        }

        if (alpha >= 1.0f) {
            var droppedCards = new Array<>(cards);
            cards.clear();
            target.cards.addAll(droppedCards);
            if (postDropHandler != null) {
                var handler = postDropHandler;
                postDropHandler = null;
                handler.handleDropCompleted(source, target, droppedCards);

            }
        }
    }

    public void render(SpriteBatch spriteBatch) {
        for(var card : cards)
            card.render(spriteBatch);
    }

    public boolean isActive() {
        return !cards.isEmpty();
    }
}
