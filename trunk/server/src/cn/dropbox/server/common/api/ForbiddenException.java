package cn.dropbox.server.common.api;

public class ForbiddenException extends Exception {
    
    public static final int UNAUTHORIZED = 403;
    
    public static final int FORBIDDEN = 403;
    
    int code;
    
    public ForbiddenException(int code, String message) {
        super(message);
        this.code = code;
    }
    
    public ForbiddenException(int code, Throwable e) {
        super(e);
        this.code = code;
    }
    
    public int getCode() {
        return code;
    }
    
}
