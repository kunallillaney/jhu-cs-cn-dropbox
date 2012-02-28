package cn.dropbox.client.parser;

import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.rmgmt.model.RType;

public class XMLHandlerFactory {
    
    public static XMLHandler getXMLHandler(RType resourceType) {
        XMLHandler retXMLHandler = null;
        switch (resourceType) {
            case FILE:
                retXMLHandler = Parser.getFileXMLHandlerInstance();
                break;
            case DIRECTORY:
                retXMLHandler = Parser.getDirXMLHandlerInstance();
                break;
        }
        return retXMLHandler;
    }

}
