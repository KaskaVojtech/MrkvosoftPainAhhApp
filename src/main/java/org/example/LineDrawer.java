package org.example;

import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.awt.*;


public class LineDrawer {

    public static void drawLine(Canvas canvas, Point a, Point b, Outline outline) {
        switch (outline.lineType()) {
            case SOLID  -> drawPatterned(canvas, a, b, outline.color(), outline.weight(), 1, 0);
            case DASHED -> drawPatterned(canvas, a, b, outline.color(), outline.weight(), 8, 4 + outline.weight());
            case DOTTED -> drawPatterned(canvas, a, b, outline.color(), outline.weight(), 2, 4 + outline.weight());
        }
    }

    // -------------------------------------------------------------------------
    // Core
    // -------------------------------------------------------------------------

    private static void drawPatterned(Canvas canvas, Point a, Point b, Color color, int weight, int dashLen, int gapLen) {
        int x0 = a.x(), y0 = a.y();
        int x1 = b.x(), y1 = b.y();

        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        int patternLen = dashLen + gapLen;
        int step = 0;

        while (true) {
            boolean inDash = (gapLen == 0) || (step % patternLen < dashLen);

            if (inDash) {
                paintThick(canvas, x0, y0, color, weight);
            }

            if (x0 == x1 && y0 == y1) break;

            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 <  dx) { err += dx; y0 += sy; }
            step++;
        }
    }

    // -------------------------------------------------------------------------
    // Thickness
    // -------------------------------------------------------------------------

    private static void paintThick(Canvas canvas, int x, int y, Color color, int weight) {
        int half = weight / 2;
        for (int dy = -half; dy <= half; dy++) {
            for (int dx = -half; dx <= half; dx++) {
                canvas.setPixel(x + dx, y + dy, color);
            }
        }
    }
}

