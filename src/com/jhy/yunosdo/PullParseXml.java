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

		// ����XmlPullParserFactory
		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory
					.newInstance();
			// ��ȡXmlPullParser��ʵ��
			XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
			// ���������� xml�ļ�
			// xmlPullParser.setInput(Thread.currentThread().getContextClassLoader().getResourceAsStream("Student.xml"),
			// "UTF-8");
			xmlPullParser.setInput(in, "UTF-8");

			// ��ʼ
			int eventType = xmlPullParser.getEventType();

			try {
				while (eventType != XmlPullParser.END_DOCUMENT) {
					String nodeName = xmlPullParser.getName();
		        	Log.d(MainActivity.TAG, "eventType:"+eventType);
		        	Log.d(MainActivity.TAG, "nodeName:"+nodeName);
		        	
					switch (eventType) {
					// �ĵ���ʼ
					case XmlPullParser.START_DOCUMENT:
						list = new ArrayList<ActivityFotaEntity>();
						break;
					// ��ʼ�ڵ�
					case XmlPullParser.START_TAG:
						// �ж������ʵ�ڵ�Ϊstudent
						if ("UpdateItem".equals(nodeName)) {
							// ʵ����student����
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.UPDATAE);
							Log.d(MainActivity.TAG,"UpdateItem");
						}else if ("InstallItem".equals(nodeName)) {
							// ʵ����student����
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.INSTALL);
							Log.d(MainActivity.TAG,"InstallItem");
						} else if("DeleteItem".equals(nodeName)){
							// ʵ����student����
							fotaInfo = new ActivityFotaEntity(ActivityFotaEntity.DEL);
							Log.d(MainActivity.TAG,"DelItem");
						} else if (fotaInfo != null) {
							
							setFieldValue(fotaInfo, xmlPullParser.getName(),
									xmlPullParser.nextText());
						}
						break;
					// �����ڵ�
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
	 * �����ֶ�ֵ
	 * 
	 * @param propertyName
	 *            �ֶ���
	 * @param obj
	 *            ʵ������
	 * @param value
	 *            �µ��ֶ�ֵ
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
