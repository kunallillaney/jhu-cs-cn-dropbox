package cn.dropbox.server.accessmgmt;

import java.net.URI;
import java.net.URISyntaxException;

import cn.dropbox.common.rmgmt.model.UserDetails;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.UserThreadLocal;

public class AccessMgr {
    
    private static AccessMgr _instance = null;
    
    public static synchronized AccessMgr getInstance() {
        if(_instance == null) {
            _instance = new AccessMgr();
        }
        return _instance;
    }
    
    public void isValidAccess(String uri) throws ForbiddenException {
        String passedURI = uri;
        try {
            URI baseUri = new URI("");
            
            // Remove all Leading Slashes
            int i = 0;
            for (i = 0; i < uri.length() && uri.charAt(i)=='/'; i++) {
                // Do nothing
            }
            uri = uri.substring(i);
            
            // Resolve against an empty URI
            URI toberesolvedUri = new URI(uri);
            URI resolvedURI = baseUri.resolve(toberesolvedUri);
            String resolvedURIStr = resolvedURI.getPath();
            
            // if(true) return; // TODO: Remove this when Aithentoication is in place.
            
            if(!resolvedURIStr.startsWith(UserThreadLocal.getUser().getUserName()) 
                    || resolvedURIStr.contains("..")) {
                throw new ForbiddenException(ForbiddenException.FORBIDDEN, "URI Passed is invalid or user is not authorized to access the same - "+passedURI);
            } 
        } catch (URISyntaxException e) {
            throw new ForbiddenException(ForbiddenException.FORBIDDEN, e);
        }
    }
    
    public static void main(String[] args) {
        try {
            URI baseUri = new URI("/usrename/");
            URI toberesolvedUri = new URI("mydir/../../toberesolved/");
            System.out.println(baseUri.resolve(toberesolvedUri));
            
            baseUri = new URI("");
            toberesolvedUri = new URI("mydir/../toberesolved/");
            System.out.println(baseUri.resolve(toberesolvedUri));
            
            UserThreadLocal.setUser(new UserDetails("myname"));
            AccessMgr.getInstance().isValidAccess("/myname/testmydir/../..");
            AccessMgr.getInstance().isValidAccess("/myname/../othername/");
            AccessMgr.getInstance().isValidAccess("/myname/../myname/../../");
            AccessMgr.getInstance().isValidAccess("../");
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ForbiddenException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
