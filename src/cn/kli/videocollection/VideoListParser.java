package cn.kli.videocollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class VideoListParser {
	private final static String VIDEO_ELEMENT = "video";
	private final static String VALUE_TITLE = "title";
	private final static String VALUE_PIC = "pic";
	private final static String VALUE_URL = "url";
	
	public static List<VideoInfo> parse(InputStream is){
		List<VideoInfo> videoList = null;
		SAXParserFactory saxParser = SAXParserFactory.newInstance();
		try {
			SAXParser sp = saxParser.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			XmlHandler handler = new XmlHandler(); 
			reader.setContentHandler(handler);
			reader.parse(new InputSource(is));
			videoList = handler.videoList;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return videoList;
	}
	
	private static class XmlHandler extends DefaultHandler{
		private List<VideoInfo> videoList = new ArrayList<VideoInfo>();
		private VideoInfo info;
		private String tmp;
		
		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes attributes)
				throws SAXException {
			if(VIDEO_ELEMENT.equals(localName)){
				info = new VideoInfo();
				Log.i("klilog", "startElement "+localName);
			}
			tmp = localName;
		}
		
		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			String value = new String(ch, start, length);
			if (info != null) {
				if (VALUE_TITLE.equals(tmp)) {
					Log.i("klilog", "characters "+tmp);
					info.title = value;
				} else if (VALUE_PIC.equals(tmp)) {
					Log.i("klilog", "characters "+tmp);
					info.pic = value;
				} else if (VALUE_URL.equals(tmp)) {
					Log.i("klilog", "characters "+tmp);
					info.url = value;
				} 
			}

		}
		
		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(VIDEO_ELEMENT.equals(localName)){
				Log.i("klilog", "endElement "+localName);
				videoList.add(info);
			}
			tmp = null;
		}

	}
}
