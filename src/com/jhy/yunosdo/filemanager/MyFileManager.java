﻿package com.jhy.yunosdo.filemanager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.jhy.yunosdo.MainActivity;
import com.jhy.yunosdo.R;
import com.jhy.yunosdo.utils.Util;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class MyFileManager extends ListActivity {
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/";
	private String curPath = "/";
	private TextView mPath;
	

	private String path = "";//go cp dest

	private final static String TAG = "bb";

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		

		Bundle bundle = this.getIntent().getExtras();  
		path = (String) bundle.get("path");
		
		setContentView(R.layout.fileselect);
		mPath = (TextView) findViewById(R.id.mPath);
		Button buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
		buttonConfirm.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent data = new Intent(MyFileManager.this, MainActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("file", curPath);
				data.putExtras(bundle);
				setResult(2, data);
				finish();

			}
		});
		Button buttonCancle = (Button) findViewById(R.id.buttonCancle);
		buttonCancle.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});
		getFileDir(rootPath);
		
		getListView().setFocusable(true);
	}

	private void getFileDir(String filePath) {
		mPath.setText(filePath);
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		File f = new File(filePath);
		File[] files = f.listFiles();

		if (!filePath.equals(rootPath)) {
			items.add("b1");
			paths.add(rootPath);
			items.add("b2");
			paths.add(f.getParent());
		}
		if(files!=null){
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				items.add(file.getName());
				paths.add(file.getPath());
			}
		}

		setListAdapter(new MyAdapter(this, items, paths));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		//Util.cpToData(MyFileManager.this,"/mnt/usbhost0/1/ota/update.zip",path);

		//Log.i(this.getClass().getName(),  "destpath :"+ path);
		
		File file = new File(paths.get(position));
		if (file.isDirectory()) {
			curPath = paths.get(position);
			getFileDir(paths.get(position));
		} else {
			openFile(file);
		}
	}

	private void openFile(File f) {
		/*Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);*/

		//Util.cpToData(MyFileManager.this,"/mnt/usbhost0/1/ota/update.zip",path);
		Util.cpToData(MyFileManager.this,f.getAbsolutePath(),path);

		Log.i(this.getClass().getName(), f.getAbsolutePath() + "destpath :"+ path);
	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName
				.substring(fName.lastIndexOf(".") + 1, fName.length())
				.toLowerCase();

		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			type = "image";
		} else {
			type = "*";
		}
		type += "/*";
		return type;
	}
}