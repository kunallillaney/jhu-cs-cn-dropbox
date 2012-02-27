package cn.dropbox.server.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.File;
import cn.dropbox.server.parser.XMLConstants;
import cn.dropbox.server.parser.XMLUtil;

public class FileXMLHandler implements XMLHandler, XMLConstants {

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructResourceObject(org.w3c.dom.Document)
     * Used to construct a resource object from XML when a PUT is called on a File. 
     */
    @Override
    public Resource constructResourceObject(Document xmlDocument) {
        File fileRes = new File();
        NodeList resElemList = xmlDocument.getElementsByTagName(RESOURCE_TAG);
        // TODO: Assume that there can only be one resource as of now!
        Element resElem = (Element)resElemList.item(0);
        fileRes.setFileName(XMLHelper.getTextValue(resElem, RESOURCE_NAME_TAG));
        fileRes.setFileSize(XMLHelper.getTextValue(resElem, RESOURCE_SIZE_TAG));
        fileRes.setMimeType(XMLHelper.getTextValue(resElem, RESOURCE_TYPE_TAG));
        
        NodeList resDateElem = resElem.getElementsByTagName(RESOURCE_DATE_TAG);
        
        
        //fileRes.setFileContents(XMLHelper.getTextValue(resElem, RESOURCE_CONTENT_TAG));
        
        return fileRes;
    }

    @Override
    public String constructXML(Resource Resource) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public static void main(String[] args) {
        FileInputStream fin;
        try {
            fin = new FileInputStream("parseme.xml");
            Resource res = XMLUtil.constructResource(fin);
            System.out.println(res);
            fin.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

}
