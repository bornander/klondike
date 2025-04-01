package com.bornander.klondike.solitaire.events;

import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.solitaire.Card;
import com.bornander.klondike.solitaire.CardStack;

public interface PostDropHandler {
    void handleDropCompleted(CardStack source, CardStack target, Array<Card> droppedCards);
}
