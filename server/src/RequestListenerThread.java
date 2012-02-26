import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;

public class RequestListenerThread extends Thread {
	
	private final ServerSocket serversocket;
    private final HttpParams params; 
    
    public RequestListenerThread(int port) throws IOException {
    	
    	this.serversocket = new ServerSocket(port);
        this.params = new SyncBasicHttpParams();
        this.params
            .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 5000)
            .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, 8 * 1024)
            .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false)
            .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true)
            .setParameter(CoreProtocolPNames.ORIGIN_SERVER, "HttpComponents/1.1");
    }
    
    public void run() {
        System.out.println("Listening on port " + this.serversocket.getLocalPort());
        while (!Thread.interrupted()) {
            try {
                // Set up HTTP connection
                Socket socket = this.serversocket.accept();
                DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
                System.out.println("Incoming connection from " + socket.getInetAddress());
                conn.bind(socket, this.params);
            } catch (InterruptedIOException ex) {
                break;
            } catch (IOException e) {
                System.err.println("I/O error initialising connection thread: " 
                        + e.getMessage());
                break;
            }
        }
    }// End of Method run
        
}//End of Class
