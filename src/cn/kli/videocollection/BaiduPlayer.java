package cn.kli.videocollection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;
import com.baidu.cyberplayer.sdk.BEngineManager;
import com.baidu.mobads.MultiFuncService;

public class BaiduPlayer {
	final static String AK = "TNpoLK8ynIMRtUfTfgMYpuGe";
	final static String SK = "ZywHFheGKhdDcAQqIvMGVd4wMbKhWuIK";
	
	public static void Play(Activity activity, String url){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		mgr.initCyberPlayerEngine(BaiduPlayer.AK, BaiduPlayer.SK);
		Intent intent = new Intent(activity, VideoViewPlayingActivity.class);
		intent.setData(Uri.parse(url));
		activity.startActivity(intent);
		MultiFuncService.getInstance(activity).videoPreLoad(activity, null);
	}
}
