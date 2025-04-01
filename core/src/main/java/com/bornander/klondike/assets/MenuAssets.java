package com.bornander.klondike.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bornander.klondike.Assets;

import static com.bornander.klondike.Assets.loadTexture;

public class MenuAssets {

    public final NinePatchDrawable background;
    public final TextButton.TextButtonStyle largeButtonStyle;
    public final TextButton.TextButtonStyle smallButtonStyle;
    public final TextButton.TextButtonStyle aboutButtonStyle;

    public MenuAssets() {
        background = new NinePatchDrawable(new NinePatch(loadTexture("graphics/hud/dialog/dialog_background.png"), 16, 16, 16, 16));
        largeButtonStyle = new TextButton.TextButtonStyle(load9("dialog/button_resume_up.png"), load9("dialog/button_resume_down.png"), null, Assets.instance.fonts.menu);
        smallButtonStyle = new TextButton.TextButtonStyle(load9("dialog/button_resume_up.png"), load9("dialog/button_resume_down.png"), null, Assets.instance.fonts.menuSmall);
        aboutButtonStyle = new TextButton.TextButtonStyle(load9("dialog/button_about_up.png"), load9("dialog/button_about_down.png"), null, Assets.instance.fonts.menuSmall);
    }

    private TextureRegion load(String file) {
        return loadTexture(String.format("graphics/hud/%s", file));
    }

    private TextureRegionDrawable loadD(String file) {
        return new TextureRegionDrawable(loadTexture(String.format("graphics/hud/%s", file)));
    }

    private NinePatchDrawable load9(String file) {
        return new NinePatchDrawable(new NinePatch(loadTexture(String.format("graphics/hud/%s", file)),16, 16, 16, 20));
    }
}
