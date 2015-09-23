package com.jhy.yunosdo;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.jhy.yunosdo.entity.ActivityFotaEntity;

public class PullParseXml {

	public List<ActivityFotaEntity> PullParseXML(InputStream in) {

		List<ActivityFotaEntity> list = null;
		ActivityFotaEntity fotaInfo = null;

		// 构建XmlPullParserFactory
		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// 获取XmlPullParser的实例
			XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
			// 设置输入流 xml文件
			// xmlPullParser.setInput(Thread.currentThread().getContextClassLoader().getResourceAsStream("Student.xml"),
			// "UTF-8");
			xmlPullParser.setInput(in, "UTF-8");

			// 开始
			int eventType = xmlPullParser.getEventType();

			try {
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
		        	Log.d(MainActivity.TAG, "eventType:"+eventType);
		        	Log.d(MainActivity.TAG, "nodeName:"+nodeName);
		        	
					switch (eventType) {
					// 文档开始
					case XmlPullParser.START_DOCUMENT:
						list = new ArrayList<ActivityFotaEntity>();
						break;
					// 开始节点
					case XmlPullParser.START_TAG:
						// 判断如果其实节点为student
						if ("UpdateItem".equals(nodeName)) {
							// 实例化student对象
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.UPDATAE);
							Log.d(MainActivity.TAG,"UpdateItem");
						}else if ("InstallItem".equals(nodeName)) {
							// 实例化student对象
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.INSTALL);
							Log.d(MainActivity.TAG,"InstallItem");
						} else if("DeleteItem".equals(nodeName)){
							// 实例化student对象
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.DEL);
							Log.d(MainActivity.TAG,"DelItem");
						} else if (fotaInfo != null) {
							
							setFieldValue(fotaInfo, xmlPullParser.getName(),
									xmlPullParser.nextText());
						}
						break;
					// 结束节点
					case XmlPullParser.END_TAG:
						if ("InstallItem".equals(nodeName) || "UpdateItem".equals(nodeName) || "DeleteItem".equals(nodeName)) {
							list.add(fotaInfo);
							fotaInfo = null;
						}
						break;
					default:
						break;
					}
					eventType = xmlPullParser.next();
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 设置字段值
	 * 
	 * @param propertyName
	 *            字段名
	 * @param obj
	 *            实例对象
	 * @param value
	 *            新的字段值
	 * @return
	 */
	public static void setFieldValue(Object obj, String propertyName,
			Object value) {
		Log.d(MainActivity.TAG,"setFieldValue xmlPullParser.getName():"+propertyName+",xmlPullParser.nextText()"
				+ value);
		try {
			Field field = obj.getClass().getDeclaredField(propertyName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception ex) {
			//throw new RuntimeException();
			StringWriter wr =new StringWriter();
			PrintWriter pw = new PrintWriter(wr);
			ex.printStackTrace(pw);
			
			Log.d(MainActivity.TAG,"setFieldValue err :"+wr.toString());
		}
	}
}
