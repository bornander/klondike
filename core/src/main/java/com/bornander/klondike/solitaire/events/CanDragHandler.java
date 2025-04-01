package com.bornander.klondike.solitaire.events;

import com.bornander.klondike.solitaire.Card;
import com.bornander.klondike.solitaire.CardStack;

public interface CanDragHandler {
    boolean canDrag(CardStack stack, Card card);
}
