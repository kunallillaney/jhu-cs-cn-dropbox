package cn.dropbox.server.listen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import cn.dropbox.server.common.api.UserSessions;

public class ServerListen {

	public static String DOCROOT = null;
	
    public static Properties userPswds;
	
	public static void main(String[] args) {
		
		String docRootEnv = System.getenv("DOC_ROOT");
		
		if(docRootEnv == null || docRootEnv.isEmpty()){
			System.err.println("DOC_ROOT NOT SET");
			System.exit(1);
		}
		
		ServerListen.DOCROOT=docRootEnv;
		
		// initialize username passwords
		Properties userNamePasswdProps = new Properties();
		FileInputStream inStream;
        try {
            inStream = new FileInputStream(DOCROOT+File.separator+"users.pswd");
            userNamePasswdProps.load(inStream);
            inStream.close();
            userPswds = userNamePasswdProps;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
		
		Thread t;
        try {
            t = new ReqtListenerThread(8080, DOCROOT);
            t.setDaemon(false);
            t.start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
	}
}
