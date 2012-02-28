package cn.dropbox.server.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.File;
import cn.dropbox.common.rmgmt.model.RType;
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
        fileRes.setFileSize(XMLHelper.getIntValue(resElem, RESOURCE_SIZE_TAG));
        fileRes.setMimeType(XMLHelper.getTextValue(resElem, RESOURCE_TYPE_TAG));
        NodeList resDateElemList = resElem.getElementsByTagName(RESOURCE_DATE_TAG);
        // Assume that there will be only one Date tag
        Element resDateElem = (Element)resDateElemList.item(0);
        int year = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_YEAR_TAG);
        int month = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_MONTH_TAG);
        int day = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_DAY_TAG);
        int hour = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_HOUR_TAG);
        int min = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_MINUTE_TAG);
        int second = XMLHelper.getIntValue(resDateElem, RESOURCE_DATE_SECOND_TAG);
        fileRes.setLastModified(year, month, day, hour, min, second);
        
        String fileContentB64 = XMLHelper.getTextValue(resElem, RESOURCE_CONTENT_TAG);
        fileRes.setFileContents(Base64.decode(fileContentB64));
        
        return fileRes;
    }

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructXML(cn.dropbox.common.rmgmt.api.Resource)
     * Used to construct a XML from a resource object when GET is called on a File. 
     */
    @Override
    public String constructXML(Resource resource) {
        File res = (File)resource;
        String retXML = null;
        try {
            Document dom = XMLHelper.getDOMMaker();
            
            //create the root element <ResourceDownload>
            Element rootEle = dom.createElement(RESOURCE_DOWNLOAD_TAG);
            dom.appendChild(rootEle);
            
            Element resourceEle = dom.createElement(RESOURCE_TAG);
            //resourceEle.setAttribute(RESOURCE_ATTR_CATEGORY, resource.getType().name().toLowerCase());
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
    
    public static void main(String[] args) {
        FileInputStream fin;
        try {
            fin = new FileInputStream("parseme.xml");
            Resource res = XMLUtil.constructResource(fin);
            System.out.println(res);
            fin.close();
            
            System.out.println(XMLUtil.constructXML(res));
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

}
