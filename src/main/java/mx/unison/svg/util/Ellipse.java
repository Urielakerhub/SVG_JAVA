package mx.unison.svg.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Ellipse2D;

public class Ellipse {
    private double cx;
    private double cy;
    private double rx;
    private double ry;
    private Color strokeColor;
    private float strokeWidth;
    private Color fillColor;

    public Ellipse(double cx, double cy, double rx, double ry, Color strokeColor, float strokeWidth, Color fillColor) {
        this.cx = cx;
        this.cy = cy;
        this.rx = rx;
        this.ry = ry;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
    }

    public void draw(Graphics2D g2d) {
        if (rx <= 0 || ry <= 0) return;

        double x = cx - rx;
        double y = cy - ry;
        double w = rx * 2;
        double h = ry * 2;

        Ellipse2D ellipse = new Ellipse2D.Double(x, y, w, h);

        if (fillColor != null) {
            g2d.setColor(fillColor);
            g2d.fill(ellipse);
        }

        if (strokeColor != null && strokeWidth > 0) {
            g2d.setColor(strokeColor);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.draw(ellipse);
        }
    }
}