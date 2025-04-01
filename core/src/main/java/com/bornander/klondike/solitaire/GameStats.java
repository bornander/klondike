package com.bornander.klondike.solitaire;

import com.bornander.klondike.Statistics;

public class GameStats {
    private int moves;

    public float elapsedTime;
    public int passesLeft;

    public void move() {
        ++moves;
        Statistics.instance.move();
    }

    public int getMoves() {
        return moves;
    }
}
