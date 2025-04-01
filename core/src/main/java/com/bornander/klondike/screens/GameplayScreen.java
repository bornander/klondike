package com.bornander.klondike.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ScreenUtils;

import com.bornander.klondike.*;
import com.bornander.klondike.libgdx.Log;
import com.bornander.klondike.solitaire.*;
import com.bornander.klondike.solitaire.events.GenericHandler;

import static com.bornander.klondike.libgdx.CameraExtensions.unproject2D;

public class GameplayScreen extends GameScreen implements InputProcessor {
    private enum GameState {
        PLAYING,
        COMPLETED,
        MENU
    }
    private final ShapeRenderer debugRenderer = new ShapeRenderer();
    private final SpriteBatch spriteBatch = new SpriteBatch();
    private final OrthographicCamera camera;
    private final Vector2 touchDownPosition = new Vector2();
    private TableTop tableTop;
    private final Grabbed grabbed = new Grabbed();
    private final DropAnimator dropAnimator = new DropAnimator();
    private CardStack tapDownStack = null;
    private final ObjectMap<CardStack, Float> previousTapTime = new ObjectMap<>();
    private final CompletedEffect completedEffect = new CompletedEffect();
    private final GameRules rules;
    private final KlondikeHud hud;
    private boolean paused = false;
    private boolean completed = false;
    private float elapsedTime;
    private boolean hasBecomeAutoCompletable = false;
    private GameState state = GameState.PLAYING;

    public GameplayScreen(KlondikeGame game, GameRules rules) {
        super(game);
        this.rules = rules;
        this.hud = new KlondikeHud(game, (KlondikeRules) rules, dropAnimator);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2.0f, camera.viewportHeight / 2.0f, 1.0f);

        tableTop = rules.create();

        Gdx.input.setInputProcessor(new InputMultiplexer(hud.stage));
        hud.onEvent = new GenericHandler() {
            @Override
            public void fire(String event) {
                switch (event) {
                    case "AUTOCOMPLETE":
                        Gdx.graphics.setCursor(Assets.instance.cursors.point);
                        paused = true;
                        runAutoComplete();
                        break;
                    case "PAUSE":
                        Gdx.graphics.setCursor(Assets.instance.cursors.point);
                        paused = true;
                        break;
                    case "RESUME":
                        paused = false;
                        Gdx.graphics.setCursor(Assets.instance.cursors.open);
                        break;
                    case "RESTART":
                        Gdx.graphics.setCursor(Assets.instance.cursors.open);
                        state = GameState.PLAYING;
                        paused = false;
                        rules.restart();
                        tableTop = rules.create();
                        hud.restart(rules);
                        layoutCards(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                        Gdx.input.setInputProcessor(new InputMultiplexer(hud.stage, GameplayScreen.this));
                        break;
                }
            }
        };
        Gdx.graphics.setCursor(Assets.instance.cursors.open);
    }

    private void runAutoComplete() {
        var autoCompletable = ((KlondikeRules)rules).findAutoCompletable();
        if (autoCompletable == null)
            return;

        dropAnimator.dropOntoStack(tableTop, autoCompletable.source, autoCompletable.target, autoCompletable.source.pop());
        Assets.instance.sounds.playSlide();
        dropAnimator.postDropHandler = (source, target, droppedCards) ->
            runAutoComplete();
    }

    private void layoutCards(int width, int height) {
        var hudTopBarVerticalSize = Settings.instance.visual.getHudTopBarPixelSize();
        var cardBounds = new Rectangle(
            width * 0.01f,
            0,
            width * 0.98f,
            height - hudTopBarVerticalSize
        );
        tableTop.updateLayoutBounds(cardBounds);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(camera.viewportWidth / 2.0f, camera.viewportHeight / 2.0f, 1.0f);
        layoutCards(width, height);
        hud.updateLayout(tableTop, paused, completed);
        hud.restart(rules);
        Gdx.input.setInputProcessor(new InputMultiplexer(hud.stage, this));
        completedEffect.resize(width, height);
    }

