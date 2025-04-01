package com.bornander.klondike;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.bornander.klondike.solitaire.*;
import com.bornander.klondike.solitaire.events.*;

import java.util.Comparator;


public class KlondikeRules implements GameRules {

    private GameStats gameStats;
    private final int drawCount;
    private final int passes;
    private TableTop tableTop;

    private final AutoCompletable autoCompletable = new AutoCompletable();

    public KlondikeRules(int drawCount, int passes) {
        this.drawCount = drawCount;
        this.passes = passes;
        restart();
    }

    @Override
    public GameStats getGameStats() {
        return gameStats;
    }

    @Override
    public TableTop create() {
        var deck = new Deck();
        deck.shuffle();
        tableTop = new TableTop(new TableTop.LayoutParameters(Align.top, 7, 4, 0.5f, 0.25f));
        var stockPile = setupStock(tableTop);
        var discardPile = setupDiscard(tableTop);

        setupFoundations(tableTop);
        setupTableau(tableTop, deck);
        stockPile.add(deck.cards, false);
        return tableTop;
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public boolean hasCompleted() {
        for(var foundationPile : tableTop.getStacks("foundation_pile")) {
            if (foundationPile.cards.size != 13)
                return false;
        }
        return true;
    }

    @Override
    public void restart() {
        gameStats = new GameStats();
        gameStats.passesLeft = passes;
    }


    public void forceComplete() {
        var cards = new ObjectMap<Suit, Array<Card>>();
        cards.put(Suit.HEARTS, new Array<>());
        cards.put(Suit.SPADES, new Array<>());
        cards.put(Suit.DIAMONDS, new Array<>());
        cards.put(Suit.CLUBS, new Array<>());
        for(var stack : tableTop.stacks.values()) {
            while(stack.any()) {
                var card = stack.pop();
                cards.get(card.suit).add(card);
            }
        }

        var i = 0;
        for(var suitCards : cards) {
            suitCards.value.sort(Comparator.comparingInt(x -> x.rank.getValue()));
            var foundation = tableTop.getStack(String.format("foundation_pile_%d", i));
            for(var card : suitCards.value) {
                foundation.add(card, true);
                foundation.update();
            }
            ++i;
        }
    }

    @Override
    public boolean canAutoComplete() {
        var result = findAutoCompletable() != null;
        return result;
    }

    private CardStack setupStock(TableTop tableTop) {
        var stockPile = tableTop.createStack("stock_pile", StackIndicator.NONE, 0, 3, 0, 0f);
        stockPile.acceptsDrops = false;
        stockPile.canDragHandler = (stack, card) -> false;
        stockPile.tapHandler = new TapHandler() {
            @Override
            public boolean handleTap(CardStack stack, TableTop tableTop, DropAnimator dropAnimator) {
                var targetStack = tableTop.getStack("discard_pile");
                if (!stack.any() && !targetStack.any())
                    return false;
                if (!stack.any())
                {
                    if (passes == -1 || gameStats.passesLeft > 0) {
                        for (var card : targetStack.cards) {
                            card.isFaceUp = false;
                        }
                        targetStack.cards.reverse();
                        dropAnimator.dropOntoStack(tableTop, targetStack, stack, new Array<>(targetStack.cards));
                        targetStack.cards.clear();
                        gameStats.move();
                        return true;
                    }
                    else
                        return false;
                }
                else {
                    if (drawCount == 1) {
                        var card = stack.draw(1).get(0);
                        dropAnimator.dropOntoStack(tableTop, stockPile, targetStack, card);
                        Assets.instance.sounds.playSlide();
                        card.isFaceUp = true;
                        if (!stockPile.any() ) {
                            gameStats.passesLeft = Math.max(0, gameStats.passesLeft - 1);
                            if (gameStats.passesLeft == 0 && passes != -1)
                                stockPile.indicator = StackIndicator.BLOCKED;
                        }
                    }
                    else {
                        var drawnCards = new Array<Card>();
                        var card1 = stack.pop();
                        drawnCards.add(card1);
                        dropAnimator.dropOntoStack(tableTop, stockPile, targetStack, card1);
                        Assets.instance.sounds.playSlide();
                        dropAnimator.postDropHandler = (source, target, droppedCards) -> {
                            if (!stack.any()) {
                                for (var card : drawnCards)
                                    card.isFaceUp = true;

                                if (!stockPile.any() ) {
                                    gameStats.passesLeft = Math.max(0, gameStats.passesLeft - 1);
                                    if (gameStats.passesLeft == 0 && passes != -1)
                                        stockPile.indicator = StackIndicator.BLOCKED;
                                }
                                return;
                            }
                            var card2 = stack.pop();
                            drawnCards.add(card2);
                            dropAnimator.dropOntoStack(tableTop, stockPile, targetStack, card2);
                            Assets.instance.sounds.playSlide();
                            dropAnimator.postDropHandler = (s2, t2, dc2) -> {
                                if (!stack.any()) {
                                    for (var card : drawnCards)
                                        card.isFaceUp = true;

                                    if (!stockPile.any() ) {
                                        gameStats.passesLeft = Math.max(0, gameStats.passesLeft - 1);
                                        if (gameStats.passesLeft == 0 && passes != -1)
                                            stockPile.indicator = StackIndicator.BLOCKED;
                                    }
                                    return;
                                }
                                var card3 = stack.pop();
                                drawnCards.add(card3);
                                dropAnimator.dropOntoStack(tableTop, stockPile, targetStack, card3);
                                Assets.instance.sounds.playSlide();
                                dropAnimator.postDropHandler = (s3, t3, dc3) -> {
                                    for (var card : drawnCards)
                                        card.isFaceUp = true;

                                    if (!stockPile.any() ) {
                                        gameStats.passesLeft = Math.max(0, gameStats.passesLeft - 1);
                                        if (gameStats.passesLeft == 0 && passes != -1)
                                            stockPile.indicator = StackIndicator.BLOCKED;
                                    }
                                };
                            };
                        };
                    }
                    gameStats.move();


                    return true;
                }
            }
        };
        return stockPile;
    }

    public CardStack setupDiscard(TableTop tableTop) {
        var discardPile = tableTop.createStack("discard_pile",StackIndicator.NONE, 1, 3, 0, 0);
        discardPile.canDragHandler = (stack, card) -> true;
        discardPile.doubleTapHandler = (stack, tableTopA, dropAnimator) -> doubleTapToAutoPlaceOnFoundations(stack, tableTopA, dropAnimator);
        return discardPile;
    }

    private void setupFoundations(TableTop tableTop) {
        for(var i = 0; i < 4; ++i) {
            var foundationPile = tableTop.createStack(String.format("foundation_pile_%d", i), StackIndicator.NONE, 3 + i, 3, 0, 0);
            foundationPile.canDragHandler = (stack, card) -> true;
            foundationPile.dropHandler = new DropHandler() {
                @Override
                public boolean handleDrop(TableTop tableTop, DropAnimator dropAnimator, CardStack source, CardStack target, Array<Card> cards) {
                    var foundationPiles = tableTop.getStacks("foundation_pile");
                    var droppedBottomCard = cards.get(0);

                    if (!Card.isAllOfSuit(cards, droppedBottomCard.suit))
                        return false;

                    if (!target.any()) {
                        if (droppedBottomCard.rank != Rank.ACE)
                            return false;
                        for(var collectPile : foundationPiles) {
                            if (collectPile.anyOfSuit(droppedBottomCard.suit)) {
                                return false;
                            }
                            else {
                                dropAnimator.dropOntoStack(tableTop, source, target, new Array<>(cards));
                                dropAnimator.postDropHandler = (sourceStack, targetStack, droppedCards) -> {
                                    if (sourceStack.any())
                                        sourceStack.top().isFaceUp = true;
                                };
                                gameStats.move();
                                return true;
                            }
                        }
                    }
                    else {
                        var topCard = target.top();
                        if (topCard.isOfSameSuitAndDistance(droppedBottomCard, 1)) {
                            dropAnimator.dropOntoStack(tableTop, source, target, new Array<>(cards));
                            dropAnimator.postDropHandler = (sourceStack, targetStack, droppedCards) -> {
                                if (sourceStack.any())
                                    sourceStack.top().isFaceUp = true;
                            };
                            gameStats.move();
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
    }

    private void setupTableau(TableTop tableTop, Deck deck) {
        for(var i = 0; i < 7; ++i) {
            var tableauPile = tableTop.createStack(String.format("tableau_%d_pile", i), StackIndicator.NONE, i, 2, 0, -0.15f,  new Vector2(Float.MAX_VALUE, 1.1f));
            tableauPile.canDragHandler = (stack, card) -> card.isFaceUp;
            tableauPile.dropHandler = (tableTopA, dropAnimator, source, target, cards) -> {
                var valid = false;
                if (target.any()) {
                    var targetTopCard = target.top();
                    var droppedBottomCard = cards.get(0);
                    valid = droppedBottomCard.isOtherColor(targetTopCard) && (targetTopCard.rank.getValue() - droppedBottomCard.rank.getValue() == 1);
                }
                else {
                    valid = cards.get(0).rank == Rank.KING;
                }

                if (valid) {
                    dropAnimator.dropOntoStack(tableTopA, source, target, cards);
                    dropAnimator.postDropHandler = (sourceStack, targetStack, droppedCards) -> {
                        if (sourceStack.any())
                            sourceStack.top().isFaceUp = true;
                    };
                }
                if (valid)
                    gameStats.move();
                return valid;
            };
            tableauPile.doubleTapHandler = (doubleTapSourceStack, tableTopA, dropAnimator) -> doubleTapToAutoPlaceOnFoundations(doubleTapSourceStack, tableTopA, dropAnimator);
            var cards = deck.draw(i + 1);
            for (var j = 0; j < cards.size; ++j) {
                tableauPile.add(cards.get(j), j == cards.size - 1);
            }
        }
    }

    private boolean doubleTapToAutoPlaceOnFoundations(CardStack doubleTapSourceStack, TableTop tableTop, DropAnimator dropAnimator) {
        if (!doubleTapSourceStack.any())
            return false;

        var foundationStacks = tableTop.getStacks("foundation_pile_");
        var card = doubleTapSourceStack.top();
        var targetFoundationStack = findTargetFoundation(card, foundationStacks);

        if (targetFoundationStack != null) {
            doubleTapSourceStack.pop();
            dropAnimator.dropOntoStack(tableTop, doubleTapSourceStack, targetFoundationStack, new Array<>(new Card[]{card}));
            dropAnimator.postDropHandler = (sourceStack, targetStack, droppedCards) -> {
                if (sourceStack.any())
                    sourceStack.top().isFaceUp = true;
            };
            gameStats.move();
            return true;
        }
        return false;
    }

    public boolean hasInfinitePasses() {
        return passes == -1;
    }

    public AutoCompletable findAutoCompletable() {
        var discard = tableTop.getStack("discard_pile");
        var stock = tableTop.getStack("stock_pile");
        var foundations = tableTop.getStacks("foundation_pile");
        var tableauPiles = tableTop.getStacks("tableau");

        if (discard.any())
            return null;
        if (stock.any())
            return null;
        for(var tableau : tableauPiles) {
            for(var card : tableau.cards) {
                if (!card.isFaceUp)
                    return null;
            }
        }

        for(var tableau : tableauPiles) {
            if (tableau.any()) {
                var peeked = tableau.top();
                var targetFoundation = findTargetFoundation(peeked, foundations);
                if (targetFoundation != null) {
                    return autoCompletable.reset(tableau, targetFoundation);
                }
            }
        }

        return null;
    }

    private CardStack findTargetFoundation(Card card, Array<CardStack> foundations) {
        if (card.rank == Rank.ACE) {
            for (var foundationStack : foundations) {
                if (!foundationStack.any()) {
                    return foundationStack;
                }
            }
        }
        else {
            for(var foundationStack : foundations) {
                if (foundationStack.any()) {
                    var foundationCard = foundationStack.top();
                    if (foundationCard.isOfSameSuitAndDistance(card, 1)) {
                        return foundationStack;
                    }
                }
            }
        }
        return null;
    }

    public static class AutoCompletable {
        public CardStack source;
        public CardStack target;
        public AutoCompletable reset(CardStack source, CardStack target) {
            this.source = source;
            this.target = target;
            return this;
        }
    }
}
