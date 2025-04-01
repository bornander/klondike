package com.bornander.klondike.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.utils.Disposable;

import static com.bornander.klondike.Assets.loadPixmap;
import static com.bornander.klondike.Assets.safeDispose;

public class CursorAssets implements Disposable {
    public Cursor open;
    public Cursor grabbed;
    public Cursor point;

    public CursorAssets() {
        open = Gdx.graphics.newCursor(loadPixmap("graphics/cursors/hand_open.png"), 16, 13);
        grabbed = Gdx.graphics.newCursor(loadPixmap("graphics/cursors/hand_closed.png"), 32, 32);
        point = Gdx.graphics.newCursor(loadPixmap("graphics/cursors/hand_point.png"), 15, 10);
    }

    @Override
    public void dispose() {
        open = safeDispose(open);
        grabbed = safeDispose(grabbed);
        point = safeDispose(point);
    }
}
