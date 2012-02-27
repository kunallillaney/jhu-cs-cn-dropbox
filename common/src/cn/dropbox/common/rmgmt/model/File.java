package cn.dropbox.common.rmgmt.model;

import java.util.Date;

import cn.dropbox.common.rmgmt.api.Resource;

public class File implements Resource {

	String fileName;
	String fileSize;
	String reqURL;
	Date lastModified;
	String mimeType;
    byte[] fileContents;

	@Override
	public RType getType() {
		return RType.FILE;
	}

	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getReqURL() {
        return reqURL;
    }

    public void setReqURL(String reqURL) {
        this.reqURL = reqURL;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public byte[] getFileContents() {
        return fileContents;
    }

    public void setFileContents(byte[] fileContents) {
        this.fileContents = fileContents;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "<"+fileName+">|" +
        		"<"+fileSize+">|" +
        		"<"+mimeType+">|" +
        		"<"+reqURL+">";
    }
	
}
