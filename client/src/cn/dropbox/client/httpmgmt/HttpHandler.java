package cn.dropbox.client.httpmgmt;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.w3c.dom.Document;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import cn.dropbox.client.parser.XMLHandlerFactory;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.Directory;
import cn.dropbox.common.rmgmt.model.File;
import cn.dropbox.common.rmgmt.model.RType;

public class HttpHandler {

	/*
	 * Usage: To perform a HTTP GET use the following lines of code -
	 * 
	 * HttpHandler h = HttpHandler.getInstance(); // h.init(userName,
	 * authHeader) // Do this only once and only where it is really needed!
	 * Resource res = h.executeGET();
	 */

	private String host;
	private int port;
	private String userName; // TODO: Change the type to User
	private String password; // TODO: Change the type of this object
	private int nc;
	private String serverNonce;

	private static HttpHandler handler = null;

	public static synchronized HttpHandler getInstance() {
		if (handler == null) {
			handler = new HttpHandler();
		}
		return handler;
	}

	public void init(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void setPort(int port) {
        this.port = port;
    }
	
	public void setHost(String host) {
        this.host = host;
    }
	
	public void setUserName(String userName) {
	    this.userName = userName;
	}
	
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void performLogin(String userName, String password) throws HttpClientException, HttpServerException {
        
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "UTF-8");
        HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
        HttpProtocolParams.setUseExpectContinue(params, true);

        HttpContext context = new BasicHttpContext(null);
        HttpHost host = new HttpHost(this.host, this.port);

        DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
        context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
        context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

        try {
            ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();
            if (!conn.isOpen()) {
                Socket socket = new Socket(host.getHostName(), host.getPort());
                conn.bind(socket, params);
            }
            String uri = "/"+userName+"/";
            BasicHttpRequest request = new BasicHttpRequest("GET", uri);

            BasicHttpProcessor httpproc = new BasicHttpProcessor();
            httpproc.addInterceptor(new RequestContent());
            httpproc.addInterceptor(new RequestTargetHost());
            httpproc.addInterceptor(new RequestConnControl());
            httpproc.addInterceptor(new RequestUserAgent());
            httpproc.addInterceptor(new RequestExpectContinue());

            HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

            httpexecutor.preProcess(request, httpproc, context);
            HttpResponse response = httpexecutor
                    .execute(request, conn, context);
            response.setParams(params);
            httpexecutor.postProcess(response, httpproc, context);

            StatusLine status = response.getStatusLine();

            // close the first connection
            if (!connStrategy.keepAlive(response, context)) {
                conn.close();
            } else {
                System.out.println("Connection kept alive...");
            }
            
            if (status.getStatusCode() == HttpStatus.SC_UNAUTHORIZED)
            {
                // Not authenticated! So send auth headers
                // Parse the nonse sent by the server
                serverNonce = getHeaderValue(response, "WWW-Authenticate", "nonce");
                nc = 0;
                Header authHeader = constructAuthHeader(userName, password, uri);
                
                HttpParams paramsIn = new BasicHttpParams();
                HttpProtocolParams.setVersion(paramsIn, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(paramsIn, "UTF-8");
                HttpProtocolParams.setUserAgent(paramsIn, "HttpComponents/1.1");
                HttpProtocolParams.setUseExpectContinue(paramsIn, true);

                HttpContext contextIn = new BasicHttpContext(null);
                HttpHost hostIn = new HttpHost(this.host, this.port);

                DefaultHttpClientConnection connIn = new DefaultHttpClientConnection();
                contextIn.setAttribute(ExecutionContext.HTTP_CONNECTION, connIn);
                contextIn.setAttribute(ExecutionContext.HTTP_TARGET_HOST, hostIn);                
                
                ConnectionReuseStrategy connStrategyIn = new DefaultConnectionReuseStrategy();
                if (!connIn.isOpen()) {
                    Socket socket = new Socket(hostIn.getHostName(), hostIn.getPort());
                    connIn.bind(socket, paramsIn);
                }

                BasicHttpProcessor httpprocIn = new BasicHttpProcessor();
                httpprocIn.addInterceptor(new RequestContent());
                httpprocIn.addInterceptor(new RequestTargetHost());
                httpprocIn.addInterceptor(new RequestConnControl());
                httpprocIn.addInterceptor(new RequestUserAgent());
                httpprocIn.addInterceptor(new RequestExpectContinue());                
                
                // Send the digest to server
                BasicHttpRequest requestIn = new BasicHttpRequest("GET", uri);
                HttpRequestExecutor httpexecutorIn = new HttpRequestExecutor();

                requestIn.setHeader(authHeader);
                
                httpexecutorIn.preProcess(requestIn, httpprocIn, contextIn);
                HttpResponse responseIn = httpexecutorIn
                        .execute(requestIn, connIn, contextIn);
                responseIn.setParams(paramsIn);
                httpexecutorIn.postProcess(responseIn, httpprocIn, contextIn);

                StatusLine statusIn = response.getStatusLine();
                if (statusIn.getStatusCode() != HttpStatus.SC_OK) {
                    // Authentication successful
                    this.userName = userName;
                    this.password = password;
                }
                
                // close the first connection
                if (!connStrategyIn.keepAlive(response, contextIn)) {
                    connIn.close();
                } else {
                    System.out.println("Connection kept alive...");
                }                
                
            } else if(status.getStatusCode() != HttpStatus.SC_OK){
                throw new HttpServerException(status.getStatusCode(), status.getReasonPhrase(), null);
            } else {
                // User is already Authenticated and everything is well set
                this.userName = userName;
                this.password = password;
            }

        } catch (UnknownHostException e) {
            throw new HttpClientException(e.getMessage(), e);
        } catch (IOException e) {
            throw new HttpClientException(e.getMessage(), e);
        } catch (HttpException e) {
            throw new HttpClientException(e.getMessage(), e);
        } finally {
            try {
                conn.close();
            } catch (IOException e) {
                throw new HttpClientException(e.getMessage(), e);
            }
        }        
        
    }
	

    private Header constructAuthHeader(String userName, String password, String URI) throws HttpClientException {
        String curValue = "";
        curValue = valueaddNameValuePairToValueElem(curValue, "username", userName);
        curValue = valueaddNameValuePairToValueElem(curValue, "nonce", serverNonce);
        curValue = valueaddNameValuePairToValueElem(curValue, "uri", URI); 
        curValue = valueaddNameValuePairToValueElem(curValue, "nc", Integer.toString(nc));
        UUID cnonceUuid = UUID.randomUUID();
        String cnonce = cnonceUuid.toString();
        curValue = valueaddNameValuePairToValueElem(curValue, "cnonce", cnonce);
        
        // Compute the response to be set
        String ha1Str = userName+":"+password;
        String ha2Str = URI; // "GET:"+URI; 
        // generate cnonce
        String midStr = serverNonce+":"+nc+":"+cnonce;
        String finalMd5Str = md5(md5(ha1Str) + ":" + midStr + ":" + md5(ha2Str));
        
        curValue = valueaddNameValuePairToValueElem(curValue, "response", finalMd5Str);
        
        Header retHeader = new BasicHeader("Authorization", curValue);
        nc++;
        return retHeader;
    }

    private String md5(String str) throws HttpClientException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(str.getBytes());
            byte[] md5Bytes = digest.digest();
            return new String(Base64.encode(md5Bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new HttpClientException(e.getMessage(), e);
        }
    }

    private String valueaddNameValuePairToValueElem(String curValue,
            String nameOfTheValue, String valueToBeAdded) {
        if(curValue != null && !curValue.equals("")) {
            curValue += ",";
        } else {
            curValue = "";
        }
        curValue += (nameOfTheValue + "=" + "\"" + valueToBeAdded+ "\"");
        return curValue;
    }

    private String getHeaderValue(HttpResponse response, String headerName,
            String headerValElementName) throws HttpServerException {
        Header[] headers = response.getHeaders(headerName);
        if(headers == null || headers.length == 0) {
            throw new HttpServerException(HttpStatus.SC_INTERNAL_SERVER_ERROR, "Required header not set by the server!", null);
        }
        Header header = headers[0];
        HeaderElement[] valElems = header.getElements();
        for (int i = 0; i < valElems.length; i++) {
            if(headerValElementName.equals(valElems[i].getName())) {
                return valElems[i].getValue();
            }
        }
        return null;
    }

    public void executePUT(Resource resource) throws HttpServerException, HttpClientException {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		/*
		 * HttpProcessor httpproc1 = new ImmutableHttpProcessor( new
		 * HttpRequestInterceptor[] { // Required protocol interceptors new
		 * RequestContent(), new RequestTargetHost(), // Recommended protocol
		 * interceptors new RequestConnControl(), new RequestUserAgent(), new
		 * RequestExpectContinue() });
		 */
		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		httpproc.addInterceptor(new RequestContent());
		httpproc.addInterceptor(new RequestTargetHost());
		httpproc.addInterceptor(new RequestConnControl());
		httpproc.addInterceptor(new RequestUserAgent());
		httpproc.addInterceptor(new RequestExpectContinue());

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpContext context = new BasicHttpContext(null);

		HttpHost host = new HttpHost(this.host, this.port);

		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
		ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		try {
			XMLHandler xmlHandle = XMLHandlerFactory.getXMLHandler(resource
					.getType());
			HttpEntity requestBody = new StringEntity(
					xmlHandle.constructXML(resource), "UTF-8");
			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}
			BasicHttpEntityEnclosingRequest request = new BasicHttpEntityEnclosingRequest(
					"PUT", resource.getURI());
			request.setEntity(requestBody);
			request.setParams(params);
			
            Header authHeader = constructAuthHeader(userName, password, resource.getURI());
            request.setHeader(authHeader);
			
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode()!=HttpStatus.SC_OK)
			{
				throw new HttpServerException(status.getStatusCode(), status.getReasonPhrase(), null);
			}
			httpexecutor.postProcess(response, httpproc, context);
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}
		} catch (HttpException e) {
			throw new HttpClientException(e.getMessage(), e);
		} catch (IOException e) {
			throw new HttpClientException(e.getMessage(), e);
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				throw new HttpClientException(e.getMessage(), e);
			}
		}
	}

	public Resource executeGET(String URI, RType resType) throws HttpServerException, HttpClientException {
		Resource rsrc = null;

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpContext context = new BasicHttpContext(null);
		HttpHost host = new HttpHost(this.host, this.port);

		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		//context.setAttribute("newattr", "testattr value");
//        AuthScope scope = new AuthScope(this.host, port);
//        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("raghu", "password");
//
//        CredentialsProvider cp = new BasicCredentialsProvider();
//        cp.setCredentials(scope, creds);
//        HttpContext credContext = new BasicHttpContext();
//        credContext.setAttribute(ClientContext.CREDS_PROVIDER, cp);
		
		
		try {
			ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();
			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}
			BasicHttpRequest request = new BasicHttpRequest("GET", URI);

			BasicHttpProcessor httpproc = new BasicHttpProcessor();
			httpproc.addInterceptor(new RequestContent());
			httpproc.addInterceptor(new RequestTargetHost());
			httpproc.addInterceptor(new RequestConnControl());
			httpproc.addInterceptor(new RequestUserAgent());
			httpproc.addInterceptor(new RequestExpectContinue());

			HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

			Header authHeader = constructAuthHeader(userName, password, URI);
			request.setHeader(authHeader);
			
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);

			HttpEntity entity = response.getEntity();
			XMLHandler xmlHandle = XMLHandlerFactory.getXMLHandler(resType);
			Document dom = XMLHelper.getDocumentFromStream(entity.getContent());
			rsrc = xmlHandle.constructResourceObject(dom);
			// Remove trailing slash
			int i = URI.length()-1;
			for(;i>0 && URI.charAt(i)=='/';i--);
			String tempURI = URI.substring(0, i+1);			
			String nameExtracted = tempURI
					.substring(tempURI.lastIndexOf('/') + 1);
			rsrc.setResourceName(nameExtracted);
			rsrc.setURI(URI);
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode()!=HttpStatus.SC_OK)
			{
				throw new HttpServerException(status.getStatusCode(), status.getReasonPhrase(), null);
			}
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}

		} catch (UnknownHostException e) {
			throw new HttpClientException(e.getMessage(), e);
		} catch (IOException e) {
			throw new HttpClientException(e.getMessage(), e);
		} catch (HttpException e) {
			throw new HttpClientException(e.getMessage(), e);
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				throw new HttpClientException(e.getMessage(), e);
			}
		}

		return rsrc;
	}

	public void executeDelete(String URI) throws HttpClientException, HttpServerException {

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		/*
		 * HttpProcessor httpproc = new ImmutableHttpProcessor( new
		 * HttpRequestInterceptor[] { // Required protocol interceptors new
		 * RequestContent(), new RequestTargetHost(), // Recommended protocol
		 * interceptors new RequestConnControl(), new RequestUserAgent(), new
		 * RequestExpectContinue() });
		 */

		BasicHttpProcessor httpproc = new BasicHttpProcessor();
		httpproc.addInterceptor(new RequestContent());
		httpproc.addInterceptor(new RequestTargetHost());
		httpproc.addInterceptor(new RequestConnControl());
		httpproc.addInterceptor(new RequestUserAgent());
		httpproc.addInterceptor(new RequestExpectContinue());

		HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

		HttpContext context = new BasicHttpContext(null);
		HttpHost host = new HttpHost(this.host, this.port);

		DefaultHttpClientConnection conn = new DefaultHttpClientConnection();
		ConnectionReuseStrategy connStrategy = new DefaultConnectionReuseStrategy();

		context.setAttribute(ExecutionContext.HTTP_CONNECTION, conn);
		context.setAttribute(ExecutionContext.HTTP_TARGET_HOST, host);

		try {

			if (!conn.isOpen()) {
				Socket socket = new Socket(host.getHostName(), host.getPort());
				conn.bind(socket, params);
			}
			BasicHttpRequest request = new BasicHttpRequest("DELETE", URI);
			request.setParams(params);
			
            Header authHeader = constructAuthHeader(userName, password, URI);
            request.setHeader(authHeader);
			
			
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			StatusLine status = response.getStatusLine();
			if(status.getStatusCode()!=HttpStatus.SC_OK)
			{
				throw new HttpServerException(status.getStatusCode(), status.getReasonPhrase(), null);
			}
			httpexecutor.postProcess(response, httpproc, context);
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}

		} catch (IOException e) {
			throw new HttpClientException(e.getMessage(), e);
		} catch (HttpException e) {
			throw new HttpClientException(e.getMessage(), e);
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				throw new HttpClientException(e.getMessage(), e);
			}
		}
	}

	public String getUserName() {
		return userName;
	}

	public static void testGet(HttpHandler httpHandler) throws HttpServerException, HttpClientException {
        Resource resource = httpHandler.executeGET("/kunal/testdir/", RType.DIRECTORY);
        System.out.println(resource);
    }
    private static void testFilePut(HttpHandler httpHandler) throws HttpServerException, HttpClientException {
        File resource = new File();
        resource.setFileName("newfile1.txt");
        resource.setFileSize(100);
        resource.setLastModified(new Date());
        resource.setMimeType("app/pdf");
        resource.setFileContents("I am being uploaded again...".getBytes());
        resource.setURI("/kunal/testdir/subdir/");
        httpHandler.executePUT(resource);
    }
    private static void testDirPut(HttpHandler httpHandler) throws HttpServerException, HttpClientException {
        Directory resource = new Directory();
        resource.setDirName("newdir");
        resource.setURI("/kunal/");
        httpHandler.executePUT(resource);
    }
    
    private static void testLogin(HttpHandler httpHandler) throws HttpClientException, HttpServerException {
        httpHandler.init("localhost", 8089);
        httpHandler.performLogin("kunal", "mypassword");
    }
    
	public static void main(String[] args) {
        System.out.println("Before");
        
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        
	    HttpHandler httpHandler = HttpHandler.getInstance();
        httpHandler.init("localhost", 8089);
        //httpHandler.setUserName("kunal");
        try {
            testLogin(httpHandler);
            //testGet(httpHandler);
			//testDirPut(httpHandler);
	        testFilePut(httpHandler);
		} catch (HttpServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println("After");
    }	
}