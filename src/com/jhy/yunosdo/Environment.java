package com.jhy.yunosdo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Environment
{
  private Context context;
  private StorageManager mStorageManager;
  private PowerManager.WakeLock wakelock = null;

  public Environment(Context paramContext)
  {
    this.context = paramContext;
  }

  public static boolean isHimediaHisi(String paramString)
  {
    return ("Hi3718CV100".equals(paramString)) || ("Hi3718CV200".equals(paramString)) || ("Hi3716CV100".equals(paramString)) || ("Hi3716CV200".equals(paramString));
  }

  public static boolean isHisi3716CV200(String paramString)
  {
    return "Hi3716CV200".equals(paramString);
  }

  public static boolean isHisi3718CV100(String paramString)
  {
    return "Hi3718CV100".equals(paramString);
  }

  public void acquireWakelock()
  {
    if (this.wakelock != null)
      return;
    this.wakelock = ((PowerManager)this.context.getSystemService("power")).newWakeLock(6, super.getClass().getCanonicalName());
    this.wakelock.acquire();
  }


  public NetworkInfo getActiveNetworkInfo()
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)this.context.getSystemService("connectivity");
    if (localConnectivityManager == null)
      return null;
    return localConnectivityManager.getActiveNetworkInfo();
  }

  public String getBaseVersion()
  {
    return "unknown";
  }

  public String getBspVersion()
  {
    return Build.DISPLAY;
  }

  public String getDeviceId()
  {
    String str = getUUID();
    if ((str == null) || ("".equals(str.trim())))
      str = "false";
    return str;
  }

  public long getDirAvailableSize(String paramString)
  {
    if (paramString == null)
      return 0L;
    long l = 0L;
    try
    {
      StatFs localStatFs = new StatFs(paramString);
      l = localStatFs.getAvailableBlocks() * localStatFs.getBlockSize();
      return l;
    }
    catch (Exception localException)
    {
    }
    return l;
  }
  
  /*public String getKernelVersion()
  {
    Matcher localMatcher;
    try
    {
      BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/version"), 256);
      try
      {
        String str1 = localBufferedReader.readLine();
        localBufferedReader.close();
        localMatcher = Pattern.compile("\\w+\\s+\\w+\\s+([^\\s]+)\\s+\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+([^\\s]+)\\s+(?:PREEMPT\\s+)?(.+)").matcher(str1);
        if (localMatcher.matches())
          break label91;
        return "Unavailable";
      }
      finally
      {
        localBufferedReader.close();
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return "Unavailable";
    }
    if (localMatcher.groupCount() < 4)
    {
      label91: Log.d("haha","Regex match on /proc/version only returned " + localMatcher.groupCount() + " groups");
      return "Unavailable";
    }
    int i = localMatcher.group(1).lastIndexOf("-");
    if (i <= 0)
      i = localMatcher.group(1).length();
    String str2 = localMatcher.group(1).substring(0, i);
    return str2;
  }

  public PackageInfo getPackageInfo(String paramString)
  {
    try
    {
      PackageInfo localPackageInfo = this.context.getPackageManager().getPackageInfo(paramString, 0);
      return localPackageInfo;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
    }
    return null;
  }*/

  public String getPhoneType()
  {
    String str = Build.MODEL;
    if (Integer.parseInt(Build.VERSION.SDK) >= 10)
      str = str + Build.VERSION.SDK;
    String[] arrayOfString = getSystemVersion().split("-");
    if ((arrayOfString != null) && (arrayOfString.length > 1))
      str = str + arrayOfString[1];
    return str;
  }

  public String getProductType()
  {
    return Build.MODEL;
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

  public String getSystemVersion()
  {
    return Build.VERSION.RELEASE;
  }

  public String getUUID()
  {
    return getSystemProperty("ro.aliyun.clouduuid",null);
  }

  public boolean hasEnoughSpaceToInstall(Context paramContext, long paramLong)
  {
    if (getDirAvailableSize(paramContext.getFilesDir().getPath()) >= paramLong);
    for (int i = 1; i == 0; i = 0)
      return false;
    return true;
  }

  public boolean isNetworkAvailable()
  {
    NetworkInfo localNetworkInfo = getActiveNetworkInfo();
    return (localNetworkInfo != null) && (localNetworkInfo.isConnected());
  }

  public void releaseWakelock()
  {
    if ((this.wakelock == null) || (!this.wakelock.isHeld()))
      return;
    this.wakelock.release();
    this.wakelock = null;
  }

}