package cn.dropbox.httpmgmt;

import cn.dropbox.common.rmgmt.api.Resource;

public class HttpHandler {
    
    /*
        Usage: To perform a HTTP GET use the following lines of code -
        
        HttpHandler h = HttpHandler.getInstance();
        // h.init(userName, authHeader) // Do this only once and only where it is really needed!
        Resource res = h.executeGET();
     */
    
    
    private String userName; // TODO: Change the type to User
    
    private Object authHeader; // TODO: Change the type of this object
    
    private static HttpHandler handler = null;
    
    public static synchronized HttpHandler getInstance() {
        if(handler == null) {
            handler = new HttpHandler();
        } 
        return handler;
    }
    
    public void init(String userName, Object authHeader) {
        this.userName = userName;
        this.authHeader = authHeader;
    }
    
    public void executePUT(Resource resource) {
        
    }
    
    public Resource executeGET(String URI) {
        return null;
    }
    
    public void executeDelete(String URI) {
        
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(Object authHeader) {
        this.authHeader = authHeader;
    }
    
}
