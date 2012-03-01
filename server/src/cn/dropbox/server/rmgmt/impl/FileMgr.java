package cn.dropbox.server.rmgmt.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import javax.activation.MimetypesFileTypeMap;

import cn.dropbox.common.rmgmt.api.Resource;
import cn.dropbox.server.accessmgmt.AccessMgr;
import cn.dropbox.server.common.api.ForbiddenException;
import cn.dropbox.server.common.api.ResourceDoesNotExistException;
import cn.dropbox.server.common.api.ServerException;
import cn.dropbox.server.listen.ServerListen;
import cn.dropbox.server.rmgmt.api.ResourceMgr;

public class FileMgr implements ResourceMgr {

	/*
	 * Testing public static void main(String[] args){ String uri =
	 * "Kunal\\test1.txt"; FileMgr fmgr = new FileMgr();
	 * cn.dropbox.common.rmgmt.model.File res = new
	 * cn.dropbox.common.rmgmt.model.File(); res.setURI(uri);
	 * res.setFileName("test1"); //byte[] buf = new byte[10];
	 * //res.setFileContents(buf); fmgr.delete(uri);
	 * //System.out.println(res.getLastModified()+" "+ res.getFileSize()); }
	 */

	@Override
	public Resource get(String uri) throws ResourceDoesNotExistException ,ForbiddenException ,ServerException {
	    AccessMgr.getInstance().isValidAccess(uri);
		File file = new File(ServerListen.DOCROOT, uri);
		if (file.exists()) {
			cn.dropbox.common.rmgmt.model.File fileResource = new cn.dropbox.common.rmgmt.model.File();
			fileResource.setFileName(file.getName());
			fileResource.setFileSize((int) file.length());
			fileResource.setURI(uri);
			Date date = new Date(file.lastModified());
			fileResource.setLastModified(date);

			// Reading File
			FileInputStream fstream;
			try {
				fstream = new FileInputStream(file);
				byte[] buf = new byte[(int) file.length()];
				fstream.read(buf);
				fileResource.setFileContents(buf);
				fstream.close();
			} catch (FileNotFoundException e) {
			    throw new ResourceDoesNotExistException(e);
			} catch (IOException e) {
			    throw new ServerException(e);
			}

			// Reading MIME type
			MimetypesFileTypeMap mimetype = new MimetypesFileTypeMap();
			fileResource.setMimeType(mimetype.getContentType(file));

			return fileResource;
		} else {
            throw new ResourceDoesNotExistException(
                    "Specified file does not exist - " + uri);
		}
	}

	@Override
	public void put(Resource resource) throws ForbiddenException, ServerException {
	    AccessMgr.getInstance().isValidAccess(resource.getURI());
	    String uri = resource.getURI();
		cn.dropbox.common.rmgmt.model.File fileResource = (cn.dropbox.common.rmgmt.model.File) resource;
		File file = new File(ServerListen.DOCROOT + uri, fileResource.getFileName());

		try {
			file.createNewFile();
			FileOutputStream out = new FileOutputStream(file);
			out.write(fileResource.getFileContents());
			out.close();
		} catch (IOException e) {
            throw new ServerException(e.getMessage() + " : Unable to create the File ["
                    + resource.getResourceName() + "] in URI [" + resource.getURI() + "]", e);
		}
	}

	@Override
	public void delete(String uri) throws ForbiddenException, ResourceDoesNotExistException {
	    AccessMgr.getInstance().isValidAccess(uri);
		File file = new File(ServerListen.DOCROOT, uri);
		if(!file.exists()) {
		    throw new ResourceDoesNotExistException("File does not exist - " + uri);
		}
		file.delete();
	}

}
