package org.example.Shapes.Geometries.DataClasses;

import org.example.Structs.BoundingBox;
import org.example.Structs.Point;

import java.util.List;

public abstract class Geometry {
    public List<Point> points;
    public BoundingBox boundingBox;
    public boolean hasChanged;

    public Geometry(List<Point> points){
        this.hasChanged = true;
        this.points = points;
        this.boundingBox = calculateBbox(points);
    }

    abstract public void Recalculate();
    abstract public boolean Contains(Point point, int OFFSET);

    public boolean BboxContains(Point point){
        return boundingBox.containsPoint(point);
    }

    public int GetNearestPointIndex(Point point) {
        int nearestIndex = 0;
        double minDistance = distance(point, points.getFirst());

        for (int i = 1; i < points.size(); i++) {
            double dist = distance(point, points.get(i));
            if (dist < minDistance) {
                minDistance = dist;
                nearestIndex = i;
            }
        }
        return nearestIndex;
    }

    public void MovePoint(int index, Point newPos){
        if (index >= 0 && index < points.size()) {
            points.set(index, newPos);
            Recalculate();
        }
    }

    public void MoveBy(Point delta){
        for(int i = 0; i < points.size(); i++){
            Point p = points.get(i);

            points.set(i, new Point(
                    p.x()+delta.x(),
                    p.y()+delta.y()
            ));
        }

        Recalculate();
    }

    public Point GetCenter() {
        return boundingBox.center();
    }

    public double distance(Point a, Point b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return Math.sqrt(dx * dx + dy * dy);
    }

    protected BoundingBox calculateBbox(List<Point> points) {
        int minX = points.stream().mapToInt(Point::x).min().orElse(0);
        int minY = points.stream().mapToInt(Point::y).min().orElse(0);
        int maxX = points.stream().mapToInt(Point::x).max().orElse(0);
        int maxY = points.stream().mapToInt(Point::y).max().orElse(0);
        return new BoundingBox(new Point(minX, minY), new Point(maxX, maxY));
    }

    protected Point interiorPoint() {
        return boundingBox.center();
    }
}
