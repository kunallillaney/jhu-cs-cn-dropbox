package cn.dropbox.server.reqhandler;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.RType;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.ResourceAlreadyExistsException;
import cn.dropbox.server.common.api.ResourceDoesNotExistException;
import cn.dropbox.server.common.api.ServerException;
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

	public void handle(final HttpRequest request, final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		String method = request.getRequestLine().getMethod()
				.toUpperCase(Locale.ENGLISH);
		/*
		 * if (!method.equals("GET") && !method.equals("DELETE") &&
		 * !method.equals("POST")) { throw new
		 * MethodNotSupportedException(method + " method not supported"); }
		 */
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
}