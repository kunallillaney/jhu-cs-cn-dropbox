package cn.dropbox.server.common.api;

public class ResourceDoesNotExistException extends Exception {
    
    public ResourceDoesNotExistException(String message) {
        super(message);
    }
    
    public ResourceDoesNotExistException(Throwable e) {
        super(e);
    }

}
