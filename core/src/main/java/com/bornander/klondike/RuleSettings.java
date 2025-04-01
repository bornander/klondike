package com.bornander.klondike;

import com.badlogic.gdx.utils.GdxRuntimeException;

public class RuleSettings {
    public int draw = 3;
    public int turns = -1;

    public void stepDraw() {
        switch (draw) {
            case  1: draw = 3; break;
            case  3: draw = 1; break;
            default:
                throw new GdxRuntimeException("bad case");
        }
    }

    public void stepTurns() {
        switch (turns) {
            case -1: turns = 1; break;
            case  1: turns = 3; break;
            case  3: turns =-1; break;
            default:
                throw new GdxRuntimeException("bad case");
        }
    }

    public String getDrawText() {
        switch (draw) {
            case 1: return "ONE";
            case 3: return "THREE";
            default:
                throw new GdxRuntimeException("bad case");
        }
    }

    public String getTurnsText() {
        switch (turns) {
            case -1: return "INFINITE";
            case 1: return "ONE";
            case 3: return "THREE";
            default:
                throw new GdxRuntimeException("bad case");
        }
    }

    public String getDifficultyText() {
        if (draw == 1 && turns == -1)
            return "EASIEST";
        if (draw == 3 && turns == -1)
            return "EASY";

        if (draw == 1 && turns == 3)
            return "MODERATE";
        if (draw == 3 && turns == 3)
            return "TRICKY";

        if (draw == 1 && turns == 1)
            return "HARD";
        if (draw == 3 && turns == 1)
            return "HARDEST";

        throw new GdxRuntimeException("bad case");
    }

    public KlondikeRules getRules() {
        return new KlondikeRules(draw, turns);
    }

    public String[] getDifficulties() {
        return new String[] {
            "EASIEST",
            "EASY",
            "MODERATE",
            "TRICKY",
            "HARD",
            "HARDEST"
        };
    }
}

