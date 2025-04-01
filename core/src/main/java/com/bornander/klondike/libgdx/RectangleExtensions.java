package com.bornander.klondike.libgdx;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class RectangleExtensions {

    private final static Vector2 center = new Vector2();

    public static Rectangle centerIn(Rectangle target, Rectangle bounds, int align) {

        target.setCenter(bounds.getCenter(center));
        if (Align.isTop(align))
            target.y = bounds.y + bounds.height - target.height;
        return target;
    }
}
