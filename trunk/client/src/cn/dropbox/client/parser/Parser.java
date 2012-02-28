package cn.dropbox.client.parser;

import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.client.parser.impl.DirectoryXMLHandler;
import cn.dropbox.client.parser.impl.FileXMLHandler;

public class Parser {
    
    private static XMLHandler _instance_FileXMLHandler = null;
    
    private static XMLHandler _instance_DirXMLHandler = null;
    
    public static XMLHandler getFileXMLHandlerInstance() {
        if(_instance_FileXMLHandler == null) {
            _instance_FileXMLHandler = new FileXMLHandler();
        }
        return _instance_FileXMLHandler;
    }

    public static XMLHandler getDirXMLHandlerInstance() {
        if(_instance_DirXMLHandler == null) {
            _instance_DirXMLHandler = new DirectoryXMLHandler();
        }
        return _instance_DirXMLHandler;
    }
}
