package mx.unison.svg.display;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TagsHandler  extends DefaultHandler {
    public static final Logger LOG = Logger.getLogger( TagsHandler.class.getName() );

    private ArrayList<Properties> shapes;
    private Properties currentShape = null;

    public TagsHandler() {
        super();
        shapes = new ArrayList<>();
    }

    public ArrayList<Properties> getShapes() {
        return shapes;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        super.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
            super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        String tagName = null;

        if(localName == null || localName.isEmpty()) {
            tagName = qName.toLowerCase();
        } else {
            tagName = localName.toLowerCase();
        }

        if  (tagName.equals("line") ) {
            LOG.info("Starting element " + tagName);
            currentShape = new Properties();
            currentShape.put("type", "line");

            copyAttributes(atts, currentShape);
            shapes.add(currentShape);
        }

        if  (tagName.equals("circle") ) {
            LOG.info("Starting element " + tagName);
            currentShape = new Properties();
            currentShape.put("type", "circle");

            copyAttributes(atts, currentShape);
            shapes.add(currentShape);
        }

        if (tagName.equals("rect")) {
            LOG.info("Starting element " + tagName);

            currentShape = new Properties();
            currentShape.setProperty("type", "rect");

            copyAttributes(atts, currentShape);
            shapes.add(currentShape);
        }

        if (tagName.equals("polyline")) {
            LOG.info("Starting element " + tagName);

            currentShape = new Properties();
            currentShape.setProperty("type", "polyline");

            copyAttributes(atts, currentShape);
            shapes.add(currentShape);
        }

    }

    private void copyAttributes(Attributes atts, Properties myShape) {
        if(atts.getLength() != 0) { // si da problemas lo comentarlo en caso de que me de problemas
            for(int i = 0; i < atts.getLength(); i++) {
                String name = atts.getLocalName(i);

                // Si SAX no proporciona el nombre local, usar qName.
                if (name == null || name.isEmpty()) {
                    name = atts.getQName(i);
                }

                String value = atts.getValue(i);

                if (name != null && !name.isEmpty() && value != null) {
                    myShape.setProperty(name, value);
                }
            }


        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentShape = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {

    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        LOG.log(Level.SEVERE, "OOPS" + e.getMessage());
    }
}
