package org.example;

import javax.swing.*;
import java.awt.*;

public class App extends JFrame {
    private final Canvas mainCanvas;
    private final Canvas tempCanvas;
    private final CanvasRenderer MainRenderer;
    private final Toolbar toolbar;

    public App() {
        super("Mrkvosoft pain ahhh App");

        mainCanvas = new Canvas(1000, 750);
        tempCanvas = new Canvas(1000, 750);
        MainRenderer = new CanvasRenderer(mainCanvas, tempCanvas);
        toolbar = new Toolbar();
        toolbar.setOpaque(true);
        toolbar.setBackground(Color.BLACK);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(toolbar, BorderLayout.NORTH);
        toolbar.setFocusable(false);

        MainRenderer.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                MainRenderer.requestFocusInWindow();
            }
        });
        for (Component c : toolbar.getComponents()) {
            c.setFocusable(false);
        }
        toolbar.setFocusable(false);
        add(MainRenderer, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        MainRenderer.setFocusable(true);
        SwingUtilities.invokeLater(() -> MainRenderer.requestFocusInWindow());

        InputController inputController = new InputController(MainRenderer);

        AppHandler handler = new AppHandler(mainCanvas, tempCanvas, MainRenderer, toolbar, inputController);
        handler.start();
    }
}
