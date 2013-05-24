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
	
	private void initUI(){
		mInstallBtn = (Button)findViewById(R.id.bt_download_engine);
		mInfoTV = (TextView)findViewById(R.id.tv_info);
		mVideoList = (ListView)findViewById(R.id.lv_video_list);
		mInstallBtn.setOnClickListener(this);
		mVideoAdapter = new VideoAdapter(this);
		mVideoList.setAdapter(mVideoAdapter);
		mVideoList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				playVideo(mVideoAdapter.getItem(pos).url);
			}
		});
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		boolean engPrepared = isEngineInstalled();
		findViewById(R.id.ll_engine).setVisibility(engPrepared ? View.GONE : View.VISIBLE);
		findViewById(R.id.ll_ads).setVisibility(engPrepared ? View.VISIBLE : View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		switch(id){
		case R.id.bt_download_engine:
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
				mgr.initCyberPlayerEngine(Config.AK, Config.SK);
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
		@Override
		public boolean onPrepare() {
			return true;
		}

		@Override
		public int onDownload(int total, int current) {
			String info = current/1000 + "kb / " + total/1000+"kb";
			setInfo(info);
			return DOWNLOAD_CONTINUE;
		}
		
		@Override
		public int onPreInstall() {
			return DOWNLOAD_CONTINUE;
		}

		@Override
		public void onInstalled(int result) {
		}		
	};

	
	private void setInfo(String info){
		Message msg = new Message();
		msg.what = UPDATE_INFO;
		msg.obj = info;
		mUIHandler.sendMessage(msg);
	}
	
	private class VideoInfo{
		String title;
		String url;
		
		VideoInfo(String title, String url){
			this.title = title;
			this.url = url;
		}
	}
	
	private class VideoAdapter extends BaseAdapter{
		private Context mContext;
		private List<VideoInfo> videoList = new ArrayList<VideoInfo>();
		private LayoutInflater inflater;
		
		VideoAdapter(Context context){
			mContext = context;
			inflater = LayoutInflater.from(mContext);
			videoList.add(new VideoInfo("TBC", "http://bj.baidupcs.com/file/89e33b2dd19d2ebb16f05a86701ca43b?fid=2852451119-250528-3278443244&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-VF2Kp%2FKEgzHpv9LEXQxZzYP2a58%3D&rt=sh&expires=8h&r=230927202&sh=1&xcode=35ba00cfa457b42caf853e09a17b1fa7&redirect=1"));
			videoList.add(new VideoInfo("WLK", "http://bj.baidupcs.com/file/9d6738fe82fe24ec5d9c8888437908b8?fid=2852451119-250528-425624354&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-kzXHv7TBM1l1O1HmeeGxYIOEnhk%3D&rt=sh&expires=8h&r=902745917&sh=1&xcode=0b15f9722a3cf19b832585c21582586d&redirect=1"));
			videoList.add(new VideoInfo("CTM", "http://bj.baidupcs.com/file/022b6f678f31482df0388ad0633f9c7f?fid=2852451119-250528-3626398307&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-A1uOnVDMlAQMB%2Bzd540DdWGBw1U%3D&rt=sh&expires=8h&r=458862257&sh=1&xcode=b75548bd7954fddde16206346c2e0bd1&redirect=1"));
			videoList.add(new VideoInfo("MOP", "http://bj.baidupcs.com/file/b6eb3359a7c43bfb00c18c82a3fa7353?fid=2852451119-250528-2265029866&time=1369241723&sign=FDTAR-DCb740ccc5511e5e8fedcff06b081203-YsGHjw%2B0tp8FgCDuHGvUCU6vScM%3D&rt=sh&expires=8h&r=193078210&sh=1&xcode=b75548bd7954fddd5589ab5cf3551107&redirect=1"));
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
