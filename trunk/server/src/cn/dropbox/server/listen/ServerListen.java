package cn.dropbox.server.listen;

public class ServerListen {

	public static String DOCROOT = null;
	
	public static void main(String[] args) throws Exception {
		
		String docRootEnv = System.getenv("DOC_ROOT");
		
		if(docRootEnv == null || docRootEnv.isEmpty()){
			System.err.println("DOC_ROOT NOT SET");
			System.exit(1);
		}
		else{
			ServerListen.DOCROOT=docRootEnv;
			Thread t = new ReqtListenerThread(8080, DOCROOT);
			t.setDaemon(false);
			t.start();
		}
		
		
	}
}
