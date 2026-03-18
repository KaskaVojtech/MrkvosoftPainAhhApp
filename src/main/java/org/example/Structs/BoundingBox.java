package org.example.Structs;

public record BoundingBox(Point leftTop, Point rightBottom) {

    public BoundingBox {
        int minX = Math.min(leftTop.x(), rightBottom.x());
        int minY = Math.min(leftTop.y(), rightBottom.y());
        int maxX = Math.max(leftTop.x(), rightBottom.x());
        int maxY = Math.max(leftTop.y(), rightBottom.y());

        leftTop = new Point(minX, minY);
        rightBottom = new Point(maxX, maxY);
    }

    public int width() {
        return rightBottom.x() - leftTop.x();
    }

    public int height() {
        return rightBottom.y() - leftTop.y();
    }

    public Point center() {
        return new Point(
                leftTop.x() + width() / 2,
                leftTop.y() + height() / 2
        );
    }

    public boolean containsPoint(Point point) {
        return point.x() >= leftTop.x() &&
                point.x() <= rightBottom.x() &&
                point.y() >= leftTop.y() &&
                point.y() <= rightBottom.y();
    }
}