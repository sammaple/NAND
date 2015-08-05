package com.jhy.yunosdo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jhy.yunosdo.entity.ActivityFotaEntity;
import com.jhy.yunosdo.filemanager.MyFileManager;
import com.yunos.fotasdk.httpxml.HttpService;
import com.yunos.fotasdk.model.HttpXmlParams;

import org.xmlpull.v1.XmlPullParser;

public class MainActivity extends Activity implements OnClickListener {

	public static final String TAG = "yunosdo";
	
	protected static final int THREAD = 0;
	protected static final int FOTA = 1;
	protected static final int FOTA_SHOW = 2;

	private static final int FILE_RESULT_CODE = 0;
	
	Button bt,bt_s,bt_sd;
	Button bt_datachmod,yunosettings,ota,ota_sdcard,ota_data,yuno_fotainfo,ota_to_data;
	Button ota_to_sdcard;
	TextView tx;
	Context ctx;
	
	  View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener()
	  {
	    public void onFocusChange(View paramView, boolean paramBoolean)
	    {
	    	Log.d("jhy", paramView.getId()+":"+paramBoolean);
	    }
	  };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ctx =this;
		bt = (Button) findViewById(R.id.button_start);
		bt_s = (Button) findViewById(R.id.button_statusbar);
		bt_sd = (Button) findViewById(R.id.button_statusbardata);
		bt_sd.setOnFocusChangeListener(onFocusChangeListener);
		
		yunosettings = (Button) findViewById(R.id.yunosettings);
		ota = (Button) findViewById(R.id.ota);
		ota_sdcard = (Button) findViewById(R.id.ota_sdcard);
		ota_data = (Button) findViewById(R.id.ota_data);
		ota_to_data = (Button) findViewById(R.id.ota_to_data);
		ota_to_sdcard =  (Button) findViewById(R.id.ota_to_sdcard);
		yuno_fotainfo  = (Button) findViewById(R.id.yuno_fotainfo);
		
		
		tx = (TextView) findViewById(R.id.textView_judge);
		bt_datachmod = (Button) findViewById(R.id.button_chmod);

		bt.setOnClickListener(this);
		bt_s.setOnClickListener(this);
		bt_sd.setOnClickListener(this);
		bt_datachmod.setOnClickListener(this);
		ota_sdcard.setOnClickListener(this);
		ota_data.setOnClickListener(this);
		ota_to_data.setOnClickListener(this);
		ota_to_sdcard.setOnClickListener(this);
		
