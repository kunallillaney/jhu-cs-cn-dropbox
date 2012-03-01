package cn.dropbox.server.listen;

public class ServerListen {

	public static final String DOCROOT = "C:\\JHU\\Sem2\\03 Computer Networks\\ProgrammingAssignments\\01Dropbox\\testspace";
	public static void main(String[] args) throws Exception {
		
		Thread t = new ReqtListenerThread(8080, DOCROOT);
		t.setDaemon(false);
		t.start();
	}
}
