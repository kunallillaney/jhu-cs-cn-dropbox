package cn.dropbox.server.rmgmt.impl;

import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.common.rmgmt.model.Directory;
import cn.dropbox.server.accessmgmt.AccessMgr;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.ResourceAlreadyExistsException;
import cn.dropbox.server.common.api.ResourceDoesNotExistException;
import cn.dropbox.server.common.api.ServerException;
import cn.dropbox.server.listen.ServerListen;
import cn.dropbox.server.rmgmt.api.ResourceMgr;

public class DirectoryMgr implements ResourceMgr {

	/*
	 * Testing public static void main(String[] args) { String uri =
	 * "Kunal\\Kunal1\\Kunal3"; DirectoryMgr fmgr = new DirectoryMgr();
	 * cn.dropbox.common.rmgmt.model.Directory res = new
	 * cn.dropbox.common.rmgmt.model.Directory(); res.setDirName("Kunal3");
	 * res.setURI(uri); fmgr.delete(uri);
	 * //System.out.println(res.getURI()+" "+res.getDirSize()); }
	 */

	@Override
	public Resource get(String uri) throws ResourceDoesNotExistException ,ForbiddenException ,ServerException {
	    AccessMgr.getInstance().isValidAccess(uri);
		File file = new File(ServerListen.DOCROOT, uri);
		if (file.exists()) {
			cn.dropbox.common.rmgmt.model.Directory dirResource = new cn.dropbox.common.rmgmt.model.Directory();
			dirResource.setDirName(file.getName());
			dirResource.setURI(uri);
			String[] list = file.list();
			for (int i = 0; i < list.length; i++) {
				File tempFile = new File(ServerListen.DOCROOT + uri, list[i]);
				if (tempFile.isDirectory()) {
					Directory tempdirResource = new Directory();
					tempdirResource.setDirName(tempFile.getName());
					tempdirResource.setURI(uri + list[i] + "/");
					tempdirResource.setLastModified(new Date(tempFile.lastModified()));
					dirResource.getDirectories().add(tempdirResource);
				} else {
					cn.dropbox.common.rmgmt.model.File tempfileResource = new cn.dropbox.common.rmgmt.model.File();
					tempfileResource.setFileName(tempFile.getName());
					tempfileResource.setURI(uri + list[i]);
					tempfileResource.setFileSize((int) tempFile.length());
					Date date = new Date(file.lastModified());
					tempfileResource.setLastModified(date);
					MimetypesFileTypeMap mimetype = new MimetypesFileTypeMap();
					tempfileResource.setMimeType(mimetype.getContentType(file));
					dirResource.getFiles().add(tempfileResource);
				}

			}
			for (int i = 0; i < dirResource.getDirectories().size(); i++) {
				Directory subdirresource = dirResource.getDirectories().get(i);
				File tempFile = new File(ServerListen.DOCROOT + uri,
						subdirresource.getDirName());
				String[] sublist = tempFile.list();
				for (int j = 0; j < sublist.length; j++) {
					File subTempFile = new File(ServerListen.DOCROOT + uri + "/" + subdirresource.getDirName(),
							sublist[j]);
					if (subTempFile.isDirectory()) {
						Directory subtempdirResource = new Directory();
						subtempdirResource.setDirName(subTempFile.getName());
						subtempdirResource.setURI(uri + "/" + subdirresource.getDirName() + "/" + sublist[j]);
						subdirresource.getDirectories().add(subtempdirResource);
					} else {
						cn.dropbox.common.rmgmt.model.File subtempfileResource = new cn.dropbox.common.rmgmt.model.File();
						Date date = new Date(file.lastModified());
						subtempfileResource.setLastModified(date);
						subdirresource.getFiles().add(subtempfileResource);
					}
				}
			}
			return dirResource;
		} else {
		    throw new ResourceDoesNotExistException("Specified directory does not exist - "+uri);
		}
	}
	
	
	public void put(Resource resource) throws ResourceAlreadyExistsException, ForbiddenException, ServerException {
		String uri = resource.getURI();
        AccessMgr.getInstance().isValidAccess(uri);
		try {
            File file = new File(ServerListen.DOCROOT + uri,
    				resource.getResourceName());
            if(file.exists()) {
                throw new ResourceAlreadyExistsException("Directory already exists - "+uri);
            }
    		boolean isDirCreated = file.mkdir();
    		if(!isDirCreated) {
                throw new ServerException("Unable to create the directory ["
                        + resource.getResourceName() + "] in URI [" + resource.getURI() + "]");
    		}
		} catch(Throwable t) {
		    throw new ServerException(t.getMessage() + " : Unable to create the directory ["
                        + resource.getResourceName() + "] in URI [" + resource.getURI() + "]", t);
		}
	}

	@Override
	public void delete(String uri) throws ForbiddenException, ResourceDoesNotExistException {
	    AccessMgr.getInstance().isValidAccess(uri);
		File file = new File(ServerListen.DOCROOT, uri);
        if(!file.exists()) {
            throw new ResourceDoesNotExistException("Directory does not exist - " + uri);
        }
		file.delete();
	}

}
