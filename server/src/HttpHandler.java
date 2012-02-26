import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class HttpHandler implements HttpRequestHandler{

	@Override
	public void handle(
			final HttpRequest request, 
			final HttpResponse response, 
			final HttpContext context) throws HttpException, IOException {
		
		// Checking for unknown Request
		String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
        if (!method.equals("GET") && !method.equals("HEAD") && !method.equals("POST")) {
            throw new MethodNotSupportedException(method + " method not supported"); 
        }
		
	}

}
