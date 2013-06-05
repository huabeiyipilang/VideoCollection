package cn.kli.videocollection;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;
import com.baidu.cyberplayer.sdk.BEngineManager;
import com.baidu.mobads.MultiFuncService;

public class VideoListActivity extends Activity implements OnItemClickListener {
	private final String TAG = "MainActivity";
	private ListView mVideoList;
	private VideoAdapter mVideoAdapter;
	private ProgressDialog mWaitingDialog;
	
	private final int PLAY_VIDEO = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//init BCyberPlayerFactory first
		BCyberPlayerFactory.init(this);
		
		setContentView(R.layout.activity_video_list);
		
		initUI();
	}
	
	void initUI(){
		mVideoList = (ListView)findViewById(R.id.lv_video_list);
		mVideoAdapter = new VideoAdapter(this);
		mVideoList.setAdapter(mVideoAdapter);
		mVideoList.setOnItemClickListener(this);
	}
	
	private Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PLAY_VIDEO:
				playVideo((String)msg.obj);
				break;
			default:
				break;
			}
		}
	};
	
	private void playVideo(String url){
		if(mWaitingDialog != null && mWaitingDialog.isShowing()){
			mWaitingDialog.dismiss();
		}
		
		if(!isEngineInstalled()){
			Intent intent = new Intent(this, EngineInstallActivity.class);
			intent.putExtra("url", url);
			startActivity(intent);
		}else{
			if(url == null || url.equals("") || "null".equals(url)){
				Toast.makeText(this, R.string.error_video_path, Toast.LENGTH_SHORT).show();
			}else{
				Log.i("klilog", url);
				BaiduPlayer.Play(this, url);
			}
		}
	}
	
	
	private boolean isEngineInstalled(){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		return mgr.EngineInstalled();
	}

	
	private class VideoAdapter extends BaseAdapter{
		private Context mContext;
		private List<VideoInfo> videoList = new ArrayList<VideoInfo>();
		private LayoutInflater inflater;
		
		VideoAdapter(Context context){
			mContext = context;
			inflater = LayoutInflater.from(mContext);
			try {
				videoList = VideoListParser.parse(mContext.getAssets().open("videolist.xml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		public View getView(int pos, View convertView, ViewGroup arg2) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = inflater.inflate(R.layout.video_list_item, null);
				holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.tv_title);
				holder.pic = (ImageView)convertView.findViewById(R.id.iv_pic);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.title.setText(getItem(pos).title);
			
			InputStream is;
			try {
				is = mContext.getAssets().open(getItem(pos).pic);
				holder.pic.setImageBitmap(BitmapFactory.decodeStream(is));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return convertView;
		}
		
		private class ViewHolder{
			TextView title;
			ImageView pic;
		}
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, final int pos, long arg3) {
		mWaitingDialog = new ProgressDialog(this);
		mWaitingDialog.setMessage(getString(R.string.parser_video_path));
		mWaitingDialog.show();
		new Thread(){
			@Override
			public void run() {
				super.run();
				String src = mVideoAdapter.getItem(pos).getRealUrl(VideoListActivity.this);
				Message msg = mUIHandler.obtainMessage(PLAY_VIDEO);
				msg.obj = src;
				msg.sendToTarget();
			}
			
		}.start();		
	}
	
}
