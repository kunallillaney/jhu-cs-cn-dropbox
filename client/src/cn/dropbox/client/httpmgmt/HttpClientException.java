package cn.dropbox.client.httpmgmt;

public class HttpClientException extends Exception {
	
	public HttpClientException(String msg, Throwable t) {
		super(msg, t);
	}

}
