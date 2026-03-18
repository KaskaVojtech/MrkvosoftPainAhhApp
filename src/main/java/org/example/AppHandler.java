package org.example;

import org.example.Shapes.*;
import org.example.Structs.*;

import java.util.ArrayList;
import java.util.List;

public class AppHandler {

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private final Canvas mainCanvas;
    private final Canvas tempCanvas;
    private final CanvasRenderer renderer;
    private final Toolbar toolbar;
    private final InputController inputController;

    // -------------------------------------------------------------------------
    // Toolbar state
    // -------------------------------------------------------------------------

    private ToolbarState toolbarState;

    // -------------------------------------------------------------------------
    // Session helpers – WHITE_ARROW
    // -------------------------------------------------------------------------

    private Shape movingShape = null;
    private Point movingShapeOriginalPos = null;

    // -------------------------------------------------------------------------
    // Session helpers – BLACK_ARROW
    // -------------------------------------------------------------------------

    private Shape editingShape = null;
    private int editingShapePointIndex = -1;

    // -------------------------------------------------------------------------
    // Session helpers – bbox shapes (RECTANGLE / TRIANGLE / ELLIPSE)
    // -------------------------------------------------------------------------

    private Point helperBboxLeftCorner = null;

    // -------------------------------------------------------------------------
    // Session helpers – POLYGON
    // -------------------------------------------------------------------------

    private final List<Point> polygonHelperArray = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Line
    // -------------------------------------------------------------------------
    private  Point lineStart = null;
    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final int WHITE_ARROW_OFFSET = 3;
    private static final int BLACK_ARROW_OFFSET = 0;
    private static final int BLACK_ARROW_POINT_MAX_DISTANCE = 30;
    private static final long TARGET_TICK_MS = 1000 / 60;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public AppHandler(Canvas mainCanvas, Canvas tempCanvas,
                      CanvasRenderer renderer, Toolbar toolbar,
                      InputController inputController) {
        this.mainCanvas = mainCanvas;
        this.tempCanvas = tempCanvas;
        this.renderer = renderer;
        this.toolbar = toolbar;
        this.inputController = inputController;

        // Load initial toolbar state
        this.toolbarState = toolbar.getState();
    }

    // -------------------------------------------------------------------------
    // Main loop
    // -------------------------------------------------------------------------

