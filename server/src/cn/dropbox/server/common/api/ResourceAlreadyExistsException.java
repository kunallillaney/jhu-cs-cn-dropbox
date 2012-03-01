package cn.dropbox.server.common.api;

public class ResourceAlreadyExistsException extends Exception {
    
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
    
    public ResourceAlreadyExistsException(Throwable e) {
        super(e);
    }

}
