package com.twituji.poemBoy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class HackTTS {
	private PoemBoyActivity pa;
	public HackTTS(PoemBoyActivity pa) {
		this.pa = pa;
	}

	// 定数
	private static final int BUFFER_SIZE = 10240;
	private static final String ENCODING = "UTF-8";
	private static final String URL_STRING = "http://translate.google.com/translate_tts?tl=zh&q=";
	private static final String HTTP_ACCEPT = "application/x-www-form-urlencoded";
	private static final String HTTP_ACCEPT_ENCODING = "gzip,deflate,sdch";
	private static final String HTTP_ACCEPT_CHARSET = "ISO-8859-1,utf-8;q=0.7,*;q=0.7";
	private static final String HTTP_ACCEPT_LANGUAGE = "ja-jp,en;q=0.5";
	private static final String HTTP_USER_AGENT = "Mozilla/5.0";

	// ボタンクリックイベント
	public void dpSpeech(MediaPlayer mp, String text) {
		try {
			File tmp = getHttpRequestFile(text);

			mp.setDataSource(new FileInputStream(tmp).getFD());
			mp.prepare();

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!mp.isPlaying()) {
			mp.start();
		} else {
			mp.pause();
		}
	}

	// HTTP偽装
	private File getHttpRequestFile(String text) throws Exception {
		try {
			StringBuilder url = new StringBuilder();
			url.append(URL_STRING);
			url.append(URLEncoder.encode(text, ENCODING));

			HttpURLConnection uc = (HttpURLConnection) new URL(url.toString())
					.openConnection();
			uc.setRequestProperty("Accept", HTTP_ACCEPT);
			uc.setRequestProperty("Accept-Encoding", HTTP_ACCEPT_ENCODING);
			uc.setRequestProperty("Accept-Charset", HTTP_ACCEPT_CHARSET);
			uc.setRequestProperty("Accept-Language", HTTP_ACCEPT_LANGUAGE);
			uc.setRequestProperty("User-Agent", HTTP_USER_AGENT);
			// uc.connect();

			try {
				InputStream is = uc.getInputStream();
				return this.getTtsFile(is);
			} finally {
				uc.getInputStream().close();
				if (uc.getErrorStream() != null)
					uc.getErrorStream().close();
			}
		} catch (Exception e) {
			throw e;
		}
	}

	// ファイル保存
	private File getTtsFile(InputStream is) throws Exception {
		byte[] buffer = new byte[BUFFER_SIZE];
		int numread = 0;
		File tmp = new File(pa.getCacheDir(), "tmp");
		FileOutputStream us = new FileOutputStream(tmp);
		try {
			do {
				numread = is.read(buffer);
				if (numread == -1)
					break;
				us.write(buffer, 0, numread);
			} while (true);
		} catch (Exception e) {
		} finally {
			us.flush();
			us.close();
		}
		return tmp;
	}
}