    public void start() {
        Thread loopThread = new Thread(() -> {
            while (true) {
                long frameStart = System.currentTimeMillis();
                tick();
                long elapsed = System.currentTimeMillis() - frameStart;
                long sleep = TARGET_TICK_MS - elapsed;
                if (sleep > 0) {
                    try { Thread.sleep(sleep); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                }
            }
        });
        loopThread.setDaemon(true);
        loopThread.start();
    }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    private void tick() {
        // 1. Toolbar change check
        if (toolbar.hasChanged) {
            toolbarState = toolbar.getState();
            System.out.println(toolbarState);
            resetSession();
        }

        InputState input = inputController.getCurrentState();
        System.out.println(input);

        // 2. Global shortcut – C key clears canvas
        if (input.cPressed()) {
            handleClear();
        }

        // 3. Dispatch by mode
        switch (toolbarState.mode()) {
            case WHITE_ARROW  -> handleWhiteArrow(input);
            case BLACK_ARROW  -> handleBlackArrow(input);
            case FILL_BUCKET  -> handleFillBucket(input);
            case ERASE        -> handleErase(input);
            case CLEAR        -> handleClear();
            case LINE -> handleLine(input);
            case RECTANGLE    -> handleBboxShape(input, Mode.RECTANGLE);
            case TRIANGLE     -> handleBboxShape(input, Mode.TRIANGLE);
            case ELLIPSE      -> handleBboxShape(input, Mode.ELLIPSE);
            case POLYGON      -> handlePolygon(input);
        }

        // 4. Redraw & render
        mainCanvas.redrawAll();
        renderer.refresh();
    }

    // -------------------------------------------------------------------------
    // Mode handlers
    // -------------------------------------------------------------------------

    private void handleClear() {
        mainCanvas.clear();
        mainCanvas.shapes.clear();
        tempCanvas.clear();
        tempCanvas.shapes.clear();
        resetSession();
    }

    // --- WHITE ARROW ---------------------------------------------------------

    private void handleWhiteArrow(InputState input) {
        if (input.mouseLeftPressed() && movingShape == null) {
            Shape hit = topShapeAt(input.cursorPoint(), WHITE_ARROW_OFFSET);
            if (hit != null) {
                movingShape = hit;
                movingShapeOriginalPos = input.cursorPoint();
            }
        }

        if (input.mouseLeftHolding() && movingShape != null) {
            Point delta = delta(movingShapeOriginalPos, input.cursorPoint());
            tempCanvas.clear();
            tempCanvas.shapes.clear();

            for (Shape s : mainCanvas.getShapes()) {
                if (s != movingShape) {
                    s.Draw(tempCanvas);
                }
            }

            movingShape.MoveBy(delta);
            mainCanvas.markDirty();
            movingShapeOriginalPos = input.cursorPoint();
            movingShape.Draw(tempCanvas);
        }

        if (input.mouseLeftReleased() && movingShape != null) {
            mainCanvas.composite(tempCanvas);
            movingShape = null;
            movingShapeOriginalPos = null;
            tempCanvas.clear();
            tempCanvas.shapes.clear();
        }
    }

    // --- BLACK ARROW ---------------------------------------------------------

    private void handleBlackArrow(InputState input) {
        if (input.mouseLeftPressed() && editingShape == null) {
            Shape hit = topShapeAt(input.cursorPoint(), BLACK_ARROW_OFFSET);
            if (hit != null) {
                int idx = hit.GetNearestPointIndex(input.cursorPoint());
                double dist = hit.distance(input.cursorPoint(), hit.points.get(idx));
                if (dist <= BLACK_ARROW_POINT_MAX_DISTANCE) {
                    editingShape = hit;
                    editingShapePointIndex = idx;
                }
            }
        }

        if (input.mouseLeftHolding() && editingShape != null && editingShapePointIndex >= 0) {
            tempCanvas.clear();
            tempCanvas.shapes.clear();

            for (Shape s : mainCanvas.getShapes()) {
                if (s != editingShape) {
                    s.Draw(tempCanvas);
                }
            }

            editingShape.MovePoint(editingShapePointIndex, input.cursorPoint());
            mainCanvas.markDirty();
            editingShape.Draw(tempCanvas);
        }

        if (input.mouseLeftReleased() && editingShape != null) {
            mainCanvas.composite(tempCanvas);
            editingShape = null;
            editingShapePointIndex = -1;
            tempCanvas.clear();
            tempCanvas.shapes.clear();
        }
    }

    // --- FILL BUCKET ---------------------------------------------------------

    private void handleFillBucket(InputState input) {
        if (!input.mouseLeftPressed()) return;

        Shape hit = topShapeAt(input.cursorPoint(), 0);
        if (hit != null) {
            hit.SetFill(toolbarState.fill().color());
            mainCanvas.markDirty();
        }
    }

    // --- ERASE ---------------------------------------------------------------

    private void handleErase(InputState input) {
        if (!input.mouseLeftPressed()) return;

        Shape hit = topShapeAt(input.cursorPoint(), 0);
        if (hit != null) {
            mainCanvas.removeShape(hit);
        }
    }

    // --- BBOX SHAPES (Rectangle / Triangle / Ellipse) ------------------------

    private void handleBboxShape(InputState input, Mode mode) {
        if (input.mouseLeftPressed()) {
            helperBboxLeftCorner = input.cursorPoint();
        }

        if (input.mouseLeftHolding() && helperBboxLeftCorner != null) {
            BoundingBox bbox = new BoundingBox(helperBboxLeftCorner, input.cursorPoint());
            Outline outline = currentOutline();
            Fill fill = toolbarState.fill();

            Shape preview = buildBboxShape(mode, bbox, outline, fill, input.shiftPressed());

            tempCanvas.clear();
            tempCanvas.shapes.clear();
            if (preview != null) {
                tempCanvas.addShape(preview);
                tempCanvas.redrawAll();
            }
        }

        if (input.mouseLeftReleased() && helperBboxLeftCorner != null) {
            BoundingBox bbox = new BoundingBox(helperBboxLeftCorner, input.cursorPoint());
            Outline outline = currentOutline();
            Fill fill = toolbarState.fill();

            Shape finalShape = buildBboxShape(mode, bbox, outline, fill, input.shiftPressed());
            if (finalShape != null) {
                mainCanvas.addShape(finalShape);
            }

            tempCanvas.clear();
            tempCanvas.shapes.clear();
            helperBboxLeftCorner = null;
        }
    }

    private Shape buildBboxShape(Mode mode, BoundingBox bbox, Outline outline, Fill fill, boolean shift) {
        return switch (mode) {
            case RECTANGLE -> shift
                    ? BboxToShapeMaker.bboxToSquare(bbox, outline, fill)
                    : BboxToShapeMaker.bboxToRect(bbox, outline, fill);
            case TRIANGLE  -> shift
                    ? BboxToShapeMaker.bboxToRightAngledTriangle(bbox, outline, fill)
                    : BboxToShapeMaker.bboxToTriangle(bbox, outline, fill);
            case ELLIPSE   -> shift
                    ? BboxToShapeMaker.bboxToCircle(bbox, outline, fill)
                    : BboxToShapeMaker.bboxToEllipse(bbox, outline, fill);
            default -> null;
        };
    }

    // --- POLYGON -------------------------------------------------------------

    private void handlePolygon(InputState input) {
        if (input.mouseLeftPressed()) {
            polygonHelperArray.add(input.cursorPoint());

            if (polygonHelperArray.size() >= 3) {
                Polygon preview = new Polygon(
                        new ArrayList<>(polygonHelperArray),
                        currentOutline(),
                        toolbarState.fill()
                );
                tempCanvas.clear();
                tempCanvas.shapes.clear();
                tempCanvas.addShape(preview);
                tempCanvas.redrawAll();
            }
        }

        if (input.enterPressed() && polygonHelperArray.size() >= 3) {
            Polygon finalPolygon = new Polygon(
                    new ArrayList<>(polygonHelperArray),
                    currentOutline(),
                    toolbarState.fill()
            );
            mainCanvas.addShape(finalPolygon);
            mainCanvas.composite(tempCanvas);

            tempCanvas.clear();
            tempCanvas.shapes.clear();
            polygonHelperArray.clear();
        }
    }

    // --- LINE -------------------------------------------------------------

    private void handleLine(InputState input) {
        if (input.mouseLeftPressed()) {
            lineStart = input.cursorPoint();
        }

        if (input.mouseLeftHolding() && lineStart != null) {
            Point end = input.shiftPressed() ? snapTo45(lineStart, input.cursorPoint()) : input.cursorPoint();

            Line preview = new Line(
                    new ArrayList<>(List.of(lineStart, end)),
                    currentOutline()
            );

            tempCanvas.clear();
            tempCanvas.shapes.clear();
            tempCanvas.addShape(preview);
            tempCanvas.redrawAll();
        }

        if (input.mouseLeftReleased() && lineStart != null) {
            Point end = input.shiftPressed() ? snapTo45(lineStart, input.cursorPoint()) : input.cursorPoint();

            Line finalLine = new Line(
                    new ArrayList<>(List.of(lineStart, end)),
                    currentOutline()
            );
            mainCanvas.addShape(finalLine);

            tempCanvas.clear();
            tempCanvas.shapes.clear();
            lineStart = null;
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Shape topShapeAt(Point point, int offset) {
        List<Shape> shapes = mainCanvas.getShapes();
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape s = shapes.get(i);
            if (s.BboxContains(point) && s.Contains(point, offset)) {
                return s;
            }
        }
        return null;
    }

    private Outline currentOutline() {
        return new Outline(
                toolbarState.outlineColor(),
                toolbarState.lineType(),
                toolbarState.weight()
        );
    }

    private Point delta(Point from, Point to) {
        return new Point(to.x() - from.x(), to.y() - from.y());
    }

    private void resetSession() {
        movingShape = null;
        movingShapeOriginalPos = null;
        editingShape = null;
        editingShapePointIndex = -1;
        helperBboxLeftCorner = null;
        polygonHelperArray.clear();
        tempCanvas.clear();
        tempCanvas.shapes.clear();
        lineStart = null;
    }

    private Point snapTo45(Point from, Point to) {
        int dx = to.x() - from.x();
        int dy = to.y() - from.y();
        double angle = Math.toDegrees(Math.atan2(dy, dx));
        double snapped = Math.round(angle / 45.0) * 45.0;
        double rad = Math.toRadians(snapped);
        int len = (int) Math.round(Math.sqrt(dx * dx + dy * dy));
        return new Point(from.x() + (int) Math.round(len * Math.cos(rad)),
                from.y() + (int) Math.round(len * Math.sin(rad)));
    }
}