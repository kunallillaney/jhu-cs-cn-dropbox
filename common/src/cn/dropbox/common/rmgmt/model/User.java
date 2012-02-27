package cn.dropbox.common.rmgmt.model;

import cn.dropbox.common.rmgmt.api.Resource;

public class User implements Resource {

	String userName;

	String userid;
	String URI;
	Directory rootDirectory; // TODO: Kunal please remove this. This is not needed!

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

    public Directory getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(Directory rootDirectory) {
        this.rootDirectory = rootDirectory;
    }
	
}
