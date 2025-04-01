package com.bornander.klondike.solitaire.events;

import com.bornander.klondike.solitaire.CardStack;
import com.bornander.klondike.solitaire.DropAnimator;
import com.bornander.klondike.solitaire.TableTop;

public interface DoubleTapHandler {
    boolean handleDoubleTap(CardStack stack, TableTop tableTop, DropAnimator dropAnimator);
}
