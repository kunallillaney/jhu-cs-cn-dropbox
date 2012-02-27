package cn.dropbox.server.listen;

public class ServerListen {

	public static void main(String[] args) throws Exception {
		
		final String docroot = "C:\\Users\\klillaney\\workspace\\Test10\\src\\";
		Thread t = new ReqtListenerThread(8080, docroot);
		t.setDaemon(false);
		t.start();
	}
}
