package cn.dropbox.common.parser.api;

import org.w3c.dom.Document;

import cn.dropbox.common.rmgmt.api.Resource;

public interface XMLHandler {
    
    public Resource constructResourceObject(Document xmlDocument);
    
    public String constructXML(Resource Resource);

}
