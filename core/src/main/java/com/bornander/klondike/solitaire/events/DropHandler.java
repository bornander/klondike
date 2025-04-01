package com.bornander.klondike.solitaire.events;

import com.badlogic.gdx.utils.Array;
import com.bornander.klondike.solitaire.Card;
import com.bornander.klondike.solitaire.CardStack;
import com.bornander.klondike.solitaire.DropAnimator;
import com.bornander.klondike.solitaire.TableTop;

public interface DropHandler {
    boolean handleDrop(TableTop tableTop, DropAnimator dropAnimator, CardStack source, CardStack target, Array<Card> cards);
}
