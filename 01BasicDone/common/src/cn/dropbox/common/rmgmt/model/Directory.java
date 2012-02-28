package cn.dropbox.common.rmgmt.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import cn.dropbox.common.rmgmt.api.Resource;

public class Directory implements Resource {

	String dirName;
	String URI;
	Date lastModified;
	int numOfElements = -1;
    public void setNumOfElements(int numOfElements) {
        this.numOfElements = numOfElements;
    }
    List<File> files = new ArrayList<File>();
	List<Directory> directories = new ArrayList<Directory>();
    
    @Override
    public String getResourceName() {
        return getDirName();
    }
    @Override
	public RType getType() {
	    return RType.DIRECTORY;
    }
	@Override
    public String getURI() {
        return URI;
    }
    public void setURI(String uRI) {
        URI = uRI;
    }
    public String getDirName() {
        return dirName;
    }
    public void setDirName(String dirName) {
        this.dirName = dirName;
    }
    public Date getLastModified() {
        return lastModified;
    }
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }    
    public List<File> getFiles() {
        return files;
    }
    public void setFiles(List<File> files) {
        this.files = files;
    }
    public List<Directory> getDirectories() {
        return directories;
    }
    public void setDirectories(List<Directory> directories) {
        this.directories = directories;
    }
    public int getDirSize() {
        if(numOfElements == -1) {
            return getFiles().size() + getDirectories().size();
        } else {
            return numOfElements;
        }
        
    }
    @Override
    public String toString() {
        String retStr =  "<"+dirName+">|" +
        		"<"+getDirSize()+">|" +
        		"<"+URI+">|" +
        		"<"+lastModified+">";
        retStr += "\n[Files:"+files.toString()+"]";
        retStr += "\n[SubDirs:"+directories.toString()+"]";
        return retStr;
        
    }
    public void setLastModified(int year, int month, int dayOfMonth, int hourOfDay, int minute,
            int second) {
        lastModified = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second).getTime();
    }
}
