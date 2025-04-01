package com.bornander.klondike;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.screens.MenuScreen;
import com.bornander.klondike.solitaire.*;
import com.bornander.klondike.solitaire.events.GenericHandler;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import static java.lang.Math.min;

public class KlondikeHud implements Disposable {
    public Stage stage = new Stage();
    public GenericHandler onEvent;

    private Label elapsedTime;
    private Label moves;
    private Label turns;
    private Image movesIcon;
    private Image turnsIcon;

    private Button menuButton;
    private Button autoCompleteButton;
    private Table dialog;
    private Table completedDialog;

    private boolean autoCompleteClicked = false;

    private KlondikeGame game;
    private KlondikeRules rules;
    private DropAnimator dropAnimator;

    public KlondikeHud(KlondikeGame game, KlondikeRules rules, DropAnimator dropAnimator) {
        this.game = game;
        this.rules = rules;
        this.dropAnimator = dropAnimator;
        stage.setViewport(new ScreenViewport());
    }


    @Override
    public void dispose() {
        if (stage != null)
            stage.dispose();
    }

    private void fireEvent(String event) {
        if (onEvent != null)
            onEvent.fire(event);
    }


    public void updateLayout(TableTop tabletop, boolean isPaused, boolean isCompleted) {

        if (stage != null)
            stage.dispose();
        var hudTopBarVerticalSize = (Settings.instance.visual.getHudTopBarPixelSize()) - 8;
        var halfTopBarSize = hudTopBarVerticalSize / 2.0f;
        stage = new Stage();
        stage.setViewport(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        var rootTable = new Table();
        rootTable.setFillParent(true);

        // TopBar
        var timeIcon = new Image(Assets.instance.hud.time);
        movesIcon = new Image(Assets.instance.hud.moves);
        turnsIcon = new Image(Assets.instance.hud.turns);

        var cardBounds = tabletop.getBounds();

        var labelStyle = new Label.LabelStyle();
        labelStyle.font = Assets.instance.fonts.hud;
        labelStyle.fontColor = Color.WHITE;

        elapsedTime = new Label("00:00", labelStyle);
        moves = new Label("", labelStyle);
        turns = new Label("0", labelStyle);
        menuButton = new Button(Assets.instance.hud.menuUp, Assets.instance.hud.menuDown);
        autoCompleteButton = new Button(Assets.instance.hud.autoCompleteUp, Assets.instance.hud.autoCompleteDown);
        turns.setAlignment(Align.right);
        moves.setAlignment(Align.right);
        rootTable.row().height(hudTopBarVerticalSize).pad(4).expandX();

        var topBar = new Table();
        topBar.add(timeIcon).maxSize(halfTopBarSize, halfTopBarSize).padLeft(cardBounds.x);
        var valueWidth = halfTopBarSize * 4;
        topBar.add(elapsedTime).padLeft(4).width(valueWidth);
        topBar.add().expandX().fillX();
        topBar.add(moves).padRight(4).width(valueWidth);
        topBar.add(movesIcon).maxSize(halfTopBarSize, halfTopBarSize).padRight(cardBounds.x);

        topBar.row();

        topBar.add();
        topBar.add();
        topBar.add();
        topBar.add(turns).padRight(4).width(valueWidth);
        topBar.add(turnsIcon).maxSize(halfTopBarSize, halfTopBarSize).padRight(cardBounds.x);

        rootTable.add(topBar).expandX().fillX();
        rootTable.row().expandY();
        rootTable.add();
        stage.addActor(rootTable);
        menuButton.setBounds((Gdx.graphics.getWidth() - hudTopBarVerticalSize) / 2.0f, Gdx.graphics.getHeight() - hudTopBarVerticalSize, hudTopBarVerticalSize, hudTopBarVerticalSize);
        menuButton.setTransform(true);
        menuButton.setOrigin(halfTopBarSize, halfTopBarSize);

        autoCompleteButton.setBounds((Gdx.graphics.getWidth() - hudTopBarVerticalSize) / 2.0f + menuButton.getWidth() + 8, Gdx.graphics.getHeight(), hudTopBarVerticalSize, hudTopBarVerticalSize);
        autoCompleteButton.setTransform(true);
        autoCompleteButton.setOrigin(halfTopBarSize, halfTopBarSize);


        stage.addActor(menuButton);
        stage.addActor(autoCompleteButton);


        // Build Pause-dialog
        {
            var dialogWidth = Gdx.graphics.getWidth() * 0.8f;
            var dialogHeight = Assets.instance.fonts.hud.getLineHeight() * 10;

            dialog = new Table();
            if (isPaused)
                dialog.setBounds((Gdx.graphics.getWidth() - dialogWidth) / 2.0f, Gdx.graphics.getHeight() - dialogHeight * 1.1f, dialogWidth, dialogHeight);
            else
                dialog.setBounds((Gdx.graphics.getWidth() - dialogWidth) / 2.0f, Gdx.graphics.getHeight(), dialogWidth, dialogHeight);

            dialog.setBackground(Assets.instance.hud.dialogBackground);
            var label = new Label("PAUSED", new Label.LabelStyle(Assets.instance.fonts.hud, Color.WHITE));
            dialog.row().expandY().fillY();
            dialog.add(label).colspan(3).expandX();
            dialog.row().height(Assets.instance.fonts.hud.getLineHeight() * 3);

            var quit = new TextButton("QUIT", Assets.instance.hud.quitStyle);
            quit.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    game.setScreen(new MenuScreen(game));
                }
            });
            dialog.add(quit).fillX().padRight(10).padBottom(4);

