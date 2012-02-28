package cn.dropbox.client.parser.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import cn.dropbox.client.parser.XMLHandlerFactory;
import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.File;
import cn.dropbox.common.rmgmt.model.RType;

public class FileXMLHandler implements XMLHandler, XMLConstants {

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructResourceObject(org.w3c.dom.Document)
     * File GET
     */
    @Override
    public Resource constructResourceObject(Document xmlDocument) {
        File fileRes = new File();
        NodeList resElemList = xmlDocument.getElementsByTagName(RESOURCE_TAG);
        // TODO: Assume that there can only be one File as of now!
        Element resElem = (Element)resElemList.item(0);
        fileRes.setFileName(XMLHelper.getTextValue(resElem, RESOURCE_NAME_TAG));
        fileRes.setFileSize(XMLHelper.getIntValue(resElem, RESOURCE_SIZE_TAG));
        fileRes.setMimeType(XMLHelper.getTextValue(resElem, RESOURCE_TYPE_TAG));
        String fileContentB64 = XMLHelper.getTextValue(resElem, RESOURCE_CONTENT_TAG);
        fileRes.setFileContents(Base64.decode(fileContentB64));
        return fileRes;
    }

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructXML(cn.dropbox.common.rmgmt.api.Resource)
     * File PUT
     */
    @Override
    public String constructXML(Resource resource) {
        File res = (File)resource;
        String retXML = null;
        try {
            Document dom = XMLHelper.getDOMMaker();
            
            //create the root element <ResourceUpload>
            Element rootEle = dom.createElement(RESOURCE_UPLOAD_TAG);
            dom.appendChild(rootEle);
            
            Element resourceEle = dom.createElement(RESOURCE_TAG);
            resourceEle.setAttribute(RESOURCE_ATTR_CATEGORY, RType.FILE.name().toLowerCase());
            
            //ResourceName which is FileName
            Element resNameEle = dom.createElement(RESOURCE_NAME_TAG);
            Text resNameText = dom.createTextNode(res.getFileName());
            resNameEle.appendChild(resNameText);
            resourceEle.appendChild(resNameEle);
            
            //ResourceSize which is FileSize
            Element resSizeEle = dom.createElement(RESOURCE_SIZE_TAG);
            Text resSizeText = dom.createTextNode(Integer.toString(res.getFileSize()));
            resSizeEle.appendChild(resSizeText);
            resourceEle.appendChild(resSizeEle);
            
            Element resDateEle = XMLHelper.constructDateElement(dom, res.getLastModified());
            resourceEle.appendChild(resDateEle);
            
            //ResourceType which is MimeType
            Element resTypeEle = dom.createElement(RESOURCE_TYPE_TAG);
            Text resTypeText = dom.createTextNode(res.getMimeType());
            resTypeEle.appendChild(resTypeText);
            resourceEle.appendChild(resTypeEle);
            
            //ResourceEncoding which is always Base64 as of now.
            Element resEncodingEle = dom.createElement(RESOURCE_ENCODING_TAG);
            Text resEncodingText = dom.createTextNode(ENCODING_TYPE);
            resEncodingEle.appendChild(resEncodingText);
            resourceEle.appendChild(resEncodingEle);

            //ResourceContent which is FileContents
            String fileContentsB64 = new String(Base64.encode(res.getFileContents()));
            Element resContentEle = dom.createElement(RESOURCE_CONTENT_TAG);
            Text resContentText = dom.createTextNode(fileContentsB64);
            resContentEle.appendChild(resContentText);
            resourceEle.appendChild(resContentEle);
            
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
        XMLHandler xmlHandler = XMLHandlerFactory.getXMLHandler(RType.FILE);
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        FileInputStream xmlStream = new FileInputStream("fileget.xml");
        Document xmlDocument = db.parse(xmlStream);
        
        Resource res = xmlHandler.constructResourceObject(xmlDocument);
        System.out.println(new Date());
        ((File)res).setLastModified(new Date());
        System.out.println(res);
        System.out.println(xmlHandler.constructXML(res));
    }

}
