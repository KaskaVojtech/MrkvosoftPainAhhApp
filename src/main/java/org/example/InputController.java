package org.example;

import org.example.Structs.InputState;
import org.example.Structs.Point;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InputController {

    private volatile boolean shiftPressed = false;
    private volatile boolean enterJustPressed = false;
    private volatile boolean cJustPressed = false;
    private volatile boolean rPressed = false;

    private volatile boolean mouseLeftHolding = false;
    private volatile boolean mouseLeftJustPressed = false;
    private volatile boolean mouseLeftReleased = false;

    private volatile Point cursorPoint = new Point(0, 0);

    public InputController(Component component) {

        component.setFocusable(true);
        component.requestFocusInWindow();

        component.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> shiftPressed = true;
                    case KeyEvent.VK_ENTER -> enterJustPressed = true;
                    case KeyEvent.VK_C     -> cJustPressed = true;
                    case KeyEvent.VK_R     -> rPressed = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_SHIFT -> shiftPressed = false;
                    case KeyEvent.VK_R     -> rPressed = false;
                }
            }
        });

        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mouseLeftHolding = true;
                    mouseLeftJustPressed = true;
                    mouseLeftReleased = false;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    mouseLeftHolding = false;
                    mouseLeftReleased = true;
                }
            }
        });

        component.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                cursorPoint = new Point(e.getPoint().x, e.getPoint().y);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                cursorPoint = new Point(e.getPoint().x, e.getPoint().y);
            }
        });
    }

    public InputState getCurrentState() {
        InputState state = new InputState(
                shiftPressed,
                enterJustPressed,
                mouseLeftJustPressed,
                mouseLeftReleased,
                mouseLeftHolding,
                rPressed,
                cursorPoint,
                cJustPressed
        );
        mouseLeftJustPressed = false;
        mouseLeftReleased = false;
        enterJustPressed = false;
        cJustPressed = false;
        return state;
    }
}