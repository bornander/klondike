package com.bornander.klondike.solitaire;

public interface GameRules {
    GameStats getGameStats();
    TableTop create();

    void update(float delta);

    boolean hasCompleted();

    void restart();

    boolean canAutoComplete();


    void forceComplete();
}
