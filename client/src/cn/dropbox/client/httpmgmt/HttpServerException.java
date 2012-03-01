package cn.dropbox.client.httpmgmt;

public class HttpServerException extends Exception {
    
    int httpStatusCode;
    
    public int getHttpStatusCode() {
		return httpStatusCode;
	}
    
    public HttpServerException(int httpStatusCode, String message, Throwable t) {
        super(message, t);
        this.httpStatusCode = httpStatusCode;
    }

}
