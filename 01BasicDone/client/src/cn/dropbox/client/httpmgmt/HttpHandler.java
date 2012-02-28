package cn.dropbox.client.httpmgmt;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpClientConnection;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.w3c.dom.Document;

import cn.dropbox.client.parser.XMLHandlerFactory;
import cn.dropbox.common.parser.api.XMLHandler;
import cn.dropbox.common.parser.impl.XMLHelper;
import cn.dropbox.common.rmgmt.api.Resource;
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

	private Object authHeader; // TODO: Change the type of this object

	private static HttpHandler handler = null;

	public static synchronized HttpHandler getInstance() {
		if (handler == null) {
			handler = new HttpHandler();
		}
		return handler;
	}

	public void init(String userName, Object authHeader, String host, int port) {
		this.userName = userName;
		this.authHeader = authHeader;
		this.host = host;
		this.port = port;
	}

	public void executePUT(Resource resource) {

		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpProcessor httpproc = new ImmutableHttpProcessor(
				new HttpRequestInterceptor[] {
						// Required protocol interceptors
						new RequestContent(), new RequestTargetHost(),
						// Recommended protocol interceptors
						new RequestConnControl(), new RequestUserAgent(),
						new RequestExpectContinue() });

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
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Resource executeGET(String URI, RType resType) {
		Resource rsrc = null;

		HttpParams params = new SyncBasicHttpParams();
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
			BasicHttpRequest request = new BasicHttpRequest("GET", URI);

			HttpRequestExecutor httpexecutor = new HttpRequestExecutor();

			HttpProcessor httpproc = new ImmutableHttpProcessor(
					new HttpRequestInterceptor[] {
							// Required protocol interceptors
							new RequestContent(), new RequestTargetHost(),
							// Recommended protocol interceptors
							new RequestConnControl(), new RequestUserAgent(),
							new RequestExpectContinue() });
			request.setParams(params);
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);

			HttpEntity entity = response.getEntity();
			XMLHandler xmlHandle = XMLHandlerFactory.getXMLHandler(resType);
			Document dom = XMLHelper.getDocumentFromStream(entity.getContent());
			rsrc = xmlHandle.constructResourceObject(dom);
			rsrc.setURI(URI);
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return rsrc;
	}

	public void executeDelete(String URI) {

		HttpParams params = new SyncBasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, "UTF-8");
		HttpProtocolParams.setUserAgent(params, "HttpComponents/1.1");
		HttpProtocolParams.setUseExpectContinue(params, true);

		HttpProcessor httpproc = new ImmutableHttpProcessor(
				new HttpRequestInterceptor[] {
						// Required protocol interceptors
						new RequestContent(), new RequestTargetHost(),
						// Recommended protocol interceptors
						new RequestConnControl(), new RequestUserAgent(),
						new RequestExpectContinue() });

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
			httpexecutor.preProcess(request, httpproc, context);
			HttpResponse response = httpexecutor
					.execute(request, conn, context);
			response.setParams(params);
			httpexecutor.postProcess(response, httpproc, context);
			if (!connStrategy.keepAlive(response, context)) {
				conn.close();
			} else {
				System.out.println("Connection kept alive...");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Object getAuthHeader() {
		return authHeader;
	}

	public void setAuthHeader(Object authHeader) {
		this.authHeader = authHeader;
	}

}
