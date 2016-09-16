package com.nubia.filltoolset.audioandvideotest;

import com.nubia.filltoolset.R;

import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class AudioAndVideoTestActivity extends FragmentActivity implements OnClickListener{
	
	public static LinearLayout container;// 装载sub Activity的容器
	AudioFragment audioFragment;
	VideoFragment videoFragment;
	int AUDIO = 0;
	int VIDEO = 1;
	Fragment lastFragment;
	Button buttons[] = new Button[2];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		audioFragment = new AudioFragment();
		videoFragment = new VideoFragment();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_audioandvideotest);
		container = (LinearLayout) findViewById(R.id.Container);
		buttons[AUDIO] = (Button) findViewById(R.id.audio);
		buttons[AUDIO].setOnClickListener(this);
		buttons[VIDEO] = (Button) findViewById(R.id.video);
		buttons[VIDEO].setOnClickListener(this);
		SwitchFragment(R.id.audio);
	
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	

	/* 根据ID打开指定的Activity */
	public void SwitchFragment(int id) {

		Fragment currentFragment = null;
//		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (id == R.id.audio) {
			refresh(AUDIO);
			currentFragment = audioFragment;
		} else if (id == R.id.video) {
			refresh(VIDEO);
			currentFragment = videoFragment;
		} 

		if (currentFragment != null) {
			if (!currentFragment.isAdded()) {
				if (lastFragment != null) {
					transaction.hide(lastFragment);
				}
				transaction.add(R.id.Container, currentFragment);
				transaction.commit();
			} else {

				transaction.hide(lastFragment).show(currentFragment).commit();
			}
			lastFragment = currentFragment;
		}

	}
	
	private void refresh(int index) {
		Drawable news_sel = getResources().getDrawable(R.drawable.tab_audio_selected);

		Drawable exhibitor_sel = getResources().getDrawable(R.drawable.tab_video_selected);

		Drawable news = getResources().getDrawable(R.drawable.tab_audio);
		
		Drawable exhibitor = getResources().getDrawable(R.drawable.tab_video);

		buttons[AUDIO].setCompoundDrawablesWithIntrinsicBounds(null, news, null, null);
		buttons[AUDIO].setTextColor(buttons[AUDIO].getResources().getColor(R.color.bt_gray));
		
		buttons[VIDEO].setCompoundDrawablesWithIntrinsicBounds(null, exhibitor, null, null);
		buttons[VIDEO].setTextColor(buttons[VIDEO].getResources().getColor(R.color.bt_gray));
		if (index == AUDIO) {
			buttons[AUDIO].setCompoundDrawablesWithIntrinsicBounds(null, news_sel, null, null);
			buttons[AUDIO].setTextColor(buttons[AUDIO].getResources().getColor(R.color.bt_bule));
		} else if (index == VIDEO) {
			buttons[VIDEO].setCompoundDrawablesWithIntrinsicBounds(null, exhibitor_sel, null, null);
			buttons[VIDEO].setTextColor(buttons[VIDEO].getResources().getColor(R.color.bt_bule));
		}

	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.audio || id == R.id.video) {

			SwitchFragment(id);
		}
	}
	
	
	
}
