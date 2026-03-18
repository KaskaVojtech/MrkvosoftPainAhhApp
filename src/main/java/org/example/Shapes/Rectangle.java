package org.example.Shapes;

import org.example.Structs.Fill;
import org.example.Structs.Outline;
import org.example.Structs.Point;

import java.util.List;

public class Rectangle extends LineBasedShape{

    public Rectangle(List<Point> pointList, Outline outline, Fill fill) {
        super(pointList, outline, fill);
    }

    @Override
    public void Recalculate() {
        this.boundingBox = calculateBbox(this.points);
    }
}
