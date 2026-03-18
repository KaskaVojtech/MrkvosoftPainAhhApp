package org.example;

import javax.swing.*;
import java.awt.*;

public class CanvasRenderer extends JPanel
{

    private final Canvas main;
    private final Canvas temp;

    public CanvasRenderer(Canvas canvas, Canvas temp) {
        this.main = canvas;
        this.temp = temp;
        setPreferredSize(new Dimension(canvas.width, canvas.height));
    }


    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCanvas(g, main);
        drawCanvas(g, temp);
    }

    private void drawCanvas(Graphics g, Canvas canvas) {
        for (int y = 0; y < canvas.height; y++)
            for (int x = 0; x < canvas.width; x++) {
                Color c = canvas.pixels[y][x];
                if (c != null) { g.setColor(c); g.fillRect(x, y, 1, 1); }
            }
    }
}
