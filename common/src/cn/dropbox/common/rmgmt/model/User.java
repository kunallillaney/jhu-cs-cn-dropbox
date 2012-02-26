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
}
