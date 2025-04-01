package com.bornander.klondike.solitaire;

public enum BackColor {
    RED,
    GREEN,
    BLUE;

    public String getAssetName() {
        return this.toString().toLowerCase();
    }
}
