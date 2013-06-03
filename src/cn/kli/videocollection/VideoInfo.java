package cn.kli.videocollection;

import android.content.Context;

public class VideoInfo {

	String title;
	String pic;
	String url;
	

	VideoInfo(){
		
	}
	
	VideoInfo(String title, String url){
		this.title = title;
		this.url = url;
	}
	
	public String getRealUrl(Context context){
		BaiduNetDiskParser parser = new BaiduNetDiskParser(context);
		return parser.parseUrl(url);
	}
}
