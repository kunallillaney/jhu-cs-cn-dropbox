package cn.dropbox.server.reqhandler;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import com.sun.org.apache.xml.internal.security.utils.Base64;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.RType;
import cn.dropbox.common.rmgmt.model.UserDetails;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.ResourceAlreadyExistsException;
import cn.dropbox.server.common.api.ResourceDoesNotExistException;
import cn.dropbox.server.common.api.ServerException;
import cn.dropbox.server.common.api.UserSessions;
import cn.dropbox.server.common.api.UserThreadLocal;
import cn.dropbox.server.listen.ServerListen;
import cn.dropbox.server.parser.XMLUtil;
import cn.dropbox.server.rmgmt.ResourceMagrFactory;
import cn.dropbox.server.rmgmt.api.ResourceMgr;

public class ReqHandler implements HttpRequestHandler {

	// private final String docRoot;

	public ReqHandler(final String docRoot) {
		super();
		// this.docRoot = docRoot;
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
    
	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		/*
		 * if (!method.equals("GET") && !method.equals("DELETE") &&
		 * !method.equals("POST")) { throw new
		 * MethodNotSupportedException(method + " method not supported"); }
		 */
		
		// Check whether there is an authorization header
		// If no, send a 401 and a nonce to the client
		// If yes, verify the response
		// If verification succeeds, overwrite <username, counter> in the map and continue
		// IF verification fails, send 401 and nonce again! 

	    // Check whether there is an authorization header
        Header[] authHeaders = request.getHeaders("Authorization");
        if(authHeaders == null || authHeaders.length == 0) {
            response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            String curValue = "";
            curValue = valueaddNameValuePairToValueElem(curValue, "nonce", UUID.randomUUID().toString());
            Header wwwAuthHeader = new BasicHeader("WWW-Authenticate", curValue);
            response.addHeader(wwwAuthHeader);
            return;
        }
        Header authHeader = authHeaders[0];
        boolean isVerified = verifyResponse(authHeader); // overwrite the counter here only if it is verified. Also set the username in the threadlocal variable.
        if(!isVerified) {
            response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
            String curValue = "";
            curValue = valueaddNameValuePairToValueElem(curValue, "nonce", UUID.randomUUID().toString());
            Header wwwAuthHeader = new BasicHeader("WWW-Authenticate", curValue);
            response.addHeader(wwwAuthHeader);
            return;
        } 
        
		
		String target = request.getRequestLine().getUri();
		try {
    		if (method.equals("GET")) {
    			System.out.println(target);
    			File file = new File(ServerListen.DOCROOT, target);
    			ResourceMgr rmgr = null;
    			if (file.isFile()) {
    				rmgr = ResourceMagrFactory.getResourceMgr(RType.FILE);
    			} else if (file.isDirectory()) {
    				rmgr = ResourceMagrFactory.getResourceMgr(RType.DIRECTORY);
    			}
    
    			StringEntity entity;
                entity = new StringEntity(XMLUtil.constructXML(rmgr
                		.get(target)));
    			response.setStatusCode(HttpStatus.SC_OK);
    			response.setEntity(entity);
    		} else if (method.equals("PUT")) {
    			System.out.println(target);
    			if (request instanceof HttpEntityEnclosingRequest) {
    				HttpEntity entity = ((HttpEntityEnclosingRequest) request)
    						.getEntity();
    				Resource rsrc = XMLUtil.constructResource(entity.getContent());
    				rsrc.setURI(target);
    				ResourceMgr rmgr = null;
    				if (rsrc.getType() == RType.FILE) {
    					rmgr = ResourceMagrFactory.getResourceMgr(RType.FILE);
    				} else if (rsrc.getType() == RType.DIRECTORY) {
    					rmgr = ResourceMagrFactory.getResourceMgr(RType.DIRECTORY);
    				} else if (rsrc.getType() == RType.USERACCOUNT) {
    					rmgr = ResourceMagrFactory
    							.getResourceMgr(RType.USERACCOUNT);
    				}
    				rmgr.put(rsrc);
    				response.setStatusCode(HttpStatus.SC_CREATED);
    			}
    
    		} else if (method.equals("DELETE")) {
    			File file = new File(ServerListen.DOCROOT, target);
    			ResourceMgr rmgr = null;
    			if (file.isFile()) {
    				rmgr = ResourceMagrFactory.getResourceMgr(RType.FILE);
    			} else if (file.isDirectory()) {
    				rmgr = ResourceMagrFactory.getResourceMgr(RType.DIRECTORY);
    			}
    
    			rmgr.delete(target);
    			response.setStatusCode(HttpStatus.SC_OK);
    		}
        } catch (ResourceDoesNotExistException e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.SC_NOT_FOUND);
        } catch (ForbiddenException e) {
            e.printStackTrace();
            int respCode = (e.getCode() == ForbiddenException.FORBIDDEN) ? HttpStatus.SC_FORBIDDEN : HttpStatus.SC_UNAUTHORIZED; 
            response.setStatusCode(respCode);
        } catch (ServerException e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
        } catch (ResourceAlreadyExistsException e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        } catch (Throwable t) {
            // Any other client side errors
            t.printStackTrace();
            response.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        }

