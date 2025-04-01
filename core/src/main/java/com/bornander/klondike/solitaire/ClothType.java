package com.bornander.klondike.solitaire;

public enum ClothType {
    BLUE,
    BROWN,
    GRAY,
    GREEN,
    PINK,
    PURPLE,
    YELLOW;

    public String getAssetName() {
        return toString().toLowerCase();
    }

    public static ClothType[] getTextures() {
        return new ClothType[] { BLUE, BROWN, GRAY, GREEN, PINK, PURPLE, YELLOW };
    }
}
