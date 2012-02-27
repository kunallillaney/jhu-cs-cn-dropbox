package cn.dropbox.common.rmgmt.model;

import cn.dropbox.common.rmgmt.api.Resource;

public class User implements Resource {

	String userName;

	String userid;

	Directory rootDirectory;

	@Override
	public RType getType() {

		return null;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
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
