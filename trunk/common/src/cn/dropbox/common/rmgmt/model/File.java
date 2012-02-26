package cn.dropbox.common.rmgmt.model;

import java.util.Date;

import cn.dropbox.common.rmgmt.api.Resource;

public class File implements Resource {

	String fileName;
	String fileSize;
	String reqURL;
	Date lastModified;
	String reqType;
	byte[] fileContents;

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
