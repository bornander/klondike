package com.bornander.klondike.assets;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bornander.klondike.Assets;

import static com.bornander.klondike.Assets.loadTexture;

public class HudAssets {
    public final TextureRegion moves;
    public final TextureRegion turns;
    public final TextureRegion time;
    public final Drawable settingsUp;
    public final Drawable settingsDown;
    public final Drawable menuUp;
    public final Drawable menuDown;
    public final Drawable autoCompleteUp;
    public final Drawable autoCompleteDown;
    public final NinePatchDrawable dialogBackground;
    public final TextButton.TextButtonStyle quitStyle;
    public final TextButton.TextButtonStyle restartStyle;
    public final TextButton.TextButtonStyle resumeStyle;

    public HudAssets() {
        moves = loadTexture("graphics/hud/icons/moves.png");
        turns = loadTexture("graphics/hud/icons/turns.png");
        time = loadTexture("graphics/hud/icons/time.png");

        settingsUp = new TextureRegionDrawable(loadTexture("graphics/hud/icons/settings_up.png"));
        settingsDown = new TextureRegionDrawable(loadTexture("graphics/hud/icons/settings_down.png"));
        menuUp = new TextureRegionDrawable(loadTexture("graphics/hud/icons/menu_up.png"));
        menuDown = new TextureRegionDrawable(loadTexture("graphics/hud/icons/menu_up.png"));
        autoCompleteUp = new TextureRegionDrawable(loadTexture("graphics/hud/icons/autocomplete_up.png"));
        autoCompleteDown = new TextureRegionDrawable(loadTexture("graphics/hud/icons/autocomplete_up.png"));
        dialogBackground = new NinePatchDrawable(new NinePatch(loadTexture("graphics/hud/dialog/dialog_background.png"), 16, 16, 16, 16));
        quitStyle = new TextButton.TextButtonStyle(load9("dialog/button_quit_up.png"), load9("dialog/button_quit_down.png"), null, Assets.instance.fonts.hud);
        restartStyle = new TextButton.TextButtonStyle(load9("dialog/button_restart_up.png"), load9("dialog/button_restart_down.png"), null, Assets.instance.fonts.hud);
        resumeStyle = new TextButton.TextButtonStyle(load9("dialog/button_resume_up.png"), load9("dialog/button_resume_down.png"), null, Assets.instance.fonts.hud);
    }

    private NinePatchDrawable load9(String file) {
        return new NinePatchDrawable(new NinePatch(loadTexture(String.format("graphics/hud/%s", file)),16, 16, 16, 20));
    }
}
