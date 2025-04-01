package com.bornander.klondike.libgdx;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraExtensions {

    private final static Vector3 screenCoordinates = new Vector3();
    private final static Vector2 unprojected = new Vector2();

    public static Vector2 unproject2D(Camera camera, int screenX, int screenY) {
        camera.unproject(screenCoordinates.set(screenX, screenY, 1.0f));
        return unprojected.set(screenCoordinates.x, screenCoordinates.y);
    }

}
