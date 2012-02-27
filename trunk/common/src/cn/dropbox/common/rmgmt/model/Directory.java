package cn.dropbox.common.rmgmt.model;

import java.io.File;
import java.util.List;
import cn.dropbox.common.rmgmt.api.Resource;

public class Directory implements Resource {

	String dirName;
	List<File> files;
	List<Directory> directories;
    
	public RType getType() {
        // TODO Auto-generated method stub
        return null;
    }
    public String getURI() {
        // TODO Auto-generated method stub
        return null;
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
