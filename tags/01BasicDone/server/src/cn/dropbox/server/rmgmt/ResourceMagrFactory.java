package cn.dropbox.server.rmgmt;

import cn.dropbox.common.rmgmt.model.RType;
import cn.dropbox.server.rmgmt.api.ResourceMgr;
import cn.dropbox.server.rmgmt.impl.DirectoryMgr;
import cn.dropbox.server.rmgmt.impl.FileMgr;
import cn.dropbox.server.rmgmt.impl.UserMgr;

public class ResourceMagrFactory {

	public static ResourceMgr getResourceMgr(RType resourceType) {
		ResourceMgr rmgr = null;
		switch (resourceType) {
		case FILE:
			rmgr = new FileMgr();
			break;
		case DIRECTORY:
			rmgr = new DirectoryMgr();
			break;
		case USERACCOUNT:
			rmgr = new UserMgr();
			break;
		}
		return rmgr;
	}
}
