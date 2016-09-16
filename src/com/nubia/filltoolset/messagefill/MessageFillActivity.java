package com.nubia.filltoolset.messagefill;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.nubia.filltoolset.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.telephony.SmsManager;

public class MessageFillActivity extends FragmentActivity implements OnClickListener {
	
	public static LinearLayout container;// 装载sub Activity的容器
	SimpleFragment audioFragment;
	PersonalFragment videoFragment;
	int AUDIO = 0;
	int VIDEO = 1;
	Fragment lastFragment;
	Button buttons[] = new Button[2];
	private long waitTime = 2000;
	private long touchTime = 0;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		audioFragment = new SimpleFragment();
		videoFragment = new PersonalFragment();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_messagefill);
		container = (LinearLayout) findViewById(R.id.Container);
		buttons[AUDIO] = (Button) findViewById(R.id.simple_button);
		buttons[AUDIO].setOnClickListener(this);
		buttons[VIDEO] = (Button) findViewById(R.id.presonal_button);
		buttons[VIDEO].setOnClickListener(this);
		SwitchFragment(R.id.simple_button);
		
	
	}
	
	/* 根据ID打开指定的Activity */
	public void SwitchFragment(int id) {

		Fragment currentFragment = null;
//		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		if (id == R.id.simple_button) {
			refresh(AUDIO);
			currentFragment = audioFragment;
		} else if (id == R.id.presonal_button) {
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
		//Drawable news_sel = getResources().getDrawable(R.drawable.tab_audio_selected);

		//Drawable exhibitor_sel = getResources().getDrawable(R.drawable.tab_video_selected);

		//Drawable news = getResources().getDrawable(R.drawable.tab_audio);
		
		//Drawable exhibitor = getResources().getDrawable(R.drawable.tab_video);

//		buttons[AUDIO].setCompoundDrawablesWithIntrinsicBounds(null, news, null, null);
		buttons[AUDIO].setTextColor(buttons[AUDIO].getResources().getColor(R.color.bt_gray));
		
//		buttons[VIDEO].setCompoundDrawablesWithIntrinsicBounds(null, exhibitor, null, null);
		buttons[VIDEO].setTextColor(buttons[VIDEO].getResources().getColor(R.color.bt_gray));
		if (index == AUDIO) {
//			buttons[AUDIO].setCompoundDrawablesWithIntrinsicBounds(null, news_sel, null, null);
			buttons[AUDIO].setTextColor(buttons[AUDIO].getResources().getColor(R.color.bt_bule));
		} else if (index == VIDEO) {
//			buttons[VIDEO].setCompoundDrawablesWithIntrinsicBounds(null, exhibitor_sel, null, null);
			buttons[VIDEO].setTextColor(buttons[VIDEO].getResources().getColor(R.color.bt_bule));
		}

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
    // 菜单项被选择事件  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) { 
    	this.finish();
    	return false;
    	
    }
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.simple_button || id == R.id.presonal_button) {

			SwitchFragment(id);
		}
	}

}
