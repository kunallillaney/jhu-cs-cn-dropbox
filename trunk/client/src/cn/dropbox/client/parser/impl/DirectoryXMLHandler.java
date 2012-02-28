package cn.dropbox.client.parser.impl;

import java.io.FileInputStream;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import cn.dropbox.client.parser.XMLHandlerFactory;
import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.Directory;
import cn.dropbox.common.rmgmt.model.RType;

public class DirectoryXMLHandler implements XMLHandler, XMLConstants {

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructResourceObject(org.w3c.dom.Document)
     * Directory GET
     */
    @Override
    public Resource constructResourceObject(Document xmlDocument) {
        Directory dirRes = new Directory();
        NodeList resElemList = xmlDocument.getElementsByTagName(RESOURCE_TAG);

        for (int i = 0; i < resElemList.getLength(); i++) {
            Element resElem = (Element)resElemList.item(i);
            String attrVal = resElem.getAttribute(RESOURCE_ATTR_CATEGORY);
            RType resType = RType.getRType(attrVal);
            String name = XMLHelper.getTextValue(resElem, RESOURCE_NAME_TAG);
            int size = XMLHelper.getIntValue(resElem, RESOURCE_SIZE_TAG);
            String resURL = XMLHelper.getTextValue(resElem, RESOURCE_URL_TAG);
            
            //fetch date
            NodeList dateTagList = resElem.getElementsByTagName(RESOURCE_DATE_TAG);
            switch(resType) {
                case FILE: 
                    
                    
            }
        }
        return null;

    }
    
    public static void main(String[] args) throws Exception {
        XMLHandler xmlHandler = XMLHandlerFactory.getXMLHandler(RType.DIRECTORY);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        FileInputStream xmlStream = new FileInputStream("directoryget.xml");
        Document xmlDocument = db.parse(xmlStream);
        xmlHandler.constructResourceObject(xmlDocument);
    }

    @Override
    public String constructXML(Resource resource) {
        // TODO Auto-generated method stub
        return null;
    }

}
