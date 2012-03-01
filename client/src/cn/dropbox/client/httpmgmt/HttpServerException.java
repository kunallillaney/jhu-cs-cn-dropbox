package cn.dropbox.client.httpmgmt;

public class HttpServerException extends Exception {
    
    int httpStatusCode;
    
    public HttpServerException(int httpStatusCode, String message, Throwable t) {
        super(message, t);
        this.httpStatusCode = httpStatusCode;
    }

}
