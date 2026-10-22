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
        LOG.info("Painting SVG Diagram");
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(strokeColor);

        for (Properties shape : shapes) {
            String type = shape.getProperty("type");

            if (type == null)
                continue;
            if (type.equals("line")) {
                String x1Str = shape.getProperty("x1");
                String y1Str = shape.getProperty("y1");
                String x2Str = shape.getProperty("x2");
                String y2Str = shape.getProperty("y2");
                String stroke = shape.getProperty("stroke");
                String strokeWidthStr = shape.getProperty("stroke-width");

                if (strokeWidthStr != null) {
                    try {
                        float strokeWidth = Float.parseFloat(strokeWidthStr);
                        currentStroke = new BasicStroke(strokeWidth);
                        g2d.setStroke(currentStroke);
                    } catch (NumberFormatException e) {
                        LOG.warning("Invalid stroke width: " + strokeWidthStr);
                        currentStroke = new BasicStroke(1.0f);
                        g2d.setStroke(currentStroke);
                    }
                }

                if (x1Str != null && y1Str != null && x2Str != null && y2Str != null) {
                    int x1 = Integer.parseInt(x1Str);
                    int y1 = Integer.parseInt(y1Str);
                    int x2 = Integer.parseInt(x2Str);
                    int y2 = Integer.parseInt(y2Str);

                    if (stroke != null) {
                        strokeColor = null;

                        try {
                            strokeColor = Color.decode(stroke);
                        } catch (NumberFormatException e) {
                            LOG.warning("Invalid stroke color: " + stroke);
                            //strokeColor = parseColor(stroke);
                            g2d.setColor( parseColor(stroke, 1.0f) );
                        }
                        g2d.setColor(strokeColor);
                    } else {
                        g2d.setColor(strokeColor);
                    }
                    g2d.drawLine(x1, y1, x2, y2);
                }

            }
            if (type.equals("circle")) {
                String cx = shape.getProperty("cx");
                String cy = shape.getProperty("cy");
                String radio = shape.getProperty("r");
                String fill = shape.getProperty("fill");
                String stroke = shape.getProperty("stroke");
                String strokeWidthStr = shape.getProperty("stroke-width");

                if (cx != null && cy != null && radio != null) {
                    int centroX = Integer.parseInt(cx);
                    int centroY = Integer.parseInt(cy);
                    int radius = Integer.parseInt(radio);

                    if (fill != null) {
                        try {
                            fillColor = Color.decode(fill);
                        } catch (NumberFormatException e) {
                            LOG.warning("Invalid stroke color: " + fill);
                            fillColor = parseColor(fill,1.0f);
                        }
                        g2d.setColor(fillColor);
                        g2d.fillOval(centroX - radius, centroY - radius
                                , radius * 2, radius * 2);
                    }
                    if (stroke != null) {
                        strokeColor = null;

                        try {
                            strokeColor = Color.decode(stroke);
                        } catch (NumberFormatException e) {
                            LOG.warning("Invalid stroke color: " + stroke);
                            strokeColor = parseColor(stroke);
                        }
                        g2d.setColor(strokeColor);
                    } else {
                        g2d.setColor(fillColor);
                    }

                    if (strokeWidthStr != null) {
                        try {
                            float strokeWidth = Float.parseFloat(strokeWidthStr);
                            currentStroke = new BasicStroke(strokeWidth);
                            g2d.setStroke(currentStroke);
                        } catch (NumberFormatException e) {
                            LOG.warning("Invalid stroke width: " + strokeWidthStr);
                            currentStroke = new BasicStroke(1.0f);
                            g2d.setStroke(currentStroke);
                        }
                    }
                    g2d.drawOval(centroX - radius,
                            centroY - radius,
                            radius * 2, radius * 2);

                }

                if (type.equals("rect")) {
                    try {
                        double x = Double.parseDouble(
                                shape.getProperty("x", "0"));
                        double y = Double.parseDouble(
                                shape.getProperty("y", "0"));
                        double width = Double.parseDouble(
                                shape.getProperty("width", "0"));
                        double height = Double.parseDouble(
                                shape.getProperty("height", "0"));

                        if (width <= 0 || height <= 0) {
                            continue;
                        }

                        float strokeWidth = Float.parseFloat(
                                shape.getProperty("stroke-width", "1"));

                        String fill = shape.getProperty("fill", "black");
                        String stroke = shape.getProperty("stroke", "none");

                        java.awt.geom.Rectangle2D.Double rect =
                                new java.awt.geom.Rectangle2D.Double(
                                        x, y, width, height);

                        // Dibujar el relleno.
                        if (!fill.equalsIgnoreCase("none")) {
                            Color fillColor = parseColor(fill, 1.0f);
                            if (fillColor != null) {
                                g2d.setColor(fillColor);
                                g2d.fill(rect);
                            }
                        }

                        // Dibujar el contorno.
                        if (!stroke.equalsIgnoreCase("none")
                                && strokeWidth > 0) {
                            Color strokeColor = parseColor(stroke, 1.0f);
                            if (strokeColor != null) {
                                g2d.setColor(strokeColor);
                                g2d.setStroke(
                                        new BasicStroke(strokeWidth));
                                g2d.draw(rect);
                            }
                        }
                    } catch (NumberFormatException ex) {
                        LOG.warning("Atributos numéricos inválidos en rect: "
                                + ex.getMessage());
                    }
                }

            }

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
