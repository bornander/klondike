package com.bornander.klondike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bornander.klondike.*;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.solitaire.GameRules;

import static java.lang.Math.min;

public class MenuScreen extends GameScreen {

    private enum State {
        MAIN,
        RULES,
        SETTINGS,
        STATISTICS,
        ABOUT

    }

    private Stage stage;
    private final SpriteBatch spriteBatch = new SpriteBatch();
    private OrthographicCamera camera;
    private final MenuBackground background = new MenuBackground();

    private Table menuTable;
    private Table rulesTable;
    private Table settingsTable;
    private Table statisticsTable;
    private Table aboutTable;
    private TextButton aboutButton;

    private Label difficultyDrawLabel;
    private Label difficultyTurnsText;

    private final Vector2 centerVector = new Vector2();
    private final Vector2 offScreenVector = new Vector2();


    private State state = State.MAIN;

    public MenuScreen(KlondikeGame game) {
        super(game);
    }


    private Vector2 getCenterScreenPosition(Table table) {
        return centerVector.set(
            (Gdx.graphics.getWidth() - table.getWidth()) / 2.0f,
            (Gdx.graphics.getHeight() - table.getHeight()) / 2.0f
        );
    }

    public Vector2 getOffScreenPosition(Table table, int align) {
        if (Align.isLeft(align))
            return offScreenVector.set(-table.getWidth(), getCenterScreenPosition(table).y);
        if (Align.isRight(align))
            return offScreenVector.set(Gdx.graphics.getWidth(), getCenterScreenPosition(table).y);

        return null;
    }

    private void buildStage() {
        Gdx.graphics.setCursor(Assets.instance.cursors.point);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 1.0f);
        camera.update();

        if (stage != null)
            stage.dispose();

        stage = new Stage();
        stage.setDebugAll(false);

        var w = Gdx.graphics.getWidth();
        var h = Gdx.graphics.getHeight();
        var rootW = w * 0.8f;
        var rootH = h * 0.8f;

        // Build main menu
        {
            menuTable = new Table();
            if (state == State.MAIN)
                menuTable.setBounds((w - rootW) / 2, (h - rootH) / 2, rootW, rootH);
            else
                menuTable.setBounds(-rootW, (h - rootH) / 2, rootW, rootH);
            menuTable.setBackground(Assets.instance.hud.dialogBackground);

            var title = new Label("KLONDIKE", new Label.LabelStyle(Assets.instance.fonts.menuTitle, Color.WHITE));
            menuTable.add().expandX().fillX();
            menuTable.add(title);
            menuTable.add().expandX().fillX();
            menuTable.row().expandY().fillY();
            menuTable.add();

            menuTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 3);
            var playButton = new TextButton("PLAY", Assets.instance.menu.largeButtonStyle);
            playButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();

