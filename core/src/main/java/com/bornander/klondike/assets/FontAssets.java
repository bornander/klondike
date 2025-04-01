package com.bornander.klondike.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;
import com.bornander.klondike.Settings;
import com.bornander.klondike.libgdx.Log;

public class FontAssets implements Disposable {

    private final static String FONT_FILE = "MotleyForcesRegular-w1rZ3.ttf";
    private final static String CHAR_SET = "ABCDEFGHIJKLMNOPQRSTUVWXYUZ0123456789:!?.";

    public BitmapFont hud;
    public BitmapFont menuTitle;
    public BitmapFont menu;
    public BitmapFont menuSmall;

    @Override
    public void dispose() {
        hud.dispose();
    }

    public void update(int width, int height) {
        var visual = Settings.instance.visual;
        hud = update(hud, FONT_FILE, visual.getHudFontSize(), Color.DARK_GRAY, CHAR_SET);
        menuTitle = update(menuTitle, FONT_FILE, visual.getMenuLargeFontSize(), Color.DARK_GRAY, CHAR_SET);
        menu = update(menu, FONT_FILE, visual.getMenuFontSize(), Color.DARK_GRAY, CHAR_SET);
        menuSmall = update(menuSmall, FONT_FILE, (int)(visual.getMenuFontSize() * 0.75f), Color.DARK_GRAY, CHAR_SET);
    }

    private BitmapFont update(BitmapFont font, String fontName, int size, Color color, String charSet) {
        if (font != null) {
            Log.info("Disposing font %s", fontName);
            font.dispose();
        }
        return buildFont(fontName, size, color, charSet);
    }

    private static BitmapFont buildFont(String filename, float size, Color borderColor, String characters) {
        Log.info("Building font %s to size %.2f", filename, size);
        var generator = new FreeTypeFontGenerator(Gdx.files.internal(String.format("fonts/%s", filename)));
        var parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = (int)size;
        parameter.shadowColor = borderColor;
        parameter.shadowOffsetX = 2;
        parameter.shadowOffsetY = 2;
        parameter.characters = characters;
        parameter.kerning = true;
        parameter.gamma = 1.0f;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;

        var font = generator.generateFont(parameter);
        font.getData().markupEnabled = true;

        generator.dispose();
        return font;
    }
}
