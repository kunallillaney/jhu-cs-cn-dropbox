package cn.dropbox.common.rmgmt.model;

import java.io.File;
import java.util.List;
import cn.dropbox.common.rmgmt.api.Resource;

public class Directory implements Resource {

	String dirName;
	String URI;
	List<File> files;
	List<Directory> directories;
    
	public RType getType() {
	    return RType.DIRECTORY;
    }
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
    
}
