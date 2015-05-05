package com.jhy.selfdo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	protected static final int THREAD = 0;
	Button bt,bt_s,bt_sd;
	Button bt_datachmod,yunosettings,ota,ota_sdcard,ota_data;
	TextView tx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		bt = (Button) findViewById(R.id.button_start);
		bt_s = (Button) findViewById(R.id.button_statusbar);
		bt_sd = (Button) findViewById(R.id.button_statusbardata);
		yunosettings = (Button) findViewById(R.id.yunosettings);
		ota = (Button) findViewById(R.id.ota);
		ota_sdcard = (Button) findViewById(R.id.ota_sdcard);
		ota_data = (Button) findViewById(R.id.ota_data);
		
		
		tx = (TextView) findViewById(R.id.textView_judge);
		bt_datachmod = (Button) findViewById(R.id.button_chmod);

		bt.setOnClickListener(this);
		bt_s.setOnClickListener(this);
		bt_sd.setOnClickListener(this);
		bt_datachmod.setOnClickListener(this);
		ota_sdcard.setOnClickListener(this);
		ota_data.setOnClickListener(this);
		
		yunosettings.setOnClickListener(this);
		ota.setOnClickListener(this);

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onPause");
		super.onPause();
	}

	@Override
	protected void onPostResume() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onPostResume");
		super.onPostResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onRestart");
		super.onRestart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean hasWindowFocus() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "hasWindowFocus");
		return super.hasWindowFocus();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onBackPressed");
		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onKeyUp");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.i(this.getClass().getName(), "onStop");
		super.onStop();
	}

	private void showNotification() {
		try {
			/*Object service = getSystemService("statusbar");
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			int versionNum = android.os.Build.VERSION.SDK_INT;
			Method expand = null;
			if (versionNum < 17)
				expand = statusBarManager.getMethod("expand");
			else
				expand = statusBarManager.getMethod("expandNotificationsPanel");

			if (expand != null) {
				expand.setAccessible(true);
				expand.invoke(service);
			}*/

			Object service = getSystemService("statusbar");
			/*Class<?> statusBarManager = Class
					.forName("com.android.server.StatusBarManagerService");*/
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			
			int versionNum = android.os.Build.VERSION.SDK_INT;
			Method expand = null;
			if (versionNum < 17)
				expand = statusBarManager.getMethod("expand");
			else
				expand = statusBarManager.getMethod("expandNotificationsPanel");

			if (expand != null) {
				expand.setAccessible(true);
				expand.invoke(service);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();
			System.err.println("ִ�д���");
		}
	}
	
	private void showNotificationListData() {
		try {

			Object service = getSystemService("statusbar");
			/*Class<?> statusBarManager = Class
					.forName("com.android.server.StatusBarManagerService");*/
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			
			int versionNum = android.os.Build.VERSION.SDK_INT;
			Method expand = null;
			expand = statusBarManager.getMethod("getNotifyList");
			
			Method[] ms = statusBarManager.getDeclaredMethods();
			for(Method m:ms){

				System.err.println(m.getName());
			}

			if (expand != null) {
				expand.setAccessible(true);
				List<Notification> list = (List<Notification>) expand.invoke(service);
				
				//expand.invoke(service);

				System.err.println(list.size());
				
				for(Notification n:list){

					System.err.println(n.toString());
					System.err.println(n.tickerText);
				}
			}else{
				System.err.println("û�д˷���");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			/*Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();*/
			System.err.println("ִ�д���");
		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0.getId() == R.id.button_start) {
			// startJudge();
			try {
				String resultstr = execCommandArray(new String[] { "sh", "-c",
						"ls sys/nand_driver0/nand_debug" });
				// resultstr = "222";

				if (resultstr.contains("No such")) {
					resultstr += "\n nand1 version!";
				} else {
					resultstr += "\n nand2 version!";
				}
				Message m = mhadler.obtainMessage(THREAD, resultstr);
				m.sendToTarget();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (arg0.getId() == R.id.button_statusbar) {
			
	      
	        
			showNotification();
		} else if (arg0.getId() == R.id.button_statusbardata) {
			showNotificationListData();
		}else if (arg0.getId() == R.id.ota) {
			try {
			RecoverySystem.installPackage(this, new File("/cache/update.zip"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(arg0.getId() == R.id.ota_sdcard){

			try {
			RecoverySystem.installPackage(this, new File("/mnt/sdcard/update.zip"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(arg0.getId() == R.id.ota_data){

			try {
			RecoverySystem.installPackage(this, new File("/data/update.zip"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (arg0.getId() == R.id.yunosettings) {
		
			/*Intent intent1=new Intent(); 
			intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent1.setComponent(new ComponentName("com.android.settings", "com.android.settings.network.wifi.WifiApSettingActivity")); 
			startActivity(intent1); */
			
			/*Intent localIntent3 = new Intent();
	        localIntent3.setAction("android.intent.action.MAIN");
	        localIntent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); 
	        localIntent3.addCategory("android.intent.category.LAUNCHER");
	        localIntent3.setComponent(new ComponentName("com.yunos.account", "com.yunos.account.AccountLoginIndex"));
	        startActivity(localIntent3); */
	        
			Intent localIntent5 = new Intent();
			localIntent5.setAction("android.intent.action.MAIN");
			localIntent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); 
	        //localIntent3.addCategory("android.intent.category.LAUNCHER");
			localIntent5.setComponent(new ComponentName("com.android.settings", "com.yunos.tv.settings.WallPaperSelect"));
	        startActivity(localIntent5); 
		}  else if (arg0.getId() == R.id.button_chmod) {

			try {
				execSuCommand("busybox chmod 777 -R /data");
				// String[]{"sh","-c","getprop|grep ip"});
				String resultstr = execCommandArray(new String[] { "sh", "-c",
						"ls -al |grep data" });
				Message m = mhadler.obtainMessage(THREAD, resultstr);
				m.sendToTarget();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	Handler mhadler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case THREAD:
				tx.setText((String) msg.obj);
				break;
			}
			super.handleMessage(msg);
		}
	};

	class cleanjob implements Runnable {

		@Override
		public void run() {
			try {
				// execCommand("getprop");
				// execCommand("");
				/*
				 * proc =
				 * runtime.exec("chmod 777 -R /mnt/sdcard/DCIM/.thumbnails");
				 * proc.waitFor();
				 */
				// proc = runtime.exec("su -c mkdir /mnt/sdcard/DCIM/ddd");
				// proc = runtime.exec("su -c rm -rf /mnt/sdcard/DCIM/dd");

				execCommandArray(new String[] { "sh", "-c", "getprop|grep ip" });

				execSuCommand("rm -rf /mnt/sdcard/DCIM/.thumbnails");// ɾ������ͼ����
				String resultstr = execCommand("ls -al /mnt/sdcard/DCIM/.thumbnails");
				Message m = mhadler.obtainMessage(THREAD, resultstr);
				m.sendToTarget();

				// execCommand("ls");
			} catch (IOException e) {
				e.printStackTrace();
			}

			/*
			 * Properties p = System.getProperties();
			 * 
			 * StringBuilder strb = new StringBuilder(tx.getText()); for (String
			 * str : vm_property) { System.err.println(p.get(str));
			 * 
			 * strb.append(str+":"+p.get(str)+"\n"); }
			 * 
			 * tx.setText(strb.toString());
			 */
		}

	}

	private void startJudge() {
		// Message m = mhadler.obtainMessage(THREAD, "test");
		// m.sendToTarget();

		// new Thread(new cleanjob()).start();

	}

	public static String execSuCommand(String cmd) throws IOException {

		System.err.println("suִ�п�ʼ");
		Process process = Runtime.getRuntime().exec("su");
		DataOutputStream os = new DataOutputStream(process.getOutputStream());
		os.writeBytes(cmd + "\n");
		os.flush();
		os.writeBytes("exit\n");
		os.flush();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		int read;
		char[] buffer = new char[4096];
		StringBuffer output = new StringBuffer();
		while ((read = reader.read(buffer)) > 0) {
			output.append(buffer, 0, read);
		}
		reader.close();
		os.close();

		System.err.println("suִ�н���");
		return output.toString();
	}

	public String execCommand(String command) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);

		System.err.println("ִ�п�ʼ");

		try {
			System.err.println("ִ�п�ʼ2");

			if (proc.waitFor() != 0) {

				System.err.println("exit value1 = " + proc.exitValue());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(proc.getErrorStream()));
				int read;
				char[] buffer = new char[4096];
				StringBuffer output = new StringBuffer();
				while ((read = reader.read(buffer)) > 0) {
					output.append(buffer, 0, read);
				}
				reader.close();

				System.out.println(output.toString());
				return output.toString();

			} else {

				System.err.println("ִ�����");
			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();

			System.out.println(output.toString());
			return output.toString();

		} catch (InterruptedException e) {

			System.err.println(e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();
		}

	}

	public String execCommandArray(String[] command) throws IOException {
		Runtime runtime = Runtime.getRuntime();

		// Process proc = runtime.exec(new
		// String[]{"sh","-c","getprop|grep ip"});
		Process proc = runtime.exec(command);

		System.err.println("execCommandArray��ʼ");
		try {

			if (proc.waitFor() != 0) {

				System.err.println("exit value = " + proc.exitValue());

				if (proc.exitValue() == 1) {

					System.err.println("execCommandArrayִ�з����޽�����߲�ѯΪ��");
				}

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(proc.getErrorStream()));
				int read;
				char[] buffer = new char[4096];
				StringBuffer output = new StringBuffer();
				while ((read = reader.read(buffer)) > 0) {
					output.append(buffer, 0, read);
				}
				reader.close();

				System.out.println(output.toString());
				return output.toString();

			} else {
				System.err.println("execCommandArray����");

			}

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			int read;
			char[] buffer = new char[4096];
			StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			reader.close();

			System.out.println(output.toString());
			return output.toString();

		} catch (InterruptedException e) {

			System.err.println(e);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return sw.toString();

		}

	}

}
