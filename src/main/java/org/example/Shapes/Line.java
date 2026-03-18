package org.example.Shapes;

import org.example.Canvas;
import org.example.LineDrawer;
import org.example.Structs.Fill;
import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.util.List;

public class Line extends Shape {

    public Line(List<Point> pointList, Outline outline) {
        super(pointList, outline, new Fill(null, false));
    }

    @Override
    public void Recalculate() {
        this.boundingBox = calculateBbox(this.points);
    }

    @Override
    public boolean Contains(Point point, int offset) {
        Point a = points.get(0);
        Point b = points.get(1);
        return distanceToSegment(point, a, b) <= Math.max(offset, outline.weight());
    }

    @Override
    protected void drawOutline(Canvas canvas) {
        LineDrawer.drawLine(canvas, points.get(0), points.get(1), outline);
    }

    private double distanceToSegment(Point p, Point a, Point b) {
        double dx = b.x() - a.x(), dy = b.y() - a.y();
        if (dx == 0 && dy == 0) {
            dx = p.x() - a.x(); dy = p.y() - a.y();
            return Math.sqrt(dx * dx + dy * dy);
        }
        double t = Math.max(0, Math.min(1, ((p.x() - a.x()) * dx + (p.y() - a.y()) * dy) / (dx * dx + dy * dy)));
        double projX = a.x() + t * dx, projY = a.y() + t * dy;
        dx = p.x() - projX; dy = p.y() - projY;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
