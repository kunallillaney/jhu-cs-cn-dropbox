package cn.dropbox.client.parser.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import cn.dropbox.client.parser.XMLHandlerFactory;
import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.Directory;
import cn.dropbox.common.rmgmt.model.File;
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
            String name = XMLHelper.getTextValue(resElem, RESOURCE_NAME_TAG);
            String resURL = XMLHelper.getTextValue(resElem, RESOURCE_URL_TAG); 
            // Extract URI from URL
            try {
                URI resURI = new URI(resURL);
                resURL = resURI.getPath();
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String mimeType = XMLHelper.getTextValue(resElem, RESOURCE_TYPE_TAG);
            //fetch date
            NodeList dateElemList = resElem.getElementsByTagName(RESOURCE_DATE_TAG);
            Element dateElem = (Element)dateElemList.item(0);
            int year = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_YEAR_TAG);
            int month = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_MONTH_TAG);
            int day = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_DAY_TAG);
            int hour = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_HOUR_TAG);
            int min = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_MINUTE_TAG);
            int sec = XMLHelper.getIntValue(dateElem, RESOURCE_DATE_SECOND_TAG);
            
            RType resType = RType.getRType(attrVal);
            switch(resType) {
                case FILE: 
                    File resFile = new File();
                    resFile.setFileName(name);
                    int size = XMLHelper.getIntValue(resElem, RESOURCE_SIZE_TAG);
                    resFile.setFileSize(size);
                    resFile.setMimeType(mimeType);
                    resFile.setLastModified(year, month, day, hour, min, sec);
                    resFile.setURI(resURL);
                    dirRes.getFiles().add(resFile);
                    break;
                case DIRECTORY: 
                    Directory resDir = new Directory();
                    resDir.setDirName(name);
                    resDir.setLastModified(year, month, day, hour, min, sec);
                    resDir.setURI(resURL);
                    int numOfElements = XMLHelper.getIntValue(resElem, RESOURCE_NUM_ITEMS);
                    resDir.setNumOfElements(numOfElements);
                    dirRes.getDirectories().add(resDir);
                    break;
            }
        }
        return dirRes;

    }


    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructXML(cn.dropbox.common.rmgmt.api.Resource)
     * Directory PUT
     */
    @Override
    public String constructXML(Resource resource) {
        String retXML = null;
        Directory dirRes = (Directory)resource;
        try {
            Document dom = XMLHelper.getDOMMaker();
            // create the root element <ResourceUpload>
            Element rootEle = dom.createElement(RESOURCE_UPLOAD_TAG);
            dom.appendChild(rootEle);
            
            Element resourceEle = dom.createElement(RESOURCE_TAG);
            resourceEle.setAttribute(RESOURCE_ATTR_CATEGORY, RType.DIRECTORY.name().toLowerCase());
            
            //ResourceName which is FileName
            Element resNameEle = dom.createElement(RESOURCE_NAME_TAG);
            Text resNameText = dom.createTextNode(dirRes.getDirName());
            resNameEle.appendChild(resNameText);
            resourceEle.appendChild(resNameEle);
            
            rootEle.appendChild(resourceEle);
            
            retXML=XMLHelper.constructXMLString(dom);
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return retXML;
    }
    
    public static void main(String[] args) throws Exception {
        XMLHandler xmlHandler = XMLHandlerFactory.getXMLHandler(RType.DIRECTORY);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        FileInputStream xmlStream = new FileInputStream("directoryget.xml");
        Document xmlDocument = db.parse(xmlStream);
        Resource resource = xmlHandler.constructResourceObject(xmlDocument);
        System.out.println(resource);
        
        ((Directory)resource).setDirName("TEstDir");
        System.out.println(xmlHandler.constructXML(resource));
        
        URI uri = new URI("http://localhost:8888/testuri/url");
        System.out.println(uri.getPath());
    }
}
