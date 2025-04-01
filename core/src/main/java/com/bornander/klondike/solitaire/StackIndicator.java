package com.bornander.klondike.solitaire;

public enum StackIndicator {
    NONE,
    BLOCKED,
    HEARTS,
    SPADES,
    DIAMONDS,
    CLUBS;

    public String getAssetName() {
        return this.toString().toLowerCase();
    }
}
