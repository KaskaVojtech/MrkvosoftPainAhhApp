package org.example.Shapes;

import org.example.Canvas;
import org.example.LineDrawer;
import org.example.Structs.Fill;
import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.util.List;

public abstract class LineBasedShape extends Shape {
    public LineBasedShape(List<Point> pointList, Outline outline, Fill fill) {
        super(pointList, outline, fill);
    }

    @Override
    protected void drawOutline(Canvas canvas) {
        for (int i = 0; i < points.size(); i++) {
            Point a = points.get(i);
            Point b = points.get((i + 1) % points.size());
            LineDrawer.drawLine(canvas, a, b, outline);
        }
    }

    @Override
    public boolean Contains(Point point, int offset) {
        int n = points.size();
        boolean inside = false;

        int x = point.x(), y = point.y();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            int xi = points.get(i).x(), yi = points.get(i).y();
            int xj = points.get(j).x(), yj = points.get(j).y();

            boolean intersects = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (double)(yj - yi) + xi);

            if (intersects) inside = !inside;
        }

        if (!inside && offset > 0) {
            for (int i = 0, j = n - 1; i < n; j = i++) {
                if (distanceToSegment(point, points.get(j), points.get(i)) <= offset) {
                    return true;
                }
            }
        }

        return inside;
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
