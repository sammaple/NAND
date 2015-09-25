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
import java.util.ArrayList;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jhy.yunosdo.entity.ActivityFotaEntity;
import com.jhy.yunosdo.filemanager.MyFileManager;
import com.jhy.yunosdo.utils.Util;
import com.yunos.fotasdk.httpxml.HttpService;
import com.yunos.fotasdk.model.HttpXmlParams;

public class MainActivity extends Activity implements OnClickListener {

	public static final String TAG = "yunosdo";
	
	
	protected static final int THREAD = 0;
	protected static final int FOTA = 1;
	protected static final int FOTA_SHOW = 2;
	protected static final int FOTA_UPLOAD = 3;
	protected static final int UI_UPDATE_THREAD = 4;
	
	
	public String FOTAINFO_PATH = "";
	
	//R28 25455161A6D2190A6B9C6B2501E131F2
	public String[] preDefine ={
			"YBKJ_R28_1.8.0-R-20141223.1400_1_Up.xml",
			"YBKJ_R28_1.8.0-R-20150120.1954_2_Up.xml",
			"YBKJ_M100_1.8.0-R-20141230.1756_Up.xml",
			"YBKJ_AK5_1.8.0-R-20141230.1318_Up.xml",
			"XMATE_R31_1.8.0-R-20141218.1036_Up.xml",
			"10MOONS_T2Q_1.7.4-R-20140811.1722_Up.xml",
			"PULIER_A31S_1.7.4-R-20141103.2304_Up.xml",
			"PULIER_A31S_1.7.4-R-20141103.2304_aftermini_Up.xml",
			"PULIER_A29_XX_1.8.0-R-20150116.1746_Up.xml",
			"PULIER_A29_XX_1.8.0-R-20150508.1132_Up.xml"
			
	};

	private static final int FILE_RESULT_CODE = 0;
	
	Button bt,bt_s,bt_sd;
	Button bt_datachmod,yunosettings,ota,ota_sdcard,ota_data,yuno_fotainfo,ota_to_data,yuno_fotaupload,yuno_fotainfo_pre;
	Button yuno_fotainfo_pre_s;
	Button ota_clear;
	Button ota_to_sdcard;
	TextView tx;
	Context ctx;

	ArrayAdapter<String> adapter;
	Spinner sp;
	
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
		
		FOTAINFO_PATH = getFilesDir().getPath();
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
		yuno_fotaupload = (Button) findViewById(R.id.yuno_fotaupload);
		yuno_fotainfo_pre = (Button) findViewById(R.id.yuno_fotainfo_pre);
		ota_clear = (Button) findViewById(R.id.ota_clear);
		yuno_fotainfo_pre_s = (Button) findViewById(R.id.yuno_fotainfo_pre_s);
		
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
		yuno_fotaupload.setOnClickListener(this);
		yuno_fotainfo_pre.setOnClickListener(this);
		ota_clear.setOnClickListener(this);
		yuno_fotainfo_pre_s.setOnClickListener(this);

