package cn.dropbox.server.common.api;

public class ServerException extends Exception {

    public ServerException(String message) {
        super(message);
    }
    
    public ServerException(Throwable t) {
        super(t);
    }
    
    public ServerException(String message, Throwable t) {
        super(message, t);
    }
    
    
}
