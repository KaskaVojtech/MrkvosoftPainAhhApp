package org.example.Shapes;

import org.example.Structs.Fill;
import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.util.List;

public class Triangle extends LineBasedShape {

    public Triangle(List<Point> pointList, Outline outline, Fill fill) {
        super(pointList, outline, fill);
    }

    @Override
    public void Recalculate() {
        this.boundingBox = calculateBbox(this.points);
    }

    @Override
    public boolean Contains(Point point, int OFFSET) {
        if (!boundingBox.containsPoint(point)) return false;

        Point a = points.get(0);
        Point b = points.get(1);
        Point c = points.get(2);

        float d1 = sign(point, a, b);
        float d2 = sign(point, b, c);
        float d3 = sign(point, c, a);

        boolean hasNeg = (d1 < -OFFSET) || (d2 < -OFFSET) || (d3 < -OFFSET);
        boolean hasPos = (d1 > OFFSET)  || (d2 > OFFSET)  || (d3 > OFFSET);

        return !(hasNeg && hasPos);
    }

    private float sign(Point p, Point a, Point b) {
        return (p.x() - b.x()) * (a.y() - b.y()) -
                (a.x() - b.x()) * (p.y() - b.y());
    }
}


