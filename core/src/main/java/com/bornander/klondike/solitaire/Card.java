package com.bornander.klondike.solitaire;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.Assets;
import com.bornander.klondike.Settings;

public class Card {
    public final Suit suit;
    public final Rank rank;
    public boolean isFaceUp;

    public final Rectangle bounds = new Rectangle();
    public final TextureRegion face;
    public final TextureRegion back;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;

        face = Assets.instance.cards.getFace(suit, rank);
        back = Assets.instance.cards.getBack(Settings.instance.visual.backColor, Settings.instance.visual.backStyle);
    }

    public void setBounds(float x, float y, float width, float height) {
        this.bounds.set(x, y, width, height);
    }

    public void setPosition(float x, float y) {
        bounds.setPosition(x, y);
    }

    @Override
    public String toString() {
        return String.format("%s of %s", rank, suit);
    }

    public boolean containsPoint(float x, float y) {
        return bounds.contains(x, y);
    }

    public boolean isOtherColor(Card other) {
        return suit.getColor() != other.suit.getColor();
    }

    public boolean isOfSameSuitAndDistance(Card other, int distance) {
        if (other.suit != suit)
            return false;

        return distance == other.rank.getValue() - rank.getValue();
    }

    public boolean isOfOtherColorAndDistance(Card other, int distance) {
        if (suit.getColor() == other.suit.getColor())
            return false;

        return distance == other.rank.getValue() - rank.getValue();
    }


    public static boolean isAllOfSuit(Array<Card> cards, Suit suit) {
        for(var card : cards) {
            if (card.suit != suit)
                return false;
        }
        return true;
    }

    private TextureRegion getTexture() {
        return isFaceUp ? face : back;
    }

    public void render(SpriteBatch spriteBatch) {
        render(spriteBatch, 0.0f);
    }
    public void render(SpriteBatch spriteBatch, float sizeFactor) {
        var sfx = bounds.width * sizeFactor;
        var sfy = bounds.height * sizeFactor;
        spriteBatch.draw(getTexture(), bounds.x - sfx * 0.5f , bounds.y - sfy * 0.5f, bounds.width + sfx, bounds.height + sfy);
    }
}
