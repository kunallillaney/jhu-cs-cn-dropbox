import java.io.IOException;


public class ServerListen {

	public static void main(String[] args)throws IOException {
		//Checking for incorrect input
		if(args.length>1) {
			System.err.println("Please specify document root directory");
            System.exit(1);
		}
		//Creating Listener Thread
		Thread t = new RequestListenerThread(8080);
		t.setDaemon(false);
        t.start();
	}
}
