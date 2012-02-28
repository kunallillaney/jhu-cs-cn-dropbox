package cn.dropbox.common.parser.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import cn.dropbox.common.rmgmt.model.File;

public class XMLHelper {
    
    public static Document getDOMMaker() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        //get an instance of builder
        DocumentBuilder db = dbf.newDocumentBuilder();
        //create an instance of DOM
        Document dom = db.newDocument();
        return dom;
    }
    
    public static String constructXMLString(Document dom) throws IOException {
        // Make a string out of the DOM object
        OutputFormat format = new OutputFormat(dom);
        format.setIndenting(true);
        ByteArrayOutputStream byteoutStream = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer(byteoutStream, format);
        serializer.serialize(dom);
        String retXMLStr = new String(byteoutStream.toByteArray());
        byteoutStream.close();
        return retXMLStr;
    }
    
    /**
     * I take a xml element and the tag name, look for the tag and get
     * the text content 
     * i.e for <employee><name>John</name></employee> xml snippet if
     * the Element points to employee node and tagName is name I will return John  
     * @param ele
     * @param tagName
     * @return
     */
    public static String getTextValue(Element ele, String tagName) {
        String textVal = null;
        NodeList nl = ele.getElementsByTagName(tagName);
        if(nl != null && nl.getLength() > 0) {
            Element el = (Element)nl.item(0);
            textVal = el.getFirstChild().getNodeValue();
            if (textVal != null) {
                textVal = textVal.trim();
            }
        }

        return textVal;
    }    
    
    /**
     * Calls getTextValue and returns a int value
     * @param ele
     * @param tagName
     * @return
     */
    public static int getIntValue(Element ele, String tagName) {
        //in production application you would catch the exception
        return Integer.parseInt(getTextValue(ele,tagName));
    }
    
    public static String constructURI(File file) {
        return "DOC_ROOT" + file.getURI(); // TODO: Prepend doc root
    }
}
