package cn.dropbox.common.rmgmt.model;

public class UserDetails {
    
    String userName;
    
    String userID;
    
    Object authHeader;
    
    public UserDetails(String userName) {
        this.userName = userName;
    }
    
    public UserDetails(String userName, String userID, Object authHeader) {
        this.userName = userName;
        this.userID = userID;
        this.authHeader = authHeader;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Object getAuthHeader() {
        return authHeader;
    }

    public void setAuthHeader(Object authHeader) {
        this.authHeader = authHeader;
    }

}
