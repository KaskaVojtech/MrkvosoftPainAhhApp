package org.example.Structs;

public record InputState(
        boolean shiftPressed,
        boolean enterPressed,
        boolean mouseLeftPressed,
        boolean mouseLeftReleased,
        boolean mouseLeftHolding,
        boolean rPressed,
        Point cursorPoint,
        boolean cPressed
) {}