		/*
		 * if (request instanceof HttpEntityEnclosingRequest) { HttpEntity
		 * entity = ((HttpEntityEnclosingRequest) request) .getEntity(); byte[]
		 * entityContent = EntityUtils.toByteArray(entity);
		 * System.out.println("Incoming entity content (bytes): " +
		 * entityContent.length); } EntityTemplate body = new EntityTemplate(new
		 * ContentProducer() {
		 * 
		 * public void writeTo(final OutputStream outstream) throws IOException
		 * { OutputStreamWriter writer = new OutputStreamWriter(outstream,
		 * "UTF-8"); writer.write("HIHIHIHIHI"); }
		 * 
		 * }); body.setContentType("text/html; charset=UTF-8");
		 * response.setEntity(body); System.out.println("Response complete"); /*
		 * final File file = new File(this.docRoot, URLDecoder.decode(target));
		 * if (!file.exists()) {
		 * 
		 * response.setStatusCode(HttpStatus.SC_NOT_FOUND); EntityTemplate body
		 * = new EntityTemplate(new ContentProducer() {
		 * 
		 * public void writeTo(final OutputStream outstream) throws IOException
		 * { OutputStreamWriter writer = new OutputStreamWriter( outstream,
		 * "UTF-8"); writer.write("<html><body><h1>"); writer.write("File ");
		 * writer.write(file.getPath()); writer.write(" not found");
		 * writer.write("</h1></body></html>"); writer.flush(); }
		 * 
		 * }); body.setContentType("text/html; charset=UTF-8");
		 * response.setEntity(body); System.out.println("File " + file.getPath()
		 * + " not found");
		 * 
		 * } else if (!file.canRead() || file.isDirectory()) {
		 * 
		 * response.setStatusCode(HttpStatus.SC_FORBIDDEN); EntityTemplate body
		 * = new EntityTemplate(new ContentProducer() {
		 * 
		 * public void writeTo(final OutputStream outstream) throws IOException
		 * { OutputStreamWriter writer = new OutputStreamWriter( outstream,
		 * "UTF-8"); writer.write("<html><body><h1>");
		 * writer.write("Access denied"); writer.write("</h1></body></html>");
		 * writer.flush(); }
		 * 
		 * }); body.setContentType("text/html; charset=UTF-8");
		 * response.setEntity(body); System.out.println("Cannot read file " +
		 * file.getPath());
		 * 
		 * } else {
		 * 
		 * response.setStatusCode(HttpStatus.SC_OK); FileEntity body = new
		 * FileEntity(file, "text/html"); response.setEntity(body);
		 * System.out.println("Serving file " + file.getPath());
		 * 
		 * }
		 */
	}

    private boolean verifyResponse(Header authHeader) {
        String userName = getValue(authHeader, "username");
        if(ServerListen.userPswds.get(userName) == null) {
            return false;
        }
        
        String serverNonce = getValue(authHeader, "nonce");
        String uri = getValue(authHeader, "uri");
        String ncStr = getValue(authHeader, "nc");
        String cnonce = getValue(authHeader, "cnonce");
        String response = getValue(authHeader, "response");
        
        
        // Compute the response 
        String ha1Str = userName+":"+ServerListen.userPswds.get(userName);
        String ha2Str = uri; // "GET:"+URI; 
        // generate cnonce
        String midStr = serverNonce+":"+ncStr+":"+cnonce;
        String finalMd5Str = md5(md5(ha1Str) + ":" + midStr + ":" + md5(ha2Str));
        
        boolean isVerified = finalMd5Str.equals(response);
        if(isVerified) {
            UserThreadLocal.setUser(new UserDetails(userName));
        }
        return isVerified;
        
    }
    
    private String getValue(Header authHeader, String name) {
        HeaderElement[] elements = authHeader.getElements();
        for (int i = 0; i < elements.length; i++) {
            if(name.equals(elements[i].getName())) {
                return elements[i].getValue();
            }
        }
        return null;
    }
    
    private String md5(String str) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA1");
            digest.update(str.getBytes());
            byte[] md5Bytes = digest.digest();
            return new String(Base64.encode(md5Bytes));
        } catch (NoSuchAlgorithmException e) {
            // TODO : Ignore as of now
            e.printStackTrace();
        }
        return null;
    }    
}