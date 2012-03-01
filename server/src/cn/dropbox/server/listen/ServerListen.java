package cn.dropbox.server.listen;

public class ServerListen {

	public static final String DOCROOT = "C:\\JHU\\Sem2\\03 Computer Networks\\ProgrammingAssignments\\01Dropbox\\testspace";
	public static void main(String[] args) throws Exception {
		
        System.setProperty("javax.net.ssl.keyStore", "C:\\JHU\\Sem2\\keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeit");
	    
	    
		Thread t = new ReqtListenerThread(8080, DOCROOT);
		t.setDaemon(false);
		t.start();
	}
}
