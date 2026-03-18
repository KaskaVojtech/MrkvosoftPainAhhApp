package org.example.Shapes;

import org.example.Canvas;
import org.example.Shapes.Geometries.DataClasses.Geometry;
import org.example.Structs.*;
import org.example.Structs.Point;

import java.awt.*;
import java.util.List;
import java.util.Stack;


public abstract class Shape extends Geometry {

    Outline outline;
    Fill fill;

    public Shape(List<Point> pointList, Outline outline, Fill fill){
        super(pointList);

        this.outline = outline;
        this.fill = fill;
    }

    protected abstract void drawOutline(Canvas canvas);

    protected void fillGeometry(Canvas canvas) {
        for (int y = boundingBox.leftTop().y(); y <= boundingBox.rightBottom().y(); y++) {
            for (int x = boundingBox.leftTop().x(); x <= boundingBox.rightBottom().x(); x++) {
                Point p = new Point(x, y);
                if (Contains(p, 0)) {
                    canvas.setPixel(x, y, fill.color());
                }
            }
        }
    }


    public void Draw(Canvas canvas) {
        drawOutline(canvas);
        if (fill.active()) {
            fillGeometry(canvas);
        }
    }

    public void Erase(Canvas canvas) {
        int offset = outline.weight();
        for (int y = boundingBox.leftTop().y() - offset; y <= boundingBox.rightBottom().y() + offset; y++) {
            for (int x = boundingBox.leftTop().x() - offset; x <= boundingBox.rightBottom().x() + offset; x++) {
                Point p = new Point(x, y);
                if (Contains(p, offset)) {
                    canvas.setPixel(x, y, null);
                }
            }
        }
    }

    public void SetFill(Color color){
        fill = new Fill(color, true);
        hasChanged = true;
    }

}
