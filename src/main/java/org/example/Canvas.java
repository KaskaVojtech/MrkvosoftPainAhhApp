package org.example;

import org.example.Shapes.Shape;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Canvas {

    public final int width;
    public final int height;
    public final Color[][] pixels;
    public final List<Shape> shapes;
    private boolean dirty = true;

    public void markDirty() {
        dirty = true;
    }

    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Color[height][width];
        this.shapes = new ArrayList<>();
        clear();
    }

    // -------------------------------------------------------------------------
    // Pixel operations
    // -------------------------------------------------------------------------

    public void setPixel(int x, int y, Color color) {
        if (inBounds(x, y)) {
            pixels[y][x] = color;
        }
    }

    public Color getPixel(int x, int y) {
        if (inBounds(x, y)) {
            return pixels[y][x];
        }
        return null;
    }

    public void clear() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y][x] = null;
            }
        }
        dirty = true;
    }

    // -------------------------------------------------------------------------
    // Shape operations
    // -------------------------------------------------------------------------

    public void addShape(Shape shape) {
        shapes.add(shape);
        shape.hasChanged = true;
        dirty = true;
    }

    public void removeShape(Shape shape) {
        if (shapes.remove(shape)) {
            dirty = true;
        }
        redrawAll();
    }

    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }


    public void redrawAll() {
        if (!dirty) return;
        clear();
        for (Shape shape : shapes) {
            shape.Draw(this);
            shape.hasChanged = false;
        }
        dirty = false;
    }

    public void composite(Canvas source) {
        if (source.width != this.width || source.height != this.height) {
            throw new IllegalArgumentException(
                    "Canvas dimensions must match for compositing."
            );
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color c = source.pixels[y][x];
                if (c != null) {
                    pixels[y][x] = c;
                }
            }
        }
        dirty = true;
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}