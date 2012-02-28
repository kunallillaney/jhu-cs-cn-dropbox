package cn.dropbox.common.rmgmt.api;

import cn.dropbox.common.rmgmt.model.RType;

public interface Resource {

	String getResourceName();
    
    RType getType();

	String getURI();

}
