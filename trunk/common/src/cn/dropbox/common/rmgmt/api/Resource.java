package cn.dropbox.common.rmgmt.api;

import cn.dropbox.common.rmgmt.model.RType;

public interface Resource {

	RType getType();

	String getURI();

}
