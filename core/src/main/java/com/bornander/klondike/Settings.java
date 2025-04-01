package com.bornander.klondike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.bornander.klondike.solitaire.InputSettings;
import com.bornander.klondike.solitaire.SoundSettings;
import com.bornander.klondike.solitaire.VisualSettings;

public class Settings {
    public static Settings instance = new Settings();
    private final static Json json = new Json(JsonWriter.OutputType.json);

    public final VisualSettings visual = new VisualSettings();
    public final InputSettings input = new InputSettings();
    public final RuleSettings rules = new RuleSettings();
    public final SoundSettings sound = new SoundSettings();

    private FileHandle getFile() {
        return Gdx.files.local("klondike_settings.json");
    }

    public void save() {
        try {
            json.setUsePrototypes(false);
            getFile().writeString(json.prettyPrint(this), false);
        }
        catch (Throwable t) {
        }
    }

    public void load() {
        try {
            var file = getFile();
            if (!file.exists())
                return;

            instance = json.fromJson(Settings.class, file);
        }
        catch (Throwable t) {
        }
    }
}
