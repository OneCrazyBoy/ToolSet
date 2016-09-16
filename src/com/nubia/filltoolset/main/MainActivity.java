package com.nubia.filltoolset.main;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.audioandvideotest.AudioAndVideoTestActivity;
import com.nubia.filltoolset.contactsfill.ContactsFillActivity;
import com.nubia.filltoolset.memmoryfill.MemmoryFillActivity;
import com.nubia.filltoolset.messagefill.MessageFillActivity;
import com.nubia.filltoolset.mmsfill.MmsFillActicity;
import com.nubia.filltoolset.niuduncleartest.NiuduncleartestActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private long waitTime = 2000;
	private long touchTime = 0;
    private Button messageFill;
    private Button mmsFill;
    private Button contactsFill;
    private Button memmoryFill;
    private Button andioandvideotest;
    private Button niuduncleartest;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		initView();
	}
    private void initView(){
    	messageFill = (Button) findViewById(R.id.btn_messagefill);
    	mmsFill = (Button) findViewById(R.id.btn_mmsfill);
    	contactsFill = (Button) findViewById(R.id.but_contactsfill);
    	memmoryFill = (Button) findViewById(R.id.btn_memmoryfill);
    	andioandvideotest = (Button)findViewById(R.id.btn_andioandvideotest);
    	niuduncleartest = (Button) findViewById(R.id.btn_niuduncleartest);
    	messageFill.setOnClickListener(this);
    	mmsFill.setOnClickListener(this);
    	contactsFill.setOnClickListener(this);
    	memmoryFill.setOnClickListener(this);
    	andioandvideotest.setOnClickListener(this);
    	niuduncleartest.setOnClickListener(this);
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
	
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - touchTime) >= waitTime) {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			touchTime = currentTime;
		} else {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			this.finish();
//			android.os.Process.killProcess(android.os.Process.myPid());
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_messagefill:
			Intent messagefill = new Intent(this,MessageFillActivity.class);
			startActivity(messagefill);
			break;
		case R.id.btn_mmsfill:
			Intent mmsfill = new Intent(this,MmsFillActicity.class);
			startActivity(mmsfill);
			//Toast.makeText(this, "待实现，请稍后 ...", Toast.LENGTH_SHORT).show();
			break;
		case R.id.but_contactsfill:
			Intent contactsfill = new Intent(this,ContactsFillActivity.class);
			startActivity(contactsfill);
			break;
		case R.id.btn_memmoryfill:
			Intent memmoryfill = new Intent(this,MemmoryFillActivity.class);
			startActivity(memmoryfill);
			break;
		case R.id.btn_andioandvideotest:
			Intent andioandvideotest = new Intent(this,AudioAndVideoTestActivity.class);
			startActivity(andioandvideotest);
			break;
		case R.id.btn_niuduncleartest:
			Intent niuduncleartest = new Intent(this,NiuduncleartestActivity.class);
			startActivity(niuduncleartest);
			break;
		default:
			break;
		}
	}

}
