package com.bornander.klondike.libgdx;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

public class Log {
    private final static String TAG = "klondike";

    private static boolean isLevelEnabled(int logLevel) {
        return logLevel <= Gdx.app.getLogLevel();
    }

    public static void debug(String format, Object...params) {
        if (isLevelEnabled(Application.LOG_DEBUG))
            Gdx.app.debug(TAG, String.format(format, params));
    }

    public static void debug(boolean enabled, String format, Object...params) {
        if (enabled)
            debug(format, params);
    }

    public static void debug(Object value) {
        if (isLevelEnabled(Application.LOG_DEBUG))
            debug("%s", value);
    }

    public static void debug(boolean enabled, Object value) {
        if (enabled)
            debug(value);
    }

    public static void info(String format, Object...params) {
        if (isLevelEnabled(Application.LOG_INFO))
            Gdx.app.log(TAG, String.format(format, params));
    }

    public static void info(boolean enabled, String format, Object...params) {
        if (enabled)
            info(format, params);
    }

    public static void info(Object value) {
        if (isLevelEnabled(Application.LOG_INFO))
            info("%s", value);
    }

    public static void info(boolean enabled, Object value) {
        if (enabled)
            info(value);
    }

    public static void error(String format, Object...params) {
        if (isLevelEnabled(Application.LOG_ERROR))
            Gdx.app.error(TAG, String.format(format, params));
    }

    public static void error(Throwable exception, String format, Object...params) {
        if (isLevelEnabled(Application.LOG_ERROR))
            Gdx.app.error(TAG, String.format(format, params), exception);
    }
}