                    Statistics.instance.play();
                    game.setScreen(new GameplayScreen(game, Settings.instance.rules.getRules()));
                }
            });

            menuTable.add();
            menuTable.add(playButton).fillX();
            menuTable.add();
            menuTable.row();
            var rulesDetailTable = new Table();
            rulesDetailTable.pad(4, 16, 4, 16);
            rulesDetailTable.row().expandX();
            rulesDetailTable.add(new Label("DRAW", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left).fillX();
            difficultyDrawLabel = new Label(Settings.instance.rules.getDrawText(), new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE));
            rulesDetailTable.add(difficultyDrawLabel).align(Align.right);
            rulesDetailTable.row();

            difficultyTurnsText = new Label(Settings.instance.rules.getTurnsText(), new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE));

            rulesDetailTable.add(new Label("TURNS", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left).fillX();
            rulesDetailTable.add(difficultyTurnsText).align(Align.right);
            menuTable.add();
            menuTable.add(rulesDetailTable).expandX().fillX();
            menuTable.add();


            menuTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 3);
            var rulesButton = new TextButton("RULES", Assets.instance.menu.largeButtonStyle);
            rulesButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(menuTable, Align.left);
                    menuTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(rulesTable);
                    rulesTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    state = State.RULES;
                    aboutButton.addAction(Actions.fadeOut(0.25f));
                }
            });
            menuTable.add();
            menuTable.add(rulesButton).fillX();
            menuTable.add();

            menuTable.row().expandY().fillY();
            menuTable.add();

            menuTable.row().pad(4);
            var bottomTable = new Table();

            menuTable.add(bottomTable).colspan(3).fillX().expandX();
            var statsButton = new TextButton("STATS", Assets.instance.menu.smallButtonStyle);
            statsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(menuTable, Align.left);
                    menuTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(statisticsTable);
                    statisticsTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    state = State.STATISTICS;
                    aboutButton.addAction(Actions.fadeOut(0.25f));

                }
            });
            var settingsButton = new TextButton("SETTINGS", Assets.instance.menu.smallButtonStyle);
            settingsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(menuTable, Align.left);
                    menuTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(settingsTable);
                    settingsTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    state = State.SETTINGS;
                    aboutButton.addAction(Actions.fadeOut(0.25f));
                }
            });
            bottomTable.row().height(Assets.instance.fonts.menuSmall.getLineHeight() * 4);
            bottomTable.add(statsButton).width(Value.percentWidth(0.4f, bottomTable)).align(Align.left).expandX();
            bottomTable.add(settingsButton).width(Value.percentWidth(0.4f, bottomTable));

            menuTable.pack();
            menuTable.setWidth(menuTable.getWidth() * 1.2f);
            menuTable.setHeight(menuTable.getHeight() * 1.2f);
            var p = state == State.MAIN ? getCenterScreenPosition(menuTable) : getOffScreenPosition(menuTable, Align.left);
            menuTable.setPosition(p.x, p.y);

            //menuTable.setBounds(menuTable.getX(), menuTable.getY(), menuTable.getWidth() * 1.5f, menuTable.getHeight() * 1.5f);
            stage.addActor(menuTable);
        }

        // Build Rules
        {

            rulesTable = new Table();
            rulesTable.setBackground(Assets.instance.hud.dialogBackground);


            var title = new Label("RULES", new Label.LabelStyle(Assets.instance.fonts.menuTitle, Color.WHITE));
            title.setAlignment(Align.center);
            var difficultyLabel = new Label(Settings.instance.rules.getDifficultyText(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            rulesTable.add(title).colspan(3).fillX();
            rulesTable.add();
            rulesTable.add();

            rulesTable.row().expandY().fillY();
            rulesTable.add();
            rulesTable.add();
            rulesTable.add();

            rulesTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 3);
            var drawButton = new TextButton("DRAW", Assets.instance.menu.largeButtonStyle);
            rulesTable.add(drawButton).fillX();
            rulesTable.add().expandX().fillX();
            var drawLabel = new Label(Settings.instance.rules.getDrawText(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            drawLabel.setAlignment(Align.right);

            drawButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    Settings.instance.rules.stepDraw();
                    drawLabel.setText(Settings.instance.rules.getDrawText());
                    difficultyLabel.setText(Settings.instance.rules.getDifficultyText());
                }
            });

            rulesTable.add(drawLabel).align(Align.right);
            rulesTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 3);
            var turnsButton = new TextButton("TURNS", Assets.instance.menu.largeButtonStyle);
            rulesTable.add(turnsButton).fillX();
            rulesTable.add().expandX().fillX();
            var turnsLabel = new Label(Settings.instance.rules.getTurnsText(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            turnsLabel.setAlignment(Align.right);
            turnsButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    Settings.instance.rules.stepTurns();
                    turnsLabel.setText(Settings.instance.rules.getTurnsText());
                    difficultyLabel.setText(Settings.instance.rules.getDifficultyText());
                }
            });
            rulesTable.add(turnsLabel).align(Align.right);//.width(Value.percentWidth(0.4f, rulesTable));


            rulesTable.row();
            rulesTable.add(difficultyLabel).colspan(3);

            rulesTable.row().expandY().fillY();
            rulesTable.add();

            rulesTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 4).padBottom(8);
            var backButton = new TextButton("BACK", Assets.instance.menu.smallButtonStyle);
            rulesTable.add(backButton).fillX();
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(rulesTable, Align.right);
                    rulesTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(menuTable);
                    menuTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    aboutButton.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeIn(0.5f)));
                    difficultyDrawLabel.setText(Settings.instance.rules.getDrawText());
                    difficultyTurnsText.setText(Settings.instance.rules.getTurnsText());
                    state = State.MAIN;

                    Settings.instance.save();
                }
            });


            rulesTable.pack();
            rulesTable.setWidth(min(rulesTable.getWidth() * 1.4f, Gdx.graphics.getWidth()));
            rulesTable.setHeight(min(rulesTable.getHeight() * 1.2f, Gdx.graphics.getHeight()));

            var p = state == State.RULES ? getCenterScreenPosition(rulesTable) : getOffScreenPosition(rulesTable, Align.right);
            rulesTable.setPosition(p.x, p.y);
            stage.addActor(rulesTable);
        }

        // Build settings
        {

            settingsTable = new Table();
            settingsTable.setBackground(Assets.instance.hud.dialogBackground);


            var title = new Label("SETTINGS", new Label.LabelStyle(Assets.instance.fonts.menuTitle, Color.WHITE));

            settingsTable.add(title).colspan(3);
            settingsTable.add();
            settingsTable.add();

            settingsTable.row().expandY().fillY();
            settingsTable.add();
            settingsTable.add();
            settingsTable.add();

            // DECK
            settingsTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 2);
            var deckButton = new TextButton("DECK", Assets.instance.menu.smallButtonStyle);
            settingsTable.add(deckButton).fillX();
            settingsTable.add().expandX().fillX();
            var deckLabel = new Label(Settings.instance.visual.getBackText(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            deckLabel.setAlignment(Align.right);
            deckButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    Settings.instance.visual.stepCardBacks();
                    deckLabel.setText(Settings.instance.visual.getBackText());
                }
            });

            settingsTable.add(deckLabel).align(Align.right);

            // BACKGROUND
            settingsTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 2);
            var backgroundButton = new TextButton("BACKGROUND", Assets.instance.menu.smallButtonStyle);
            settingsTable.add(backgroundButton).fillX();
            settingsTable.add().expandX().fillX();
            var backgroundLabel = new Label(Settings.instance.visual.getBackgroundText(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            backgroundLabel.setAlignment(Align.right);
            backgroundButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    Settings.instance.visual.stepBackground();;
                    backgroundLabel.setText(Settings.instance.visual.getBackgroundText());
                }
            });
            settingsTable.add(backgroundLabel).align(Align.right);

            // SOUND
            settingsTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 2);
            var soundButton = new TextButton("SOUND", Assets.instance.menu.smallButtonStyle);
            settingsTable.add(soundButton).fillX();
            settingsTable.add().expandX().fillX();
            var soundLabel = new Label(Settings.instance.sound.getState(), new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            soundLabel.setAlignment(Align.right);
            soundButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    Settings.instance.sound.toggle();
                    soundLabel.setText(Settings.instance.sound.getState());
                }
            });
            settingsTable.add(soundLabel).align(Align.right);

            // VIBRATION
            settingsTable.row().pad(4).height(Assets.instance.fonts.menu.getLineHeight() * 2);
            var vibrationButton = new TextButton("VIBRATION", Assets.instance.menu.smallButtonStyle);
            settingsTable.add(vibrationButton).fillX();
            settingsTable.add().expandX().fillX();
            var vibrationLabel = new Label("ON", new Label.LabelStyle(Assets.instance.fonts.menu, Color.WHITE));
            vibrationLabel.setAlignment(Align.right);
            vibrationButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    vibrationLabel.setText("OFF");
                }
            });
            settingsTable.add(vibrationLabel).align(Align.right);

            settingsTable.row().expandY().fillY();
            settingsTable.add();

            settingsTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 4).padBottom(8);
            var backButton = new TextButton("BACK", Assets.instance.menu.smallButtonStyle);
            settingsTable.add(backButton).fillX();
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(settingsTable, Align.right);
                    settingsTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(menuTable);
                    menuTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    aboutButton.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeIn(0.5f)));
                    state = State.MAIN;
                    Settings.instance.save();
                }
            });


            settingsTable.pack();
            settingsTable.setWidth(settingsTable.getWidth() * 1.2f);
            settingsTable.setHeight(settingsTable.getHeight() * 1.2f);

            var p = state == State.ABOUT ? getCenterScreenPosition(settingsTable) : getOffScreenPosition(settingsTable, Align.right);
            settingsTable.setPosition(p.x, p.y);
            stage.addActor(settingsTable);
        }


        // Build statistics
        {

            statisticsTable = new Table();
            statisticsTable.setBackground(Assets.instance.hud.dialogBackground);


            var title = new Label("STATISTICS", new Label.LabelStyle(Assets.instance.fonts.menuTitle, Color.WHITE));

            statisticsTable.add(title).colspan(4);
            statisticsTable.add();
            statisticsTable.add();
            statisticsTable.add();

            statisticsTable.row().expandY().fillY();
            statisticsTable.add().width(Value.percentWidth(0.22f, statisticsTable));
            statisticsTable.add().width(Value.percentWidth(0.22f, statisticsTable));
            statisticsTable.add().width(Value.percentWidth(0.22f, statisticsTable));
            statisticsTable.add().width(Value.percentWidth(0.22f, statisticsTable));

            // Table header
            statisticsTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 2);
            statisticsTable.add(new Label("TYPE", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            statisticsTable.add(new Label("PLAYED", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.right);
            statisticsTable.add(new Label("WON", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.right);
            statisticsTable.add(new Label("MOVES", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.right);
            statisticsTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 2);
            for(var type : Settings.instance.rules.getDifficulties()) {
                var stats = Statistics.instance.get(type);
                statisticsTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
                statisticsTable.add(new Label(type, new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.left);
                statisticsTable.add(new Label(Integer.toString(stats.played), new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
                statisticsTable.add(new Label(Integer.toString(stats.completed), new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
                statisticsTable.add(new Label(Integer.toString(stats.moves), new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            }

            statisticsTable.row().expandY().fillY();
            statisticsTable.add();

            statisticsTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 4).padBottom(8);
            var backButton = new TextButton("BACK", Assets.instance.menu.smallButtonStyle);
            statisticsTable.add(backButton).fillX();
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(statisticsTable, Align.right);
                    statisticsTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(menuTable);
                    menuTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    aboutButton.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeIn(0.5f)));
                    state = State.MAIN;
                    Settings.instance.save();
                }
            });


            statisticsTable.pack();
            statisticsTable.setWidth(statisticsTable.getWidth() * 1.2f);
            statisticsTable.setHeight(statisticsTable.getHeight() * 1.2f);

            var p = state == State.SETTINGS ? getCenterScreenPosition(statisticsTable) : getOffScreenPosition(statisticsTable, Align.right);
            statisticsTable.setPosition(p.x, p.y);
            stage.addActor(statisticsTable);
        }
        // Build about
        {

            aboutTable = new Table();
            aboutTable.setBackground(Assets.instance.hud.dialogBackground);


            var title = new Label("ABOUT", new Label.LabelStyle(Assets.instance.fonts.menuTitle, Color.WHITE));

            aboutTable.add(title).colspan(2);
            aboutTable.add();

            aboutTable.row().expandY().fillY();
            aboutTable.add().width(Value.percentWidth(0.40f, aboutTable));
            aboutTable.add().width(Value.percentWidth(0.40f, aboutTable));

            // Table header
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 2);
            aboutTable.add(new Label("CODE", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("BORNANDER", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("GRAPHICS", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("KENNEY.NL", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("FONT", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("NISKALA HURUF", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("SOUNDS", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("FREESOUND.ORG", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add();
            //aboutTable.add(new Label("SOUNDS", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("ESCOBARRB26", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add();
            //aboutTable.add(new Label("SOUNDS", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("CHRISTOPHERDERP", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("BUILT USING", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("LIBGDX", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("TESTED BY", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("MALOSKAR", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);
            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 1);
            aboutTable.add(new Label("FOR", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.LIGHT_GRAY))).align(Align.left);
            aboutTable.add(new Label("AGNETA", new Label.LabelStyle(Assets.instance.fonts.menuSmall, Color.WHITE))).align(Align.right);

            aboutTable.row().expandY().fillY();
            aboutTable.add();

            aboutTable.row().pad(4).height(Assets.instance.fonts.menuSmall.getLineHeight() * 4).padBottom(8);
            var backButton = new TextButton("BACK", Assets.instance.menu.smallButtonStyle);
            aboutTable.add(backButton).fillX();
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(aboutTable, Align.right);
                    aboutTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(menuTable);
                    menuTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    aboutButton.addAction(Actions.sequence(Actions.delay(1.0f), Actions.fadeIn(0.5f)));
                    state = State.MAIN;
                    Settings.instance.save();
                }
            });


            aboutTable.pack();
            aboutTable.setWidth(aboutTable.getWidth() * 1.2f);
            aboutTable.setHeight(aboutTable.getHeight() * 1.2f);

            var p = state == State.SETTINGS ? getCenterScreenPosition(aboutTable) : getOffScreenPosition(aboutTable, Align.right);
            aboutTable.setPosition(p.x, p.y);
            stage.addActor(aboutTable);
        }

        aboutButton = new TextButton("?", Assets.instance.menu.aboutButtonStyle);
        aboutButton.setBounds(8, 8, Assets.instance.fonts.menuSmall.getLineHeight() * 3, Assets.instance.fonts.menuSmall.getLineHeight() * 3);
        aboutButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (state == State.MAIN) {
                    Log.info("ABOUT CLICKED");
                    Assets.instance.sounds.playButton();
                    var off = getOffScreenPosition(menuTable, Align.left);
                    menuTable.addAction(Actions.moveTo(off.x, off.y, 1.0f, Interpolation.fastSlow));
                    var on = getCenterScreenPosition(aboutTable);
                    aboutTable.addAction(Actions.moveTo(on.x, on.y, 1.0f, Interpolation.fastSlow));
                    state = State.STATISTICS;
                    aboutButton.addAction(Actions.fadeOut(0.25f));
                }
            }
        });
        stage.addActor(aboutButton);


        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        Assets.instance.resize(width, height);
        state = State.MAIN;
        buildStage();
    }

    @Override
    public void update(float delta) {
        if (Gdx.input.isButtonPressed(Input.Buttons.BACK))
            Gdx.app.exit();
        if (stage == null)
            return;
        stage.act(delta);
        background.update(delta);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        if (stage == null)
            return;

        camera.update();
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        background.render(spriteBatch);
        spriteBatch.end();


        stage.draw();
    }
}
