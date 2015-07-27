package com.jhy.yunosdo.entity;

public class ActivityFotaEntity {

	String Id;
	String Name;
	String PackageName;
	String VersionCode;
	String Version;
	public String Size;
	
	@Override
	public String toString() {
		return "ActivityFotaEntity [Id=" + Id + ", Name=" + Name
				+ ", PackageName=" + PackageName + ", VersionCode="
				+ VersionCode + ", Version=" + Version + ", Size=" + Size + "]";
	}
	
	
}
