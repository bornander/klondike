package com.bornander.klondike.assets;

import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bornander.klondike.solitaire.BackColor;
import com.bornander.klondike.solitaire.Rank;
import com.bornander.klondike.solitaire.StackIndicator;
import com.bornander.klondike.solitaire.Suit;
import static com.bornander.klondike.Assets.loadTexture;

public class CardAssets {

    private final ObjectMap<BackColor, IntMap<TextureRegion>> backs;
    private final ObjectMap<Suit, ObjectMap<Rank, TextureRegion>> cards;
    private final ObjectMap<StackIndicator, TextureRegion> indicators;

    public final TextureRegion dropShadow;

    public final float aspectRatio;

    public CardAssets( ) {
        backs = loadBacks();
        cards = loadCards();
        indicators = loadIndicators();

        dropShadow = loadTexture("graphics/cards/dropshadow.png");

        var aceOfHearts = getFace(Suit.HEARTS, Rank.ACE);
        aspectRatio = aceOfHearts.getRegionWidth() / (float)aceOfHearts.getRegionHeight();
    }

    private static ObjectMap<StackIndicator, TextureRegion> loadIndicators() {
        var targets = new ObjectMap<StackIndicator, TextureRegion>();
        for(var indicator : StackIndicator.values()) {
            targets.put(indicator, loadTarget(indicator));
        }
        return targets;
    }

    private static TextureRegion loadTarget(StackIndicator indicator) {
        return loadTexture(String.format("graphics/cards/indicators/%s.png", indicator.getAssetName()));
    }

    private static ObjectMap<Suit, ObjectMap<Rank, TextureRegion>> loadCards() {
        var cards = new ObjectMap<Suit, ObjectMap<Rank, TextureRegion>>();
        for(var suit : Suit.values()) {
            var suitMap = new ObjectMap<Rank, TextureRegion>();
            for (var rank : Rank.values()) {
                suitMap.put(rank, loadFace(suit, rank));
            }
            cards.put(suit, suitMap);
        }
        return cards;
    }

    private static TextureRegion loadFace(Suit suit, Rank rank) {
        return loadTexture(String.format("graphics/cards/%s/card%s%s.png", suit.getAssetName().toLowerCase(), suit.getAssetName(), rank.getAssetName()));
    }

    private static ObjectMap<BackColor, IntMap<TextureRegion>> loadBacks() {
        var backs = new ObjectMap<BackColor, IntMap<TextureRegion>>();
        for(var color : BackColor.values()) {
            var colorMap = new IntMap<TextureRegion>();
            for(var i = 1; i < 6; ++i) {
                colorMap.put(i, loadBack(color, i));
            }
            backs.put(color, colorMap);
        }
        return backs;
    }

    private static TextureRegion loadBack(BackColor color, int index) {
        return loadTexture(String.format("graphics/cards/backs/%s/%d.png", color.getAssetName(), index));
    }

    public TextureRegion getBack(BackColor color, int index) {
        return backs.get(color).get(index);
    }

    public TextureRegion getFace(Suit suit, Rank rank) {
        return cards.get(suit).get(rank);
    }

    public TextureRegion getIndicator(StackIndicator indicator) { return indicators.get(indicator); }
}
