package cn.dropbox.server.rmgmt.api;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.ResourceAlreadyExistsException;
import cn.dropbox.server.common.api.ResourceDoesNotExistException;
import cn.dropbox.server.common.api.ServerException;

public interface ResourceMgr {

	Resource get(String uri) throws ResourceDoesNotExistException, ForbiddenException, ServerException;
	void put(Resource resource) throws ResourceAlreadyExistsException, ForbiddenException, ServerException;
	void delete(String uri) throws ResourceDoesNotExistException, ForbiddenException, ServerException;
}
