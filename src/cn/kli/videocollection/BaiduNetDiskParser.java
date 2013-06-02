package cn.kli.videocollection;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.os.Message;

public class BaiduNetDiskParser {
	
	private Context mContext;
	private static final String ENCODE="UTF-8";
	
	
	public BaiduNetDiskParser(Context context){
		mContext = context;
	}
	
	public String parseUrl(String origin){
		String result = null;
		HttpUriRequest req = new HttpGet(origin);
		try {
			HttpClient mHttpClient = new DefaultHttpClient();
			HttpResponse response = mHttpClient.execute(req);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity(), ENCODE);
			}
			result = decodeUrl(result);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	public void parseUrl(final String origin, final Message callback){
		new Thread(){

			@Override
			public void run() {
				super.run();
				String result = null;
				HttpUriRequest req = new HttpGet(origin);
				try {
					HttpClient mHttpClient = new DefaultHttpClient();
					HttpResponse response = mHttpClient.execute(req);
					if (response.getStatusLine().getStatusCode() == 200) {
						result = EntityUtils.toString(response.getEntity(), ENCODE);
					}
					result = decodeUrl(result);
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callback.obj = result;
				callback.sendToTarget();
			}
			
		}.start();
	}
	
	private String decodeUrl(String origin){
		origin = origin.replace("&amp;", "&");
		String res = null;
		int pos = origin.indexOf("new-dbtn");
		int start = origin.indexOf("\"", pos+"new-dbtn".length()+2) + 1;
		int end = origin.indexOf("\"", start+1);
		res = origin.substring(start, end);
		return res;
	}
}
