package cn.dropbox.common.rmgmt.model;

import cn.dropbox.common.rmgmt.api.Resource;

public class User implements Resource {

	String userName;
	String userid;
	String URI;

    @Override
    public String getResourceName() {
        return getUserName();
    }
	
	@Override
	public RType getType() {
	    return RType.USERACCOUNT;
	}

	@Override
	public String getURI() {
	    return URI;
	}

    public void setURI(String uRI) {
        URI = uRI;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
	
}
