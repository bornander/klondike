package com.bornander.klondike.solitaire;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import static java.lang.Math.min;

public class VisualSettings {
    public ClothType clothType = ClothType.GREEN;
    public BackColor backColor = BackColor.RED;
    public int backStyle = 4;
    public Color clothTextureBrightness = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public int hudTopBarRowCount = 2;
    public float hudTopBarFraction = 0.1f;
    public float hudFontFactor = 0.8f;

    public float getHudTopBarPixelSize() {
        return (float)min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) * hudTopBarFraction;
    }

    public int getHudFontSize() {
        return (int)((getHudTopBarPixelSize() / hudTopBarRowCount) * hudFontFactor);
    }

    public int getMenuFontSize() {
        return (int)((getHudTopBarPixelSize() / hudTopBarRowCount) * hudFontFactor);
    }

    public int getMenuLargeFontSize() {
        return getMenuFontSize() * 2;
    }

    public void stepCardBacks() {
        backStyle++;
        if (backStyle > 5) {
            backStyle = 1;
            switch (backColor) {
                case RED: backColor = BackColor.GREEN; break;
                case GREEN: backColor = BackColor.BLUE; break;
                case BLUE: backColor = BackColor.RED; break;
            }
        }
    }

    public String getBackText() {
        return String.format("%s %d", backColor, backStyle);
    }

    public void stepBackground() {
        switch (clothType) {
            case BLUE: clothType = ClothType.GREEN; break;
            case GREEN: clothType = ClothType.YELLOW; break;
            case YELLOW: clothType = ClothType.PINK; break;
            case PINK: clothType = ClothType.BROWN; break;
            case BROWN: clothType = ClothType.GRAY; break;
            case GRAY: clothType = ClothType.PURPLE; break;
            case PURPLE: clothType = ClothType.BLUE; break;
        }
    }

    public String getBackgroundText() {
        return clothType.toString();
    }
}