		yunosettings.setOnClickListener(this);
		ota.setOnClickListener(this);
		yuno_fotainfo.setOnClickListener(this);

	}

	@Override
	protected void onPause() {
		Log.i(this.getClass().getName(), "onPause");
		super.onPause();
	}

	@Override
	protected void onPostResume() {
		Log.i(this.getClass().getName(), "onPostResume");
		super.onPostResume();
	}

	@Override
	protected void onRestart() {
		Log.i(this.getClass().getName(), "onRestart");
		super.onRestart();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getName(), "onKeyDown");
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean hasWindowFocus() {
		Log.i(this.getClass().getName(), "hasWindowFocus");
		return super.hasWindowFocus();
	}

	@Override
	public void onBackPressed() {
		Log.i(this.getClass().getName(), "onBackPressed");
		super.onBackPressed();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getName(), "onKeyUp");
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onResume() {
		Log.i(this.getClass().getName(), "onResume");
		super.onResume();
	}

	@Override
	protected void onStart() {
		Log.i(this.getClass().getName(), "onStart");
		super.onStart();
	}

	@Override
	protected void onStop() {
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
			System.err.println("执行错误");
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
				System.err.println("没有此方法");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			/*Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_LONG).show();*/
			System.err.println("执行错误");
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
				e.printStackTrace();
			}
		}else if(arg0.getId() == R.id.ota_sdcard){

			try {
			RecoverySystem.installPackage(this, new File("/mnt/sdcard/update.zip"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(arg0.getId() == R.id.ota_data){

			try {
			RecoverySystem.installPackage(this, new File("/data/update.zip"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(arg0.getId() == R.id.ota_to_data){

			Intent intent = new Intent(MainActivity.this,MyFileManager.class);
			Bundle bundle = new Bundle();  
            bundle.putString("path", "/data/"); 
			intent.putExtras(bundle);
			startActivityForResult(intent, FILE_RESULT_CODE);
			
		}else if(arg0.getId() == R.id.ota_to_sdcard){

			Intent intent = new Intent(MainActivity.this,MyFileManager.class);
			Bundle bundle = new Bundle();  
            bundle.putString("path", "/mnt/sdcard/");
			intent.putExtras(bundle);
			startActivityForResult(intent, FILE_RESULT_CODE);
			
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
		}  else if(arg0.getId() == R.id.yuno_fotainfo){
			

			Message m = mhadler.obtainMessage(FOTA, "test");
			m.sendToTarget();
			
			
			
		} else if (arg0.getId() == R.id.button_chmod) {

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
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(FILE_RESULT_CODE == requestCode){
			Bundle bundle = null;
			if(data!=null&&(bundle=data.getExtras())!=null){
				tx.setText("选择文件夹为："+bundle.getString("file"));
			}
		}
	}

	  public String getSystemProperty(String paramString1, String paramString2)
	  {
	    try
	    {
	      String str = (String)Class.forName("android.os.SystemProperties").getMethod("get", new Class[] { String.class, String.class }).invoke(null, new Object[] { paramString1, paramString2 });
	      return str;
	    }
	    catch (Exception localException)
	    {
	    }
	    return paramString2;
	  }
	  
	  private Map<String, String> buildAppCheckParams()
	  {
		  Environment environment = new Environment(ctx);
	    Map<String, String> localMap = buildParams();
	    localMap.put("updateType", environment.getSystemProperty("ro.product.model", null));
	    localMap.put("queryType", String.valueOf(14));
	    List<PackageInfo> localList = getPackageManager().getInstalledPackages(0);
	    StringBuilder localStringBuilder = new StringBuilder();
	    localStringBuilder.append("[");
	    for (int i = 0; i < localList.size(); ++i)
	    {
	      PackageInfo localPackageInfo = (PackageInfo)localList.get(i);
	      localStringBuilder.append("{\"");
	      localStringBuilder.append(localPackageInfo.packageName);
	      localStringBuilder.append("\":\"");
	      localStringBuilder.append(localPackageInfo.versionCode);
	      localStringBuilder.append("\"}");
	      if (i >= -1 + localList.size())
	        continue;
	      localStringBuilder.append(",");
	    }
	    localStringBuilder.append("]");
	    localMap.put("applist", localStringBuilder.toString());
	    return localMap;
	  }

	  private Map<String, String> buildParams()
	  {
		  Environment environment = new Environment(ctx);
	    LinkedHashMap<String, String> localLinkedHashMap = new LinkedHashMap();
	    localLinkedHashMap.put("productType", environment.getProductType());
	    localLinkedHashMap.put("phone", environment.getPhoneType());
	    localLinkedHashMap.put("imei", environment.getDeviceId());
	    localLinkedHashMap.put("system", environment.getSystemVersion());
	    localLinkedHashMap.put("base", environment.getBaseVersion());
	    //if (new FotaUpdateResult(this.context).getInstallFailCount() > 0);
	    for (String str = "unknown"; ; str = environment.getBspVersion())
	    {
	      localLinkedHashMap.put("aliyun", str);
	      //localLinkedHashMap.put("kernel", environment.getKernelVersion());
	      localLinkedHashMap.put("kernel", "null");
	      return localLinkedHashMap;
	    }
	  }
	  
	Handler mhadler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case THREAD:
				tx.setText((String) msg.obj);
				break;
			case FOTA_SHOW:
				tx.setText((String) msg.obj);
				break;
			case FOTA:
				new Thread(new fotainfo()).start();
				break;
			}
			super.handleMessage(msg);
		}
	};

	class fotainfo implements Runnable {

		@Override
		public void run() {

			HttpService hs =new HttpService();
			HttpXmlParams xmlParams = new HttpXmlParams();
			
			try {
				String str = hs.doPost("https://osupdateservice.yunos.com/update/manifest", buildAppCheckParams(), xmlParams);
				Log.d(MainActivity.TAG,str);
				
				SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");  
		        String t=format.format(new Date());
		        
				String filename = getFilesDir().getPath()+File.separator+Build.MODEL+"_"+t +".xml";
				File file = new File(filename);
				FileOutputStream fs = new FileOutputStream( file );
				
				fs.write(str.getBytes("UTF-8"));
				fs.flush();
				fs.close();
				
				Log.d(MainActivity.TAG,"start to parse file!");
				parseInfo(new FileInputStream(file));//
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	List<ActivityFotaEntity> parseInfo(InputStream in){

		List<ActivityFotaEntity> list=new PullParseXml().PullParseXML(in);
		StringBuilder sb = new StringBuilder();
	    float total = (float) 0.0;
        for(ActivityFotaEntity info:list){
		//XmlPullParser p = new 
        	Log.d(MainActivity.TAG, info.toString());
        	sb.append(info.toString()+"\n");
        	if(info.Size != null){
            	total += Float.valueOf(info.Size);
        	}
        }

    	sb.append("总共推送YUNOS应用大小"+total+"M\n");
    	
		Message m = mhadler.obtainMessage(THREAD, sb.toString());
		m.sendToTarget();
		
        return list;
	}
	
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

				execSuCommand("rm -rf /mnt/sdcard/DCIM/.thumbnails");// 删除缩略图区域
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

		System.err.println("su执行开始");
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

		System.err.println("su执行结束");
		return output.toString();
	}

	public String execCommand(String command) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command);

		System.err.println("执行开始");

		try {
			System.err.println("执行开始2");

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

				System.err.println("执行完毕");
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

		System.err.println("execCommandArray开始");
		try {

			if (proc.waitFor() != 0) {

				System.err.println("exit value = " + proc.exitValue());

				if (proc.exitValue() == 1) {

					System.err.println("execCommandArray执行返回无结果或者查询为空");
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
				System.err.println("execCommandArray结束");

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
