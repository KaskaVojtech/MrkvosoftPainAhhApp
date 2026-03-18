package org.example.Structs;

import java.awt.*;

public record ToolbarState(
        Color outlineColor,
        LineType lineType,
        Fill fill,
        Mode mode,
        int weight
) {}