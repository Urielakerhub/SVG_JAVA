package mx.unison.svg.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.RoundRectangle2D;

public class Rect {
    private double width;
    private double height;
    private double x;
    private double y;
    private double rx;
    private double ry;
    private Color stroke;
    private float strokeWidth;
    private Color fill;

    // 1. Constructor requerido por Geometric.java para calcular área y perímetro
    public Rect(double width, double height) {
        this.width = width;
        this.height = height;
        this.x = 0;
        this.y = 0;
    }

    // 2. Constructor completo por si se requiere para el renderizado gráfico
    public Rect(double x, double y, double width, double height, double rx, double ry, Color stroke, float strokeWidth, Color fill) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rx = rx;
        this.ry = ry;
        this.stroke = stroke;
        this.strokeWidth = strokeWidth;
        this.fill = fill;
    }

    // Métodos de cálculo requeridos por Geometric.java
    public double area() {
        return width * height;
    }

    public double perimeter() {
        return 2 * (width + height);
    }

    // Método de dibujo para Java 2D
    public void draw(Graphics2D g2d) {
        if (width <= 0 || height <= 0) return;
        RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, rx * 2, ry * 2);

        if (fill != null) {
            g2d.setColor(fill);
            g2d.fill(rect);
        }
        if (stroke != null && strokeWidth > 0) {
            g2d.setColor(stroke);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.draw(rect);
        }
    }
}