package com.twituji.poemBoy;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PoemBoyActivity extends Activity implements View.OnClickListener {
	private Button startBtn;
	private static final int REQUEST_CODE = 1;
	public static String RESULT = "";
	public String input;

	/** Called when the activity is first created. */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (!checkNetworkState()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert).setTitle(
							R.string.error) // ダイアログのタイトル
					.setMessage(R.string.error_network_offline) // ダイアログに表示するメッセージ
					.setNeutralButton(R.string.exit,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									dialog.cancel();
									PoemBoyActivity.this.finish();
								}
							});
			builder.show();
		}
		startBtn = (Button) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(this);
	}

	/**
	 * 
	 * @return
	 */
	private boolean checkNetworkState() {
		return NetworkManager.isOnline(this.getApplicationContext());
	}

	/**
	 * 
	 */
	@Override
	public void onClick(View v) {
		if (v == startBtn) {
			try {
				// 音声認識の準備
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				// Intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.CHINA
						.toString());
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, this
						.mkStringFromRid(R.string.voiceTip));
				// インテント発行
				startActivityForResult(intent, REQUEST_CODE);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(PoemBoyActivity.this,
						this.mkStringFromRid(R.string.noPlugin),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	/**
	 * 
	 * @param rid
	 * @return
	 */
	private String mkStringFromRid(int rid) {
		return this.getApplicationContext().getString(rid);
	}

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
			// 認識結果を取得
			ArrayList<String> candidates = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			Log.v("Speech", "Candidate Num = " + candidates.size());
			if (candidates.size() > 0) {
				input = candidates.get(0);// 認識結果(1位候補)
				Toast.makeText(PoemBoyActivity.this, "您说的：" + input, Toast.LENGTH_LONG).show();
				AccessApiTask task = new AccessApiTask(PoemBoyActivity.this);  
				task.execute("http://songci.sinaapp.com/ask.php/tang/part/", input);
			}
		}
	}
	
	
}