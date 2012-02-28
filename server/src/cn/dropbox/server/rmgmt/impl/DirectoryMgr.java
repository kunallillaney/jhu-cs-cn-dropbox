package cn.dropbox.server.rmgmt.impl;

import java.io.File;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.server.listen.ServerListen;
import cn.dropbox.server.rmgmt.api.ResourceMgr;

public class DirectoryMgr implements ResourceMgr {

	public static void main(String[] args) {
		String uri = "Kunal\\Kunal1\\Kunal3";
		DirectoryMgr fmgr = new DirectoryMgr();
		cn.dropbox.common.rmgmt.model.Directory res = new cn.dropbox.common.rmgmt.model.Directory();
		res.setDirName("Kunal3");
		res.setURI(uri);
		fmgr.delete(uri);
		//System.out.println(res.getURI()+" "+res.getDirSize());
	}

	@Override
	public Resource get(String uri) {

		File file = new File(ServerListen.DOCROOT, uri);
		if (file.exists()) {
			cn.dropbox.common.rmgmt.model.Directory dirResource = new cn.dropbox.common.rmgmt.model.Directory();
			dirResource.setDirName(file.getName());
			dirResource.setURI(uri);
			String[] list = file.list();
			for (int i = 0; i < list.length; i++) {
				File tempFile = new File(ServerListen.DOCROOT + uri, list[i]);
				if (tempFile.isDirectory()) {
					cn.dropbox.common.rmgmt.model.Directory tempdirResource = new cn.dropbox.common.rmgmt.model.Directory();
					tempdirResource.setDirName(tempFile.getName());
					tempdirResource.setURI(uri + list[i]);
					tempdirResource.setNumOfElements(tempFile.list().length);
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
			return dirResource;
		}
		return null;
	}

	@Override
	public void put(Resource resource) {
		String uri = resource.getURI();
		cn.dropbox.common.rmgmt.model.Directory dirResource = (cn.dropbox.common.rmgmt.model.Directory) resource;
		File file = new File(ServerListen.DOCROOT + uri,
				dirResource.getDirName());
		file.mkdir();
	}

	@Override
	public void delete(String uri) {
		File file = new File(ServerListen.DOCROOT, uri);
		file.delete();
	}

}