package cn.dropbox.common.rmgmt.model;

import java.io.File;
import java.util.List;
import cn.dropbox.common.rmgmt.api.Resource;

public class Directory implements Resource {

	String dirName;
	List<File> files;
	List<Directory> directories;

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
