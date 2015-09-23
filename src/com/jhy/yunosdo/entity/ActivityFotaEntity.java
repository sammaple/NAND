package com.jhy.yunosdo.entity;

public class ActivityFotaEntity {

	static public int UPDATAE =0; 
	static public int INSTALL =1; 
	static public int DEL =2; 
	public int  type = UPDATAE;//1 is delete file
	String Id;
	String Name;
	String PackageName;
	String VersionCode;
	String Version;
	public String Size;
	
	
	public ActivityFotaEntity(int type) {
		super();
		this.type = type;
	}


	public int getType() {
		return type;
	}


	public void setType(int type) {
		this.type = type;
	}


	@Override
	public String toString() {
		return "ActivityFotaEntity [type=" + type + ", Id=" + Id + ", Name="
				+ Name + ", PackageName=" + PackageName + ", VersionCode="
				+ VersionCode + ", Version=" + Version + ", Size=" + Size + "]";
	}
	
	
	
}
