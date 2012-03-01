package cn.dropbox.server.common.api;

import cn.dropbox.common.rmgmt.model.UserDetails;

public class UserThreadLocal {

    private static final ThreadLocal<UserDetails> user = new ThreadLocal<UserDetails>();
    
    public static void setUser(UserDetails userDetails) {
        user.set(userDetails);
    }
    
    public static UserDetails getUser() {
        return user.get();
    }
    
    public static void removeUser() {
        user.remove();
    }
    
}