    @Override
    public void update(float delta) {
        elapsedTime += delta;
        camera.update();


        switch (state) {
            case PLAYING:
                rules.update(delta);
                tableTop.update();
                grabbed.update(tableTop);
                dropAnimator.update(delta);

                if (!dropAnimator.isActive() && !hasBecomeAutoCompletable && rules.canAutoComplete()) {
                    hasBecomeAutoCompletable = true;
                    hud.showAutoCompleteButton();
                }

                if (rules.hasCompleted()) {
                    Statistics.instance.complete();
                    completedEffect.reset(true);
                    state = GameState.COMPLETED;
                    hud.displayCompletedDialog();
                }
                break;
            case COMPLETED:
                completed = true;
                break;
            case MENU:
                paused = true;
                break;
        }
        completedEffect.update(delta);
        hud.update(delta, rules.getGameStats(), paused || completed);
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);


        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        switch (state) {
            case PLAYING:
                tableTop.render(spriteBatch);
                grabbed.render(spriteBatch);
                dropAnimator.render(spriteBatch);
                break;
            case COMPLETED:
                tableTop.render(spriteBatch);

                break;
        }
        spriteBatch.end();

        hud.render();

        spriteBatch.begin();
        completedEffect.render(spriteBatch);
        spriteBatch.end();

        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        //tableTop.debugRender(debugRenderer);
        debugRenderer.end();
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode){
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (paused)
            return false;
        Log.info("touchDown(%d, %d, %d, %d)", screenX, screenY, pointer, button);
        tapDownStack = null;
        var position = unproject2D(camera, screenX, screenY);
        touchDownPosition.set(position);
        tapDownStack = tableTop.getStackAt(position);
        if (tapDownStack != null) {
            Vibrator.card();
            Gdx.graphics.setCursor(Assets.instance.cursors.grabbed);
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        if (paused)
            return false;

        Gdx.graphics.setCursor(Assets.instance.cursors.open);
        var position = unproject2D(camera, screenX, screenY);
        if (grabbed.hasAny()) {
            Log.info("Releasing cards");
            var cardStack = tableTop.getStackAt(position);
            if (cardStack != null) {
                var released = grabbed.release();
                if (cardStack.handleDrop(tableTop, dropAnimator, grabbed.sourceStack, released)) {
                    Log.info("Drop accepted");
                    Assets.instance.sounds.playSlide();
                }
                else
                    dropAnimator.dropOntoStack(tableTop, null, grabbed.sourceStack, released);
            }
            else
                dropAnimator.dropOntoStack(tableTop, null, grabbed.sourceStack, grabbed.release());

            return true;
        }
        else {
            if (!dropAnimator.isActive()) {
                if (tapDownStack != null && position.dst(touchDownPosition) < tableTop.cardSize.x / 4.0f) {
                    if (getElapsedTimeSinceLastTap(tapDownStack) < 0.25f) {
                        if (tapDownStack.doubleTap(tableTop, dropAnimator)) {
                            Log.info("Double-tap accepted");
                            Assets.instance.sounds.playSlide();
                            return true;
                        }
                    } else {
                        if (tapDownStack.tap(tableTop, dropAnimator)) {
                            Log.info("Tap accepted");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private float getElapsedTimeSinceLastTap(CardStack cardStack) {
        if (paused)
            return Float.MAX_VALUE;

        var elapsed = Float.MAX_VALUE;
        if(previousTapTime.containsKey(cardStack)) {
            var previousTime = previousTapTime.get(cardStack);
            elapsed = elapsedTime - previousTime;
        }
        previousTapTime.put(cardStack, elapsedTime);
        return elapsed;
    }

    @Override
    public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        if (paused)
            return false;
        var position = unproject2D(camera, screenX, screenY);
        if (grabbed.hasAny())
            grabbed.update(position);
        else
        {
            var draggedDistanceFromDown = Vector2.dst(position.x, position.y, touchDownPosition.x, touchDownPosition.y);
            if (draggedDistanceFromDown > tableTop.cardSize.x / 4) {
                if (!dropAnimator.isActive()) {
                    if (tableTop.grab((int) touchDownPosition.x, (int) touchDownPosition.y, grabbed)) {
                        grabbed.update(position);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return false;
    }
}
