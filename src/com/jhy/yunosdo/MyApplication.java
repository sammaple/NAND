package com.jhy.yunosdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

public class MyApplication extends Application {
	
	static public String TAG = "YunosSelfDo";

	private HashMap<String, String> map = new HashMap<String, String>();

	public HashMap<String, String> getMap() {
		return map;
	}

	public void setMap(HashMap<String, String> map) {
		this.map = map;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

}
