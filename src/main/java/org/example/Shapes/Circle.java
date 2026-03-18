package org.example.Shapes;

import org.example.Canvas;
import org.example.EllipseDrawer;
import org.example.Structs.BoundingBox;
import org.example.Structs.Fill;
import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.awt.*;
import java.util.List;
import java.util.Stack;

public class Circle extends Shape {

    public Circle(List<Point> pointList, Outline outline, Fill fill) {
        super(pointList, outline, fill);
        Recalculate();
    }


    private int radiusX() {
        return Math.abs(points.get(1).x() - points.get(0).x());
    }

    private int radiusY() {
        return Math.abs(points.get(2).y() - points.get(0).y());
    }

    @Override
    public void Recalculate() {
        Point center = points.get(0);
        int rx = radiusX();
        int ry = radiusY();

        this.boundingBox = new BoundingBox(
                new Point(center.x() - rx, center.y() - ry),
                new Point(center.x() + rx, center.y() + ry)
        );
    }

    @Override
    public boolean Contains(Point point, int offset) {
        Point center = points.get(0);
        int rx = radiusX() + offset;
        int ry = radiusY() + offset;

        double dx = point.x() - center.x();
        double dy = point.y() - center.y();
        return (dx * dx) / (rx * rx) + (dy * dy) / (ry * ry) <= 1.0;
    }

    @Override
    protected void drawOutline(Canvas canvas) {
        EllipseDrawer.drawEllipse(canvas, points.get(0), radiusX(), radiusY(), outline);
    }
}