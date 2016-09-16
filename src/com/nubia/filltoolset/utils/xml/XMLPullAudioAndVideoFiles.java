package com.nubia.filltoolset.utils.xml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.util.Log;

public class XMLPullAudioAndVideoFiles {

	private static XMLFileBean bean = null;
	private static XMLSort xmlSort = null;
	private static List<XMLFileBean> XMLSceneBeans = null;
	private static Map<String,List<XMLFileBean>> XMLMap = new HashMap<String,List<XMLFileBean>>();
	public static Map<String,List<XMLFileBean>> parseSceneXML(InputStream is) {
		if (is == null) {
			return null;
		}
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();

			parser.setInput(is, null);
			int eventType = parser.getEventType();//0
			Log.v("ao", "进入XML解析||"); 
			while (eventType != XmlPullParser.END_DOCUMENT) {//1
				// 解析XML文件
				Log.v("ao", "type||"+eventType+"||"+XmlPullParser.END_DOCUMENT+"||"+XmlPullParser.START_TAG+"||"+XmlPullParser.END_TAG);
				if (eventType == XmlPullParser.START_TAG) {//2
					// 新建一个XMLSceneBean
					if (parser.getName().equalsIgnoreCase("key")) {
						Log.v("ao", "key"+parser.getName());
						XMLSceneBeans = new ArrayList<XMLFileBean>();
						xmlSort = new XMLSort();					 
						String sceneId = parser.getAttributeValue(0);
						Log.v("ao", sceneId);			
						xmlSort.setSortId(sceneId);

					}
					
					else if(parser.getName().equalsIgnoreCase("p")){
						bean = new XMLFileBean();
						try {
							parser.next();
						} catch (IOException e) {
							e.printStackTrace();
						}
						bean.setFileName(parser.getText());
					}
				}
				else if(eventType == XmlPullParser.END_TAG){//3
					Log.v("ao", "||"+XmlPullParser.END_TAG);
						String endName = parser.getName();
						Log.v("ao","endName||"+endName );
						if(endName.equalsIgnoreCase("p")){
							XMLSceneBeans.add(bean);
;						}
						else if(endName.equalsIgnoreCase("key")){
							if(xmlSort != null)
								xmlSort.setElements(XMLSceneBeans);
							XMLMap.put(xmlSort.getSortId(), xmlSort.getXMLFileBeans());
						}
					}			
					Log.v("ao","eventType"+eventType );
					try {
						eventType = parser.next();
						Log.v("ao","eventTypehou"+eventType );
						
					} catch (IOException e) {
						Log.v("ao","eventprintyichang"+eventType );
						e.printStackTrace();
					}
					
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		return XMLMap;
	}


}
