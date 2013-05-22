package cn.kli.videocollection;

import com.baidu.cyberplayer.sdk.BCyberPlayerFactory;
import com.baidu.cyberplayer.sdk.BEngineManager;
import com.baidu.cyberplayer.sdk.BVideoView;
import com.baidu.cyberplayer.sdk.BVideoView.OnCompletionListener;
import com.baidu.cyberplayer.sdk.BVideoView.OnErrorListener;
import com.baidu.cyberplayer.sdk.BVideoView.OnInfoListener;
import com.baidu.cyberplayer.sdk.BVideoView.OnPreparedListener;
import com.baidu.cyberplayer.sdk.BMediaController;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class VideoViewPlayingActivity extends Activity implements OnPreparedListener, OnCompletionListener, OnErrorListener, OnInfoListener {
	
	private final String TAG = "VideoViewPlayingActivity";
		
	private String mVideoSource = null;
	
	private BVideoView mVV = null;
	private BMediaController mVVCtl = null;
	private RelativeLayout mViewHolder = null;
	private LinearLayout mControllerHolder = null;
	
	private boolean mIsHwDecode = false;
	
	private final int UI_EVENT_PLAY = 0;
		
	private WakeLock mWakeLock = null;
	private static final String POWER_LOCK = "VideoViewPlayingActivity";
	
	private  enum PLAYER_STATUS {
		PLAYER_IDLE, PLAYER_PREPARING, PLAYER_PREPARED,
	}
	
	private PLAYER_STATUS mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	
	private int mLastPos = 0;
	
	private View.OnClickListener mPreListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "pre btn clicked");
		}
	};
	
	private View.OnClickListener mNextListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Log.v(TAG, "next btn clicked");
		}
	};
		
	Handler mUIHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UI_EVENT_PLAY:
				mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARING;
				if(mLastPos != 0){
					mVV.seekTo(mLastPos);
					mLastPos = 0;
				}
				mVV.setVideoPath(mVideoSource);
				mVV.start();
				break;
			default:
				break;
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
						
		setContentView(R.layout.controllerplaying);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, POWER_LOCK);
		
		mIsHwDecode = getIntent().getBooleanExtra("isHW", false);
		Uri uriPath = getIntent().getData();
		if (null != uriPath) {
			String scheme = uriPath.getScheme();
			if (null != scheme && (scheme.equals("http") || scheme.equals("https") || scheme.equals("rtsp") || scheme.equals("bdhd"))) {
				mVideoSource = uriPath.toString();
			} else {
				mVideoSource = uriPath.getPath();
			}
		}
		
		initUI();
	}
	
	private void initUI() {		
		mViewHolder = (RelativeLayout)findViewById(R.id.view_holder);
		mControllerHolder = (LinearLayout )findViewById(R.id.controller_holder);
		mVV = new BVideoView(this);
		mVVCtl = new BMediaController(this);
		mViewHolder.addView(mVV);
		mControllerHolder.addView(mVVCtl);
		
		//register listener if you need
		mVV.setOnPreparedListener(this);
		mVV.setOnCompletionListener(this);
		mVV.setOnErrorListener(this);
		mVV.setOnInfoListener(this);
		mVVCtl.setPreNextListener(mPreListener, mNextListener);
		
		mVV.setMediaController(mVVCtl);
		mVV.setDecodeMode(BVideoView.DECODE_SW);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		Log.v(TAG, "onPause");
		if(mPlayerStatus != PLAYER_STATUS.PLAYER_IDLE){
			mLastPos = mVV.getCurrentPosition();
			mVV.stopPlayback();
		}
	}

	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.v(TAG, "onResume");
		if (null != mWakeLock && (!mWakeLock.isHeld())) {
			mWakeLock.acquire();
		}
		mUIHandler.sendEmptyMessage(UI_EVENT_PLAY);		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		Log.v(TAG, "onStop");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		Log.v(TAG, "onDestroy");
	}

	@Override
	public boolean onInfo(int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onError(int what, int extra) {
		// TODO Auto-generated method stub
		Log.v(TAG, "onError");
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
		return true;
	}

	@Override
	public void onCompletion() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onCompletion");
		mPlayerStatus = PLAYER_STATUS.PLAYER_IDLE;
	}

	@Override
	public void onPrepared() {
		// TODO Auto-generated method stub
		Log.v(TAG, "onPrepared");
		mPlayerStatus = PLAYER_STATUS.PLAYER_PREPARED;
	}	
}
