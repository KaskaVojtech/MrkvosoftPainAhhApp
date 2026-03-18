package org.example;

import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.awt.*;

public class EllipseDrawer {

    public static void drawEllipse(Canvas canvas, Point center, int rx, int ry, Outline outline) {
        int dashLen, gapLen;
        switch (outline.lineType()) {
            case DASHED -> { dashLen = 8; gapLen = 4 + outline.weight(); }
            case DOTTED -> { dashLen = 2; gapLen = 4 + outline.weight(); }
            default     -> { dashLen = 1; gapLen = 0; }
        }

        drawPatterned(canvas, center, rx, ry, outline.color(), outline.weight(), dashLen, gapLen);
    }
    // -------------------------------------------------------------------------
    // Core – Bresenham s pattern čítačem
    // -------------------------------------------------------------------------

    private static void drawPatterned(Canvas canvas, Point center,
                                      int rx, int ry, Color color, int weight,
                                      int dashLen, int gapLen) {
        if (rx <= 0 || ry <= 0) return;

        int patternLen = dashLen + gapLen;
        int step = 0;

        int x = 0, y = ry;
        double d1 = (ry * ry) - (rx * rx * ry) + (0.25 * rx * rx);
        double dx = 2.0 * ry * ry * x;
        double dy = 2.0 * rx * rx * y;

        while (dx < dy) {
            paintPoints(canvas, center, x, y, color, step, patternLen, dashLen, gapLen, weight);
            if (d1 < 0) {
                x++;
                dx += 2.0 * ry * ry;
                d1 += dx + ry * ry;
            } else {
                x++;
                y--;
                dx += 2.0 * ry * ry;
                dy -= 2.0 * rx * rx;
                d1 += dx - dy + ry * ry;
            }
            step++;
        }

        double d2 = (ry * ry) * ((x + 0.5) * (x + 0.5))
                + (rx * rx) * ((y - 1.0) * (y - 1.0))
                - (rx * rx * ry * ry);

        while (y >= 0) {
            paintPoints(canvas, center, x, y, color, step, patternLen, dashLen, gapLen, weight);
            if (d2 > 0) {
                y--;
                dy -= 2.0 * rx * rx;
                d2 += rx * rx - dy;
            } else {
                y--;
                x++;
                dx += 2.0 * ry * ry;
                dy -= 2.0 * rx * rx;
                d2 += dx - dy + rx * rx;
            }
            step++;
        }
    }

    private static void paintPoints(Canvas canvas, Point c, int x, int y,
                                    Color color, int step,
                                    int patternLen, int dashLen, int gapLen,
                                    int weight) {
        boolean visible = (gapLen == 0) || (step % patternLen < dashLen);
        if (!visible) return;

        int half = weight / 2;
        for (int oy = -half; oy <= half; oy++) {
            for (int ox = -half; ox <= half; ox++) {
                canvas.setPixel(c.x() + x + ox, c.y() + y + oy, color);
                canvas.setPixel(c.x() - x + ox, c.y() + y + oy, color);
                canvas.setPixel(c.x() + x + ox, c.y() - y + oy, color);
                canvas.setPixel(c.x() - x + ox, c.y() - y + oy, color);
            }
        }
    }
}
