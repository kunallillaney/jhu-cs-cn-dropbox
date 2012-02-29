package cn.dropbox.common.rmgmt.api;

import cn.dropbox.common.rmgmt.model.RType;

public interface Resource {

	String getResourceName();
	
	void setResourceName(String name);
    
    RType getType();

	String getURI();
	
	void setURI(String uri);

}
