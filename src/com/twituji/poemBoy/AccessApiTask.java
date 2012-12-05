package com.twituji.poemBoy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AccessApiTask extends AsyncTask<String, Integer, String> implements
		OnCompletionListener, OnBufferingUpdateListener {
	private PoemBoyActivity pa;
	private ProgressDialog mProgressDialog;
	private MediaPlayer mediaPlayer;

	public AccessApiTask(PoemBoyActivity poemBoyActivity) {
		pa = poemBoyActivity;
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
	}

	// 可变长的输入参数，与AsyncTask.exucute()对应
	@Override
	protected String doInBackground(String... params) {
		try {
			HttpClient client = new DefaultHttpClient();
			// params[0] 代表连接的url
			HttpGet get = new HttpGet(params[0] + params[1]);
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			long length = entity.getContentLength();
			InputStream is = entity.getContent();
			String s = null;
			if (is != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int ch = -1;
				int count = 0;
				while ((ch = is.read(buf)) != -1) {
					baos.write(buf, 0, ch);
					count += ch;
					if (length > 0) {
						// 如果知道响应的长度，调用publishProgress（）更新进度
						publishProgress((int) ((count / (float) length) * 100));
					}
				}
				s = new String(baos.toByteArray());
				if (1 == s.trim().length())
					s = "抱歉我回答不上来这一句" + params[1];
				baos.close();
			}
			HackTTS tts = new HackTTS(pa);
			tts.dpSpeech(mediaPlayer, s);
			// 返回结果
			return s;
		} catch (Exception e) {
			Log.e("exception", e.getLocalizedMessage());
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// 返回HTML页面的内容
		mProgressDialog.dismiss();
		PoemBoyActivity.RESULT = result;
		Toast.makeText(this.pa, result, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(pa);
		mProgressDialog.setMessage(pa.getApplicationContext().getString(
				R.string.doing));
		mProgressDialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		// 更新进度
		mProgressDialog.setProgress(values[0]);
	}

	@Override
	public void onBufferingUpdate(MediaPlayer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

}
