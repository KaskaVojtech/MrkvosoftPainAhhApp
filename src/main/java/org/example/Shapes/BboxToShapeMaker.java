package org.example.Shapes;

import org.example.Structs.*;

import java.util.ArrayList;
import java.util.List;

public class BboxToShapeMaker {

    public static Rectangle bboxToRect(BoundingBox bbox, Outline outline, Fill fill) {
        Point lt = bbox.leftTop();
        Point rb = bbox.rightBottom();
        List<Point> points = new ArrayList<>(List.of(
                lt,
                new Point(rb.x(), lt.y()),
                rb,
                new Point(lt.x(), rb.y())
        ));
        return new Rectangle(points, outline, fill);
    }

    public static Rectangle bboxToSquare(BoundingBox bbox, Outline outline, Fill fill) {
        Point lt = bbox.leftTop();
        int size = Math.min(bbox.width(), bbox.height());
        List<Point> points = new ArrayList<>(List.of(
                lt,
                new Point(lt.x() + size, lt.y()),
                new Point(lt.x() + size, lt.y() + size),
                new Point(lt.x(), lt.y() + size)
        ));
        return new Rectangle(points, outline, fill);
    }

    public static Triangle bboxToTriangle(BoundingBox bbox, Outline outline, Fill fill) {
        Point lt = bbox.leftTop();
        Point rb = bbox.rightBottom();
        int midX = bbox.center().x();
        List<Point> points = new ArrayList<>(List.of(
                new Point(midX, lt.y()),
                rb,
                new Point(lt.x(), rb.y())
        ));
        return new Triangle(points, outline, fill);
    }

    public static Triangle bboxToRightAngledTriangle(BoundingBox bbox, Outline outline, Fill fill) {
        Point lt = bbox.leftTop();
        Point rb = bbox.rightBottom();
        List<Point> points = new ArrayList<>(List.of(
                lt,
                new Point(rb.x(), lt.y()),
                new Point(lt.x(), rb.y())
        ));
        return new Triangle(points, outline, fill);
    }

    public static Circle bboxToEllipse(BoundingBox bbox, Outline outline, Fill fill) {
        Point center = bbox.center();
        int radiusX = bbox.width() / 2;
        int radiusY = bbox.height() / 2;
        List<Point> points = new ArrayList<>(List.of(
                center,
                new Point(center.x() + radiusX, center.y()),
                new Point(center.x(), center.y() + radiusY)
        ));
        return new Circle(points, outline, fill);
    }

    public static Circle bboxToCircle(BoundingBox bbox, Outline outline, Fill fill) {
        Point center = bbox.center();
        int radius = Math.min(bbox.width() / 2, bbox.height() / 2);
        List<Point> points = new ArrayList<>(List.of(
                center,
                new Point(center.x() + radius, center.y()),
                new Point(center.x(), center.y() + radius)
        ));
        return new Circle(points, outline, fill);
    }
}
