package com.bornander.klondike.solitaire;

public class SoundSettings {
    public boolean enabled;

    public void toggle() {
        enabled = !enabled;
    }

    public String getState() {
        return enabled ? "ON" : "OFF";
    }
}
