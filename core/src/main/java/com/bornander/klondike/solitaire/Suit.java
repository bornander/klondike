package com.bornander.klondike.solitaire;

import com.badlogic.gdx.utils.GdxRuntimeException;

public enum Suit {
    HEARTS,
    SPADES,
    DIAMONDS,
    CLUBS;

    public String getAssetName() {
        switch (this) {
            case HEARTS: return "Hearts";
            case SPADES: return "Spades";
            case DIAMONDS: return "Diamonds";
            case CLUBS: return "Clubs";
            default:
                throw new GdxRuntimeException(String.format("Bad case: %s", this));
        }
    }

    public SuitColor getColor() {
        return this == HEARTS || this == DIAMONDS ? SuitColor.RED : SuitColor.BLACK;
    }

    public enum SuitColor {
        RED, BLACK
    }
}
