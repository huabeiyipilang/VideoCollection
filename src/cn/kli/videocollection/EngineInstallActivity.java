package cn.kli.videocollection;

import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;
import com.baidu.cyberplayer.sdk.BEngineManager;
import com.baidu.cyberplayer.sdk.BEngineManager.OnEngineListener;
import com.baidu.mobads.MultiFuncService;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EngineInstallActivity extends Activity implements OnClickListener {

	private TextView mInfoTV;
	private final int UPDATE_INFO = 1;
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
	
	private String mUrl;
	
	private Handler mUIHandler = new Handler() {
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
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.engine_install_activity);
	    mUrl = getIntent().getStringExtra("url");
	    findViewById(R.id.bt_install).setOnClickListener(this);
	    mInfoTV = (TextView)findViewById(R.id.tv_install_info);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.bt_install:
			checkEngineInstalled();
			break;
		}
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
	
	
	private boolean isEngineInstalled(){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		return mgr.EngineInstalled();
	}

	
	private void installEngine(){
		BEngineManager mgr = BCyberPlayerFactory.createEngineManager();
		mgr.installAsync(mEngineListener);
	}
	
	private void setInfo(String info){
		Message msg = new Message();
		msg.what = UPDATE_INFO;
		msg.obj = info;
		mUIHandler.sendMessage(msg);
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
				BaiduPlayer.Play(EngineInstallActivity.this, mUrl);
				finish();
			}
		}		
	};
}
