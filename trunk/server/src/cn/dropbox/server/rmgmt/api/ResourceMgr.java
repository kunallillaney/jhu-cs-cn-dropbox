package cn.dropbox.server.rmgmt.api;

import cn.dropbox.common.rmgmt.api.Resource;

public interface ResourceMgr {

	Resource get(String uri);
	void put(Resource resource);
	void delete(String uri);
}