            var restart = new TextButton("RESTART", Assets.instance.hud.restartStyle);
            dialog.add(restart).fillX().padLeft(5).padRight(5).padBottom(4);
            restart.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var targetX = (Gdx.graphics.getWidth() - dialog.getWidth()) / 2.0f;
                    var targetY = Gdx.graphics.getHeight() + 1;

                    dialog.addAction(moveTo(targetX, targetY, 0.3f, Interpolation.swingIn));
                    menuButton.addAction(sequence(delay(0.3f),rotateTo(0, 0.2f)));
                    autoCompleteButton.addAction(moveTo(autoCompleteButton.getX(), Gdx.graphics.getHeight(), 1.0f));
                    autoCompleteClicked = false;
                    fireEvent("RESTART");
                }
            });

            var resume = new TextButton("RESUME", Assets.instance.hud.resumeStyle);
            dialog.add(resume).fillX().padLeft(10).padBottom(4);
            resume.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var targetX = (Gdx.graphics.getWidth() - dialog.getWidth()) / 2.0f;
                    var targetY = Gdx.graphics.getHeight() + 1;

                    dialog.addAction(moveTo(targetX, targetY, 0.3f, Interpolation.swingIn));
                    menuButton.addAction(sequence(delay(0.3f),rotateTo(0, 0.2f)));

                    fireEvent("RESUME");
                }
            });

            stage.addActor(dialog);
        }

        // Build Completed-dialog
        {
            var dialogWidth = Gdx.graphics.getWidth() * 0.8f;
            var dialogHeight = Assets.instance.fonts.hud.getLineHeight() * 10;

            completedDialog = new Table();
            if (isCompleted)
                completedDialog.setBounds((Gdx.graphics.getWidth() - dialogWidth) / 2.0f, Gdx.graphics.getHeight() - dialogHeight * 1.1f, dialogWidth, dialogHeight);
            else
                completedDialog.setBounds((Gdx.graphics.getWidth() - dialogWidth) / 2.0f, Gdx.graphics.getHeight(), dialogWidth, dialogHeight);

            completedDialog.setBackground(Assets.instance.hud.dialogBackground);
            var title = new Label("COMPLETED", new Label.LabelStyle(Assets.instance.fonts.hud, Color.WHITE));
            var encouragement = new Label("WELL DONE!", new Label.LabelStyle(Assets.instance.fonts.hud, Color.WHITE));
            completedDialog.row().expandY().fillY();
            completedDialog.add(title).colspan(3).expandX();
            completedDialog.row().expandY().fillY();
            completedDialog.add(encouragement).colspan(3).expandX();
            completedDialog.row().height(Assets.instance.fonts.hud.getLineHeight() * 3);

            var quit = new TextButton("QUIT", Assets.instance.hud.quitStyle);
            completedDialog.add(quit).fillX().padRight(10).padBottom(4);
            quit.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    game.setScreen(new MenuScreen(game));
                }
            });

            completedDialog.add().fillX().padLeft(5).padRight(5).padBottom(4);

            var restart = new TextButton("RESTART", Assets.instance.hud.resumeStyle);
            completedDialog.add(restart).fillX().padLeft(10).padBottom(4);
            restart.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Assets.instance.sounds.playButton();
                    var targetX = (Gdx.graphics.getWidth() - completedDialog.getWidth()) / 2.0f;
                    var targetY = Gdx.graphics.getHeight() + 1;

                    completedDialog.addAction(moveTo(targetX, targetY, 0.3f, Interpolation.swingIn));
                    menuButton.addAction(sequence(delay(0.3f),rotateTo(0, 0.2f)));
                    fireEvent("RESTART");
                }
            });

            stage.addActor(completedDialog);
        }

        stage.act();
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Log.info("Clicked menu");
                Assets.instance.sounds.playButton();
                Vibrator.button();
                var targetX = (Gdx.graphics.getWidth() - dialog.getWidth()) / 2.0f;
                var targetY = Gdx.graphics.getHeight() - dialog.getHeight() * 1.1f;
                dialog.addAction(sequence(delay(0.2f), moveTo(targetX, targetY, 0.6f, Interpolation.swingOut)));
                menuButton.addAction(rotateTo(90, 0.2f));
                fireEvent("PAUSE");
            }
        });

        autoCompleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Log.info("Clicked auto-complete");
                Assets.instance.sounds.playButton();
                if (autoCompleteClicked)
                    return;

                autoCompleteClicked = true;
                autoCompleteButton.addAction(moveTo(autoCompleteButton.getX(), Gdx.graphics.getHeight(), 1.0f));
                fireEvent("AUTOCOMPLETE");
            }
        });
    }


    public void update(float delta, GameStats gameStats, boolean isPaused) {
        stage.act(delta);

        if (!isPaused) {
            gameStats.elapsedTime += delta;
            var elapsedMinutes = min(99, ((int) gameStats.elapsedTime) / 60);
            var elapsedSeconds = ((int) gameStats.elapsedTime) % 60;
            // TODO: Allocation
            elapsedTime.setText(String.format("%02d:%02d", elapsedMinutes, elapsedSeconds));
            moves.setText(gameStats.getMoves());
            turns.setText(gameStats.passesLeft);
        }
    }

    public void render() {
        stage.draw();
    }

    public void displayCompletedDialog() {
        Vibrator.button();
        var targetX = (Gdx.graphics.getWidth() - dialog.getWidth()) / 2.0f;
        var targetY = Gdx.graphics.getHeight() - dialog.getHeight() * 1.1f;
        completedDialog.addAction(sequence(delay(0.2f), moveTo(targetX, targetY, 0.6f, Interpolation.swingOut)));
        menuButton.addAction(rotateTo(90, 0.2f));
    }

    public void restart(GameRules rules) {
        var infinitePasses =((KlondikeRules) rules).hasInfinitePasses();
        turns.setVisible(!infinitePasses);
        turnsIcon.setVisible(!infinitePasses);
    }

    public void showAutoCompleteButton() {
        autoCompleteButton.addAction(moveTo(autoCompleteButton.getX(), Gdx.graphics.getHeight() - autoCompleteButton.getHeight(), 1.0f));
    }
}
