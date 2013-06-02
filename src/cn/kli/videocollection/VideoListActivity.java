package cn.kli.videocollection;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;
import com.baidu.cyberplayer.sdk.BEngineManager;
import com.baidu.cyberplayer.sdk.BEngineManager.OnEngineListener;

public class VideoListActivity extends Activity implements OnClickListener {
	private final String TAG = "MainActivity";
	private Button mInstallBtn;
	private TextView mInfoTV;
	private ListView mVideoList;
	private VideoAdapter mVideoAdapter;
	
	private final int UPDATE_INFO = 0;
	private final int PLAY_VIDEO = 1;
	
	private String AK = "TNpoLK8ynIMRtUfTfgMYpuGe";
	private String SK = "ZywHFheGKhdDcAQqIvMGVd4wMbKhWuIK";
	
	String[] mRetInfo = new String[] {
			"RET_NEW_PACKAGE_INSTALLED",
			"RET_NO_NEW_PACKAGE",
			"RET_STOPPED",
			"RET_CANCELED",
			"RET_FAILED_STORAGE_IO",
			"RET_FAILED_NETWORK",
			"RET_FAILED_ALREADY_RUNNING",
			"RET_FAILED_OTHERS",
			"RET_FAILED_ALREADY_INSTALLED",
			"RET_FAILED_INVALID_APK"
	};
	
	
	
	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UPDATE_INFO:
				mInfoTV.setText((String)msg.obj);
				break;
			case PLAY_VIDEO:
				playVideo((String)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//init BCyberPlayerFactory first
		BCyberPlayerFactory.init(this);
		
		setContentView(R.layout.activity_video_list);
		
		initUI();
	}
	
	void initUI(){
		mInstallBtn = (Button)findViewById(R.id.installBtn);
		mInfoTV = (TextView)findViewById(R.id.infoTV);
		mVideoList = (ListView)findViewById(R.id.lv_video_list);
		mInstallBtn.setOnClickListener(this);
		mVideoAdapter = new VideoAdapter(this);
		mVideoList.setAdapter(mVideoAdapter);
		mVideoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int pos,
					long arg3) {
				new Thread(){

					@Override
					public void run() {
						super.run();
						BaiduNetDiskParser parser = new BaiduNetDiskParser(VideoListActivity.this);
						String src = parser.parseUrl(mVideoAdapter.getItem(pos).url);
						Log.i("klilog", src);
						Message msg = mUIHandler.obtainMessage(PLAY_VIDEO);
						msg.obj = src;
						msg.sendToTarget();
					}
					
				}.start();
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch(id){
		case R.id.installBtn:
			checkEngineInstalled();
			break;
		default:
			break;
		}
	}
	
	private void playVideo(String url){
		if(!isEngineInstalled()){
			setInfo("CyberPlayerEngine not installed,\n please install it first");
		}else{
			if(url == null || url.equals("")){
				setInfo("Please input a valid video path");
			}else{
				BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
				mgr.initCyberPlayerEngine(AK, SK);
				Intent intent = new Intent(this, VideoViewPlayingActivity.class);
				intent.setData(Uri.parse(url));
				startActivity(intent);
			}
		}
	}
	
	
	private boolean isEngineInstalled(){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		return mgr.EngineInstalled();
	}
	
	private void installEngine(){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		mgr.installAsync(mEngineListener);
	}
	
	private void checkEngineInstalled(){
		if(isEngineInstalled()){
			setInfo("CyberPlayerEngine Installed.\n");
			//BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
			//mgr.initCyberPlayerEngine(AK, SK);
		}else{
			installEngine();
		}
	}

	private OnEngineListener mEngineListener = new OnEngineListener(){
		String info = "";
		
		String dlhead = "install engine: onDownload   ";
		String dlbody = "";
		@Override
		public boolean onPrepare() {
			// TODO Auto-generated method stub
			info = "install engine: onPrepare.\n";
			setInfo(info);
			return true;
		}

		@Override
		public int onDownload(int total, int current) {
			// TODO Auto-generated method stub
			if(dlhead != null){
				info += dlhead;
				dlhead = null;
			}
			dlbody = current + "/" + total;
			setInfo(info + dlbody + "\n");
			return DOWNLOAD_CONTINUE;
		}
		
		@Override
		public int onPreInstall() {
			// TODO Auto-generated method stub
			info += dlbody;
			info += "\n";
			info += "install engine: onPreInstall.\n";
			setInfo(info);
			
			return DOWNLOAD_CONTINUE;
		}

		@Override
		public void onInstalled(int result) {
			// TODO Auto-generated method stub
			info += "install engine: onInstalled, ret = " + mRetInfo[result] + "\n";
			setInfo(info);
			if(result == OnEngineListener.RET_NEW_PACKAGE_INSTALLED){
				//BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
				//mgr.initCyberPlayerEngine(AK, SK);
			}
		}		
	};

	
	private void setInfo(String info){
		Message msg = new Message();
		msg.what = UPDATE_INFO;
		msg.obj = info;
		mUIHandler.sendMessage(msg);
	}
	
	
	private class VideoAdapter extends BaseAdapter{
		private Context mContext;
		private List<VideoInfo> videoList = new ArrayList<VideoInfo>();
		private LayoutInflater inflater;
		
		VideoAdapter(Context context){
			mContext = context;
			inflater = LayoutInflater.from(mContext);
			videoList.add(new VideoInfo("TBC", "http://wsdl6.yunpan.cn/share.php?method=Share.download&fhash=a876543784aeecad1950ccf1a3231473832a5545&xqid=74879046&fname=%E5%A4%A7%E7%81%BE%E5%8F%98-720p-en.mp4&fsize=38626261&nid=13698350152977153&cqid=49a8db637f7c15d0a4e26d70fd020af8&st=1620a730805afacce2232fff5dfbc9a0&e=1370009964"));
			videoList.add(new VideoInfo("WLK", "http://bj.baidupcs.com/file/9d6738fe82fe24ec5d9c8888437908b8?fid=2852451119-250528-425624354&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-kzXHv7TBM1l1O1HmeeGxYIOEnhk%3D&rt=sh&expires=8h&r=902745917&sh=1&xcode=0b15f9722a3cf19b832585c21582586d&redirect=1"));
			videoList.add(new VideoInfo("CTM", "http://bj.baidupcs.com/file/022b6f678f31482df0388ad0633f9c7f?fid=2852451119-250528-3626398307&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-A1uOnVDMlAQMB%2Bzd540DdWGBw1U%3D&rt=sh&expires=8h&r=458862257&sh=1&xcode=b75548bd7954fddde16206346c2e0bd1&redirect=1"));
			videoList.add(new VideoInfo("MOP", "http://pan.baidu.com/share/link?shareid=498392&uk=2852451119"));
		}

		@Override
		public int getCount() {
			return videoList.size();
		}

		@Override
		public VideoInfo getItem(int i) {
			return videoList.get(i);
		}

		@Override
		public long getItemId(int i) {
			return i;
		}

		@Override
		public View getView(int pos, View arg1, ViewGroup arg2) {
			View item = inflater.inflate(R.layout.video_list_item, null);
			TextView tvTitle = (TextView)item.findViewById(R.id.tv_title);
			tvTitle.setText(getItem(pos).title);
			return item;
		}
		
	}
	
}
