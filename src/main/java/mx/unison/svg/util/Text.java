package mx.unison.svg.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class Text {
    private double x;
    private double y;
    private String fontFamily;
    private float fontSize;
    private Color fillColor;
    private String content;

    public Text(double x, double y, String fontFamily, float fontSize, Color fillColor, String content) {
        this.x = x;
        this.y = y;
        this.fontFamily = (fontFamily != null && !fontFamily.isEmpty()) ? fontFamily : "SansSerif";
        this.fontSize = (fontSize > 0) ? fontSize : 12f;
        this.fillColor = (fillColor != null) ? fillColor : Color.BLACK;
        this.content = (content != null) ? content : "";
    }

    public void draw(Graphics2D g2d) {
        if (content.trim().isEmpty()) return;

        Font font = new Font(fontFamily, Font.PLAIN, (int) fontSize);
        g2d.setFont(font);
        g2d.setColor(fillColor);
        g2d.drawString(content, (float) x, (float) y);
    }
}