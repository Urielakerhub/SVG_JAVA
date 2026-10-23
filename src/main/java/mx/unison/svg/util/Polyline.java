package mx.unison.svg.util;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class Polyline {
    private List<double[]> points;
    private Color strokeColor;
    private float strokeWidth;
    private Color fillColor;

    public Polyline(String pointsStr, Color strokeColor, float strokeWidth, Color fillColor) {
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
        this.fillColor = fillColor;
        this.points = parsePoints(pointsStr);
    }

    private List<double[]> parsePoints(String pointsStr) {
        List<double[]> list = new ArrayList<>();
        if (pointsStr == null || pointsStr.trim().isEmpty()) return list;

        String[] tokens = pointsStr.trim().split("[\\s,]+");
        for (int i = 0; i < tokens.length - 1; i += 2) {
            try {
                double x = Double.parseDouble(tokens[i]);
                double y = Double.parseDouble(tokens[i + 1]);
                list.add(new double[]{x, y});
            } catch (NumberFormatException e) {

            }
        }
        return list;
    }

    public void draw(Graphics2D g2d) {
        if (points.size() < 2) return;

        Path2D path = new Path2D.Double();
        path.moveTo(points.get(0)[0], points.get(0)[1]);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i)[0], points.get(i)[1]);
        }

        if (fillColor != null) {
            g2d.setColor(fillColor);
            g2d.fill(path);
        }

        if (strokeColor != null && strokeWidth > 0) {
            g2d.setColor(strokeColor);
            g2d.setStroke(new BasicStroke(strokeWidth));
            g2d.draw(path);
        }
    }
}
