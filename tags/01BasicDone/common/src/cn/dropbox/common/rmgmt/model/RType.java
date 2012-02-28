package cn.dropbox.common.rmgmt.model;

public enum RType {
	FILE,
	DIRECTORY,
	USERACCOUNT;
	
	public static RType getRType(String name) {
	    for (RType rtype : RType.values()) {
	        if(rtype.name().equalsIgnoreCase(name)) {
	            return rtype;
	        }
	    }
	    return null;
	}
};
