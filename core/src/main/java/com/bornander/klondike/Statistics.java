package com.bornander.klondike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;

public class Statistics {
    public static Statistics instance = new Statistics();
    private final static Json json = new Json(JsonWriter.OutputType.json);

    public static class Stats {
        public int played;
        public int completed;
        public int moves;
    }

    public final ObjectMap<String, Stats> data = new ObjectMap<>();

    public Stats get(String type) {
        if (!data.containsKey(type))
            data.put(type, new Stats());
        return data.get(type);
    }

    private Stats get() {
        return get(Settings.instance.rules.getDifficultyText());
    }

    public void play() {
        get().played++;
        save();
    }

    public void complete() {
        get().completed++;
        save();
    }

    public void move() {
        get().moves++;
    }

    private FileHandle getFile() {
        return Gdx.files.local("klondike_statistics.json");
    }

    public void save() {
        try {
            json.setUsePrototypes(false);
            getFile().writeString(json.prettyPrint(this), false);
        }
        catch(Throwable t) {
        }
    }

    public void load() {
        try {
            var file = getFile();
            if (!file.exists())
                return;

            instance = json.fromJson(Statistics.class, file);
        }
        catch (Throwable t) {
        }
    }
}
