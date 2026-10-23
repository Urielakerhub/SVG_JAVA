package mx.unison.svg.display;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Logger;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Properties;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SVGDiagram extends JComponent {

    public static final Logger LOG = Logger.getLogger(SVGDiagram.class.getName());

    private SAXParser parser = null;

    private int svgW; // ancho del dibujo
    private int svgH;  // alto del dibujo

    private Properties webColors;

    // parametros iniciales par dibujar las figuras
    private Color strokeColor;
    private Color fillColor;
    private BasicStroke currentStroke;

    // propiedades de las figuras leidas del archivo SVG
    private ArrayList<Properties> shapes = null;
    private Properties currentShape = null;

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(svgW, svgH);
    }


    public SVGDiagram(File file) {
        super();
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);

        try {
            parser = factory.newSAXParser();
        } catch (Exception e) {
            LOG.severe("Error creating SAX parser: " + e.getMessage());
        }
        try {
            // Parsear el archivo SVG usando un manejador de eventos anónimo
            // Este manejador solo captura el elemento <svg> para obtener sus atributos de ancho y alto
            LOG.info("Parsing SVG file: " + file.getAbsolutePath());
            parser.parse(file, new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
                    if (localName.equals("svg")) {
                        String width = attributes.getValue("width");
                        String height = attributes.getValue("height");
                        svgW = Integer.parseInt(width);
                        svgH = Integer.parseInt(height);
                        LOG.info("SVG Document\n\tWidth: " + width + " , Height: " + height);
                    }
                }
            });
        } catch (Exception e) {
            LOG.severe("Error getting SVG dimensions: " + e.getMessage());
        }

        shapes = new ArrayList<>();
        strokeColor = Color.BLACK;
        fillColor = Color.LIGHT_GRAY;
        currentStroke = new BasicStroke(1.0f);

        TagsHandler tagsHandler = new TagsHandler();

        try {
            parser.reset();
            parser.parse(file, tagsHandler);
        } catch (Exception e) {
            LOG.severe("Error in SVG file: " + e.getMessage());
            e.printStackTrace();
        }
        shapes = tagsHandler.getShapes();
        // Cargar los colores web desde el archivo colors.properties
        loadColors();
    }

    @Override
    protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();

            try {
                g2d.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                for (Properties shape : shapes) {
                    String type = shape.getProperty("type", "");

                    // Dibujar LINE
                    if (type.equals("line")) {
                        double x1 = Double.parseDouble(shape.getProperty("x1", "0"));
                        double y1 = Double.parseDouble(shape.getProperty("y1", "0"));
                        double x2 = Double.parseDouble(shape.getProperty("x2", "0"));
                        double y2 = Double.parseDouble(shape.getProperty("y2", "0"));

                        String stroke = shape.getProperty("stroke", "black");
                        float strokeWidth = Float.parseFloat(
                                shape.getProperty("stroke-width", "1")
                        );

                        if (!stroke.equals("none") && strokeWidth > 0) {
                            Color color = getSafeColor(stroke);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.setStroke(new BasicStroke(strokeWidth));
                                g2d.draw(new java.awt.geom.Line2D.Double(
                                        x1, y1, x2, y2
                                ));
                            }
                        }
                    }

                    // Dibujar CIRCLE
                    else if (type.equals("circle")) {
                        double cx = Double.parseDouble(shape.getProperty("cx", "0"));
                        double cy = Double.parseDouble(shape.getProperty("cy", "0"));
                        double r = Double.parseDouble(shape.getProperty("r", "0"));

                        if (r <= 0) {
                            continue;
                        }

                        java.awt.geom.Ellipse2D.Double circle =
                                new java.awt.geom.Ellipse2D.Double(
                                        cx - r, cy - r, r * 2, r * 2
                                );

                        String fill = shape.getProperty("fill", "black");
                        String stroke = shape.getProperty("stroke", "none");
                        float strokeWidth = Float.parseFloat(
                                shape.getProperty("stroke-width", "1")
                        );

                        if (!fill.equals("none")) {
                            Color color = getSafeColor(fill);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.fill(circle);
                            }
                        }

                        if (!stroke.equals("none") && strokeWidth > 0) {
                            Color color = getSafeColor(stroke);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.setStroke(new BasicStroke(strokeWidth));
                                g2d.draw(circle);
                            }
                        }
                    }

                    // Dibujar RECT
                    else if (type.equals("rect")) {
                        double x = Double.parseDouble(shape.getProperty("x", "0"));
                        double y = Double.parseDouble(shape.getProperty("y", "0"));
                        double width = Double.parseDouble(
                                shape.getProperty("width", "0")
                        );
                        double height = Double.parseDouble(
                                shape.getProperty("height", "0")
                        );

                        if (width <= 0 || height <= 0) {
                            continue;
                        }

                        java.awt.geom.Rectangle2D.Double rect =
                                new java.awt.geom.Rectangle2D.Double(
                                        x, y, width, height
                                );

                        String fill = shape.getProperty("fill", "black");
                        String stroke = shape.getProperty("stroke", "none");
                        float strokeWidth = Float.parseFloat(
                                shape.getProperty("stroke-width", "1")
                        );

                        if (!fill.equals("none")) {
                            Color color = getSafeColor(fill);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.fill(rect);
                            }
                        }

                        if (!stroke.equals("none") && strokeWidth > 0) {
                            Color color = getSafeColor(stroke);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.setStroke(new BasicStroke(strokeWidth));
                                g2d.draw(rect);
                            }
                        }
                    }

                    // Dibujar POLYLINE
                    else if (type.equals("polyline")) {
                        String points = shape.getProperty("points", "").trim();

                        if (points.isEmpty()) {
                            continue;
                        }

                        String[] coordinates = points.split("[\\s,]+");

                        if (coordinates.length < 4 ||
                                coordinates.length % 2 != 0) {
                            continue;
                        }

                        java.awt.geom.Path2D.Double path =
                                new java.awt.geom.Path2D.Double();

                        for (int i = 0; i < coordinates.length; i += 2) {
                            double px = Double.parseDouble(coordinates[i]);
                            double py = Double.parseDouble(coordinates[i + 1]);

                            if (i == 0) {
                                path.moveTo(px, py);
                            } else {
                                path.lineTo(px, py);
                            }
                        }

                        String fill = shape.getProperty("fill", "black");
                        String stroke = shape.getProperty("stroke", "none");
                        float strokeWidth = Float.parseFloat(
                                shape.getProperty("stroke-width", "1")
                        );

                        if (!fill.equals("none")) {
                            Color color = getSafeColor(fill);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.fill(path);
                            }
                        }

                        if (!stroke.equals("none") && strokeWidth > 0) {
                            Color color = getSafeColor(stroke);

                            if (color != null) {
                                g2d.setColor(color);
                                g2d.setStroke(new BasicStroke(strokeWidth));
                                g2d.draw(path);
                            }
                        }
                    }
                }
            } catch (NumberFormatException e) {
                LOG.warning("Una figura SVG contiene un valor numerico invalido: "
                        + e.getMessage());
            } finally {
                g2d.dispose();
            }
        }

    private Color getSafeColor(String colorString) {
        if (colorString == null || colorString.trim().isEmpty()
                || colorString.equalsIgnoreCase("none")) {
            return null;
        }

        try {
            String value = colorString.trim();

            if (value.startsWith("#") || value.startsWith("0x")
                    || value.startsWith("0X")) {
                return Color.decode(value);
            }

            return parseColor(value, 1.0f);
        } catch (RuntimeException e) {
            LOG.warning("Color no reconocido: " + colorString);
            return null;
        }
    }


    // https://developer.mozilla.org/en-US/docs/Web/CSS/Reference/Values/named-color
    private void loadColors() {
        try {
            String userDir = System.getProperty("user.dir");
            FileReader reader = new FileReader(userDir + "/colors.properties");
            webColors = new Properties();
            webColors.load(reader);
            //System.out.println(webColors.size() + " " + webColors.getProperty("maroon"));
        } catch (IOException ex) {
            LOG.severe(ex.getMessage());
        }
    }

    // Metodo para parsear un color desde un string, considerando el formato hexadecimal, rgb o nombre de color
    private Color parseColor(String colorString, float opacity) {
        String colorCode = colorString.toLowerCase();
        Color newColor = null;

        //System.out.println("param " + colorCode);
        if (colorCode.startsWith("#")) {
            // Codigo de color "#00ffff"
            colorCode = colorCode.substring(1);
        } else if (colorCode.startsWith("0x")) {
            colorCode = colorCode.substring(2);
        } else if (colorCode.startsWith("rgb")) {
            // Codigo de color "rgb(00,255,0)"
            if (colorCode.startsWith("(", 3)) {
                return Color.BLACK;
            } else if (colorCode.startsWith("a(", 3)) {
                return Color.BLACK;
            }
        } else {
            // nombre de color e.g. "red
            //Color namedColor = Color.getColor(colorString);
            colorCode = webColors.getProperty(colorCode).substring(1).trim();
        }
        try {
            int r;
            int g;
            int b;
            int a;
            int len = colorCode.length();

            if (len == 6) {
                //System.out.println(colorCode);
                r = Integer.parseInt(colorCode.substring(0, 2), 16);
                g = Integer.parseInt(colorCode.substring(2, 4), 16);
                b = Integer.parseInt(colorCode.substring(4, 6), 16);
                //System.out.println(r + "," + g + "," + b);
              // newColor = new Color(r, g, b, opacity);
                newColor = new Color(r, g, b);
            }

        } catch (NumberFormatException nfe) {
           LOG.warning("Invalid color format: " + nfe.getMessage() );
           newColor = Color.BLACK;
        }
        return newColor;
    }



    private Color parseColor(String colorStr) {
        Color color = null;
        switch (colorStr) {
            case "black":
                color = Color.BLACK;
                break;
            case "white":
                color = Color.WHITE;
                break;
            case "red":
                color = Color.RED;
                break;
            case "green":
                color = Color.GREEN;
                break;
            case "blue":
                color = Color.BLUE;
                break;
            case "yellow":
                color = Color.YELLOW;
                break;
            case "cyan":
                color = Color.CYAN;
                break;
            case "magenta":
                color = Color.MAGENTA;
                break;
            case "gray":
                color = Color.GRAY;
                break;
            default:
                LOG.warning("Unknown color: " + colorStr);
        }
        return color;
    }

}