		sp = (Spinner) findViewById(R.id.spinner1);
		new Thread(new ScanDataJob()).start();// get kl files

	}
	
	public   String   inputStream2String   (InputStream   in)   throws   IOException   { 
        StringBuffer   out   =   new   StringBuffer(); 
        byte[]   b   =   new   byte[4096]; 
        for   (int   n;   (n   =   in.read(b))   !=   -1;)   { 
                out.append(new   String(b,   0,   n)); 
        } 
        return   out.toString(); 
} 
	
	//osupdate配置文件扫描
	class ScanDataJob implements Runnable {

		@Override
		public void run() {
			InputStream in = null;
			for (String path : preDefine) {

				try {
					in = ctx.getAssets().open(path);
					if(in != null){
						String parameters = inputStream2String(in);
						((MyApplication)(ctx.getApplicationContext())).getMap().put(path,parameters);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					in =null;
				}
			}
			
			
			Message m = mhadler.obtainMessage(UI_UPDATE_THREAD, "");
			m.sendToTarget();
		}
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
		}  else if(arg0.getId() == R.id.yuno_fotainfo){//get app fota infos
			
			Message m = mhadler.obtainMessage(FOTA, "test");
			m.sendToTarget();
			
		} else if(arg0.getId() == R.id.yuno_fotainfo_pre){

			Message m = mhadler.obtainMessage(FOTA, "pre");//pre 
			m.sendToTarget();
		}else if(arg0.getId() == R.id.yuno_fotainfo_pre_s){

			Message m = mhadler.obtainMessage(FOTA, "pre_noimei");//pre  不带imei
			m.sendToTarget();
		}
		else if(arg0.getId() == R.id.yuno_fotaupload){

			Message m = mhadler.obtainMessage(FOTA_UPLOAD, "test");
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
		else if(arg0.getId() == R.id.ota_clear){
			Util.delOTA(ctx);
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
				if(msg.obj == "test"){
					new Thread(new FotaInfo(0,null)).start();
				}else if( msg.obj == "pre_noimei"){
					String parameters =( (MyApplication)ctx.getApplicationContext()).getMap().get((String)sp.getSelectedItem());
					new Thread(new FotaInfo(2, parameters)).start();
				}else{
					String parameters =( (MyApplication)ctx.getApplicationContext()).getMap().get((String)sp.getSelectedItem());
					new Thread(new FotaInfo(1, parameters)).start();
				}
				break;
			case UI_UPDATE_THREAD:
				//tx.setText((String) msg.obj);
				//update insert data
				List<String> namelist =new ArrayList<String>();
				namelist.addAll(((MyApplication) ctx.getApplicationContext()).getMap().keySet());
				
				adapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_checked, namelist);    
		        //第三步：为适配器设置下拉列表下拉时的菜单样式。    
		        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);    
		        //第四步：将适配器添加到下拉列表上    
		        sp.setAdapter(adapter);    
				break;
			case FOTA_UPLOAD:
				new Thread(new FotaUpload()).start();
				break;
			}
			super.handleMessage(msg);
		}
	};
	

	/**
	 * FOTA 环境变量收集显示
	 */
    private void enterUploadFotaInfo() {
        // 显示进度Dialog
        /*ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setMessage("请稍后，正在收集信息并上传......");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.show();
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(MainActivity.this, "收集完成", Toast.LENGTH_SHORT).show();
            }
        });*/

		try {
			Gson gson = new Gson();
	    	Map<String, String> parameters = buildAppCheckParams();
	    	String final_gson = gson.toJson(parameters);
	    	
			SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");  
	        String t=format.format(new Date());

	        String version = Util.getVersion();
	        
			String filename = getFilesDir().getPath()+File.separator+Build.MODEL+"_"+version+"_"+t +"_Up.xml";
			File file = new File(filename);
			FileOutputStream fs = new FileOutputStream( file );
			
			fs.write(final_gson.getBytes("UTF-8"));
			fs.flush();
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

        String model = Util.getModel();
        String version = Util.getVersion();
        String uuid = Util.getUUID();
        String flag = "FOTAINFO";
        // 获取 zip 包 并上传
        Util.getZip(null, flag, model, version, uuid,FOTAINFO_PATH);

    }
    

	class FotaUpload implements Runnable {

		@Override
		public void run() {
			enterUploadFotaInfo();
		}
		
	}
    

    /**
     * 模拟发送FOTA APP query,并解析显示
     * @author juling.jhy
     *
     */
	class FotaInfo implements Runnable {

		public FotaInfo(int flag, String parameters) {
			super();
			this.flag = flag;
			this.parameters = parameters;
		}
		
		int flag = 0;//0 is local variable,1 is prebuild variable,2 is prebuild variable without imei
		String parameters = "";
		
		@Override
		public void run() {

			HttpService hs =new HttpService();
			HttpXmlParams xmlParams = new HttpXmlParams();
			
			try {
				String str = "";
				String uuid= "";
				if(flag ==0){
					Map<String,String> map  = buildAppCheckParams();
					uuid = map.get("imei");
					str = hs.doPost("https://osupdateservice.yunos.com/update/manifest", map, xmlParams);
				}else{

					Gson gson = new Gson();
					Map<String,String> map =  gson.fromJson(parameters, new TypeToken<Map<String,String>>(){}.getType());
					uuid = map.get("imei");
					if(flag ==2){// without imei
						map.put("imei","false");
					}
					str = hs.doPost("https://osupdateservice.yunos.com/update/manifest", map, xmlParams);
				}
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
				parseInfo(new FileInputStream(file),uuid);//list update/install apps info &delete apps info
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	List<ActivityFotaEntity> parseInfo(InputStream in,String uuid){

		List<ActivityFotaEntity> list=new PullParseXml().PullParseXML(in);
		StringBuilder sb = new StringBuilder();
		StringBuilder sb_del = new StringBuilder();
	    float total = (float) 0.0;
        for(ActivityFotaEntity info:list){
		//XmlPullParser p = new 
        	Log.d(MainActivity.TAG, info.toString());
        	if(info.type != ActivityFotaEntity.DEL){
            	sb.append(info.toString()+"\n");
            	if(info.Size != null){
                	total += Float.valueOf(info.Size);
            	}	
        	}else{
        		sb_del.append(info.toString()+"\n");
        	}
        }

    	sb.append("本次测试UUID："+uuid+"\n");
    	sb.append("总共推送YUNOS应用大小"+total+"M\n");
    	sb.append("卸载YUNOS应用信息：\n");
    	sb.append(sb_del);
    	
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
