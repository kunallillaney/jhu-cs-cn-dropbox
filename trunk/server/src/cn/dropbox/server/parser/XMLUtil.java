package cn.dropbox.server.parser;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.RType;

public class XMLUtil implements XMLConstants {

    public static String constructXML(Resource res) {
        XMLHandler xmlHandler = XMLHandlerFactory.getXMLHandler(res.getType());
        return xmlHandler.constructXML(res);
    }

    public static Resource constructResource(InputStream xmlStream) {
        Resource retResource = null;
        // get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            // Using factory get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using builder to get DOM representation of the XML file
            Document dom = db.parse(xmlStream);
            
            // Find the type of resource
            RType resType = null;
            NodeList resElem = dom.getElementsByTagName(RESOURCE_TAG);
            if(resElem == null || resElem.item(0) == null) {
                // TODO: Throw error, something is wrong. A malicious XML was posted!
            }
            NamedNodeMap resAttrs = resElem.item(0).getAttributes();
            String xmlCategory = null;
            if (resAttrs != null) {
                Node xmlCategoryNode = resAttrs.getNamedItem(RESOURCE_ATTR_CATEGORY);
                if (xmlCategoryNode != null) {
                    xmlCategory = xmlCategoryNode.getNodeValue();
                    resType = RType.getRType(xmlCategory);
                }
            }
            if(resType == null) {
                // TODO: Throw error, something is wrong. A malicious XML was posted!
            }
            
            // Parse the contents of the XML and return the Resource object.
            XMLHandler xmlHandler = XMLHandlerFactory.getXMLHandler(resType);
            retResource = xmlHandler.constructResourceObject(dom);
            
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace(); // TODO
        } catch (SAXException se) {
            se.printStackTrace(); // TODO
        } catch (IOException ioe) {
            ioe.printStackTrace(); // TODO
        } finally {
            // Close the stream
            try {
                xmlStream.close();
            } catch (IOException e) {
                // Usually Ignored!
            }
        }
        return retResource;
    }

}
