package com.bornander.klondike.solitaire;

import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.Assets;

public class Deck {

    public final Array<Card> cards = new Array<>();

    public Deck() {
        for(var suit : Suit.values()) {
            for(var rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public void shuffle() {
        Assets.instance.sounds.playShuffle();
        cards.shuffle();
    }

    public Array<Card> draw(int count) {
        var drawnCards = new Array<Card>();
        for(var i = 0; i < count; ++i) {
            drawnCards.add(cards.get(i));
        }
        cards.removeAll(drawnCards, true);
        return drawnCards;
    }
}
