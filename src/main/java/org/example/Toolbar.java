package org.example;

import org.example.Structs.Fill;
import org.example.Structs.LineType;
import org.example.Structs.Mode;
import org.example.Structs.ToolbarState;

import javax.swing.*;
import java.awt.*;

public class Toolbar extends JPanel {

    // =========================
    // State
    // =========================

    private Color chosenOutlineColor = Color.BLACK;
    private LineType chosenLineType = LineType.SOLID;
    private Fill chosenFill = new Fill(null, false);
    private Mode chosenMode = Mode.WHITE_ARROW;
    private int chosenWeight = 3;

    public boolean hasChanged = false;

    // =========================
    // Constructor
    // =========================

    public Toolbar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        initUI();
    }

    private void markChanged() {
        hasChanged = true;
    }

    // =========================
    // UI
    // =========================

    private void initUI() {

        // WHITE ARROW
        JButton whiteArrow = new JButton("White Arrow");
        whiteArrow.addActionListener(e -> {
            chosenMode = Mode.WHITE_ARROW;
            markChanged();
        });
        add(whiteArrow);

        // BLACK ARROW
        JButton blackArrow = new JButton("Black Arrow");
        blackArrow.addActionListener(e -> {
            chosenMode = Mode.BLACK_ARROW;
            markChanged();
        });
        add(blackArrow);

        // FILL BUCKET
        JButton fillBucket = new JButton("Bucket");
        fillBucket.addActionListener(e -> {
            chosenMode = Mode.FILL_BUCKET;
            markChanged();
        });
        add(fillBucket);

        // ERASE VECTOR
        JButton erase = new JButton("Erase");
        erase.addActionListener(e -> {
            chosenMode = Mode.ERASE;
            markChanged();
        });
        add(erase);


        // CLEAR
        JButton clear = new JButton("Clear");
        clear.addActionListener(e -> {
            chosenMode = Mode.CLEAR;
            markChanged();
        });
        add(clear);

        // SHAPE SELECTOR
        String[] shapes = {"Line","Rectangle", "Triangle", "Ellipse", "Polygon"};
        JComboBox<String> shapeBox = new JComboBox<>(shapes);

        shapeBox.addActionListener(e -> {
            String selected = (String) shapeBox.getSelectedItem();

            switch (selected) {
                case "Line" -> chosenMode = Mode.LINE;
                case "Rectangle" -> chosenMode = Mode.RECTANGLE;
                case "Triangle" -> chosenMode = Mode.TRIANGLE;
                case "Ellipse" -> chosenMode = Mode.ELLIPSE;
                case "Polygon" -> chosenMode = Mode.POLYGON;
            }

            markChanged();
        });

        add(shapeBox);

        // OUTLINE COLOR
        JButton outlineColorBtn = new JButton("Outline Color");
        outlineColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Pick outline color", chosenOutlineColor);
            if (newColor != null) {
                chosenOutlineColor = newColor;
                markChanged();
            }
        });
        add(outlineColorBtn);

        // FILL COLOR
        JButton fillColorBtn = new JButton("Fill Color");
        fillColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Pick fill color", chosenFill.color());
            if (newColor != null) {
                chosenFill = new Fill(newColor, true);
                markChanged();
            }
        });
        add(fillColorBtn);

        // LINE TYPE
        JComboBox<LineType> lineTypeBox = new JComboBox<>(LineType.values());
        lineTypeBox.setSelectedItem(LineType.SOLID);

        lineTypeBox.addActionListener(e -> {
            chosenLineType = (LineType) lineTypeBox.getSelectedItem();
            markChanged();
        });

        add(lineTypeBox);

        // WEIGHT
        Integer[] weights = {1, 3, 5, 7, 9, 11, 20};
        JComboBox<Integer> weightBox = new JComboBox<>(weights);
        weightBox.setSelectedItem(3);

        weightBox.addActionListener(e -> {
            chosenWeight = (Integer) weightBox.getSelectedItem();
            markChanged();
        });

        add(weightBox);
    }

    // =========================
    // Public API
    // =========================

    public ToolbarState getState() {
        hasChanged = false;
        return new ToolbarState(
                chosenOutlineColor,
                chosenLineType,
                chosenFill,
                chosenMode,
                chosenWeight
        );
    }
}

