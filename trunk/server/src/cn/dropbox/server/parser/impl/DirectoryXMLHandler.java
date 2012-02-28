package cn.dropbox.server.parser.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import cn.dropbox.common.parser.api.XMLConstants;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.Directory;
import cn.dropbox.common.rmgmt.model.File;
import cn.dropbox.common.rmgmt.model.RType;
import cn.dropbox.server.listen.ServerListen;
import cn.dropbox.server.parser.XMLUtil;

public class DirectoryXMLHandler implements XMLHandler, XMLConstants {

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructResourceObject(org.w3c.dom.Document)
     * Used to construct a resource object from XML when a PUT is called on a Directory. 
     */
    @Override
    public Resource constructResourceObject(Document xmlDocument) {
        Directory dirRes = new Directory();
        NodeList resElemList = xmlDocument.getElementsByTagName(RESOURCE_TAG);
        
        // TODO: Assume that there can only be one resource as of now!
        Element resElem = (Element)resElemList.item(0);
        dirRes.setDirName(XMLHelper.getTextValue(resElem, RESOURCE_NAME_TAG));
        
        return dirRes;
    }

    /* (non-Javadoc)
     * @see cn.dropbox.common.parser.api.XMLHandler#constructXML(cn.dropbox.common.rmgmt.api.Resource)
     * Used to construct a resource object from XML when a GET is called on a Directory. 
     */
    @Override
    public String constructXML(Resource resource) {
        String retXML = null;
        Directory dirRes = (Directory)resource;
        try {
            Document dom = XMLHelper.getDOMMaker();
            // create the root element <ResourceList>
            Element rootEle = dom.createElement(RESOURCE_LIST_TAG);
            dom.appendChild(rootEle);
            // 
            List<File> fileList = dirRes.getFiles();
            for(File file : fileList) {
                Element resFileEle = getFileResElement(dom, file);
                rootEle.appendChild(resFileEle);
            }
            
            List<Directory> dirList = dirRes.getDirectories();
            for(Directory dir : dirList) {
                Element resDirEle = getDirResElement(dom, dir);
                rootEle.appendChild(resDirEle);
            }
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
    
    private Element getDirResElement(Document dom, Directory dir) {
        Element resourceEle = dom.createElement(RESOURCE_TAG);
        resourceEle.setAttribute(RESOURCE_ATTR_CATEGORY, RType.DIRECTORY.name().toLowerCase());
        
        //ResourceName which is DirectoryName
        Element resNameEle = dom.createElement(RESOURCE_NAME_TAG);
        Text resNameText = dom.createTextNode(dir.getDirName());
        resNameEle.appendChild(resNameText);
        resourceEle.appendChild(resNameEle);
        
        //ResourceSize which is Number of elements
        Element resSizeEle = dom.createElement(RESOURCE_NUM_ITEMS);
        Text resSizeText = dom.createTextNode(Integer.toString(dir.getDirSize()));
        resSizeEle.appendChild(resSizeText);
        resourceEle.appendChild(resSizeEle);
        
        //ResourceURL which is ResourceURI
        Element resURLEle = dom.createElement(RESOURCE_URL_TAG);
        Text resURLText = dom.createTextNode(constructURI(dir));
        resURLEle.appendChild(resURLText);
        resourceEle.appendChild(resURLEle);
        
        // Iterate through the files and determine which is the last modified file in the directory
        long lastModifiedFileTime = dir.getLastModified().getTime();
        List<File> fileList = dir.getFiles();
        for (Iterator itr = fileList.iterator(); itr.hasNext();) {
            File file = (File) itr.next();
            if(file.getLastModified().getTime() > lastModifiedFileTime) {
                lastModifiedFileTime = file.getLastModified().getTime(); 
            }
        }
        
        //ResourceDate which is Date
        Element resDateEle = XMLHelper.constructDateElement(dom, new Date(lastModifiedFileTime));
        resourceEle.appendChild(resDateEle);
        
        return resourceEle;
    }

    private Element getFileResElement(Document dom, File file) {
        Element resourceEle = dom.createElement(RESOURCE_TAG);
        resourceEle.setAttribute(RESOURCE_ATTR_CATEGORY, RType.FILE.name().toLowerCase());
        
        //ResourceName which is FileName
        Element resNameEle = dom.createElement(RESOURCE_NAME_TAG);
        Text resNameText = dom.createTextNode(file.getFileName());
        resNameEle.appendChild(resNameText);
        resourceEle.appendChild(resNameEle);
        
        //ResourceSize which is FileSize
        Element resSizeEle = dom.createElement(RESOURCE_SIZE_TAG);
        Text resSizeText = dom.createTextNode(Integer.toString(file.getFileSize()));
        resSizeEle.appendChild(resSizeText);
        resourceEle.appendChild(resSizeEle);
        
        //ResourceURL which is ResourceURI
        Element resURLEle = dom.createElement(RESOURCE_URL_TAG);
        Text resURLText = dom.createTextNode(constructURI(file));
        resURLEle.appendChild(resURLText);
        resourceEle.appendChild(resURLEle);
        
        //ResourceType which is MimeType
        Element resTypeEle = dom.createElement(RESOURCE_TYPE_TAG);
        Text resTypeText = dom.createTextNode(file.getMimeType());
        resTypeEle.appendChild(resTypeText);
        resourceEle.appendChild(resTypeEle);
        
        //ResourceDate which is Date
        Element resDateEle = XMLHelper.constructDateElement(dom, file.getLastModified());
        resourceEle.appendChild(resDateEle);
        
        return resourceEle;
    }

    public static void main(String[] args) {
        FileInputStream fin;
        try {
            fin = new FileInputStream("directoryput.xml");
            Resource res = XMLUtil.constructResource(fin);
            System.out.println(res);
            fin.close();
            
            List<File> testFiles = new ArrayList<File>();
            List<Directory> testDirs = new ArrayList<Directory>();
            for(int i=0;i<3;i++) {
                File f = new File();
                f.setFileName("TestFile"+i);
                f.setFileSize(i*1024);
                f.setLastModified(new Date(new Date().getTime() + i*1000*1000*1000));
                f.setMimeType("Test Mime Type("+i+")");
                f.setURI("/uri/file"+i);
                testFiles.add(f);
                
                Directory d = new Directory();
                d.setDirName("DirName"+i);
                d.setURI("uri/dir"+i);
                d.setLastModified(new Date(new Date().getTime() + i*1000*1000*1000));
                testDirs.add(d);
            }
            ((Directory)res).setFiles(testFiles);
            ((Directory)res).setDirectories(testDirs);
            System.out.println(XMLUtil.constructXML(res));
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }

    
    public static String constructURI(Resource res) {
        return res.getURI(); // TODO: Prepend doc root
    }
}
