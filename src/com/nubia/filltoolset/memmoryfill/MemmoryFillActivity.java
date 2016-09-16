package com.nubia.filltoolset.memmoryfill;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.DialogAction;
import com.nubia.filltoolset.utils.ShowDialogUtil;
import com.nubia.filltoolset.utils.file.FileManager;
import com.nubia.filltoolset.utils.file.FileRW;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

public class MemmoryFillActivity extends Activity {
	TextView display;
	EditText fillvalue;
	EditText fillpercent;
	Button submit;
	ProgressBar progerssBar;
	long total;
	long available;
	long used;
	float percent;
	float percent2;
	String value;
	int fileTotal = 0;
	int fileHas = 0;
	int test = 0;
	private long waitTime = 2000;
	private long touchTime = 0;
	//文件操作
	private FileRW fileRW = FileRW.getInstance(this);
	
	public  Handler updateUIHandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0x01)
			{
				if(msg.obj != null){
					display.setText((String)msg.obj);
				}
				
			}else if(msg.what == 0x02){
				if(msg.obj != null){
//					Toast.makeText(MemmoryFillActivity.this, "设置初始值：" + msg.obj, Toast.LENGTH_SHORT).show();
					progerssBar.setVisibility(View.VISIBLE);
				    progerssBar.setMax((Integer) msg.obj);
				    progerssBar.setProgress(0);
				}

			}else if(msg.what == 0x03){
				if(msg.obj != null){
//					Toast.makeText(MemmoryFillActivity.this, "进度：" + msg.obj + "M", Toast.LENGTH_SHORT).show();
					progerssBar.setProgress((Integer) msg.obj);
				}	
			}else if(msg.what == 0x04)	{
				Toast.makeText(MemmoryFillActivity.this, "填充已完成", Toast.LENGTH_SHORT).show();
			    fillvalue.setText("");
			    fillpercent.setText("");
				progerssBar.setVisibility(View.GONE);
			}			
		}
	
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_memmoryfill);
		initView();
		DoTimer();
	}

	private void initView(){
		display = (TextView)findViewById(R.id.text_display);
		fillvalue = (EditText)findViewById(R.id.fillvalue);
		fillpercent = (EditText)findViewById(R.id.fillpercent);
		submit = (Button)findViewById(R.id.btn_Submit);
		progerssBar = (ProgressBar)findViewById(R.id.progress1);
		/**
		 * 需要先检查输入框非空才可进行提交。
		 */
		submit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doSend();
			}
		});
	}
	private void doSend() {
		ShowDialogUtil.showDialog(MemmoryFillActivity.this, "确认填充吗？", null, "确定", "取消", new DialogAction() {		
			@Override
			public void positiveAction() {
				submit.setClickable(false);
				float fillValue = 0;
				int fillPercent;
				String sFillValue = fillvalue.getText().toString().trim();
				String sFillPercent = fillpercent.getText().toString().trim();
				if(sFillValue.length()> 0){
					fillValue = Integer.parseInt(sFillValue);
					if(fillValue < 0 || fillValue > 24000){
						submit.setClickable(true);
						Toast.makeText(MemmoryFillActivity.this, "请输入正确的填充值", Toast.LENGTH_SHORT).show();
						return;
					}
					fileTotal = (int) fillValue;
					new Thread(new Runnable() {
						@Override
						public void run() {		     
					         Message msg = updateUIHandle.obtainMessage(0x02); 	
					         msg.obj = fileTotal;
					         msg.sendToTarget();
						}
					}).start();	

					final int times = (int) (fillValue / 30); //一次最多填36M
					final int last = (int) (fillValue % 30);
						new Thread(new Runnable() {
							@Override
							public void run() {	
								for(int i = 0;i < times ;i++){
									byte[] data = new byte[(int) (1024 * 1024 * 30)];
								    String Sdata = new String(data);
								    WriteToFile(Sdata);
								    fileHas = (int) 30 * (i + 1);
						         Message msg = updateUIHandle.obtainMessage(0x03); 	
						         msg.obj = fileHas;
						         msg.sendToTarget();										
								}
								byte[] data = new byte[(int) (1024 * 1024 * last)];
							    String Sdata = new String(data);
							    WriteToFile(Sdata);
							    fileHas = fileHas + last;
						         Message msg = updateUIHandle.obtainMessage(0x03); 	
						         msg.obj = fileHas;
						         msg.sendToTarget();
						         
								    DoTimer();
								    submit.setClickable(true);
								    fileTotal = 0;
								    fileHas = 0;	
								    try {
										Thread.sleep(2000);
								         Message msg01 = updateUIHandle.obtainMessage(0x04); 	
								         msg01.sendToTarget();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							}
						}).start();

	    
				   // Toast.makeText(MemmoryFillActivity.this, "填充成功", Toast.LENGTH_SHORT).show();
				}else if(sFillPercent.length() >0){
					fillPercent = Integer.parseInt(sFillPercent);
					if(fillPercent < percent || fillPercent > 99){
						submit.setClickable(true);
						Toast.makeText(MemmoryFillActivity.this, "请输入正确的百分比", Toast.LENGTH_SHORT).show();
						return;
					}
					fillValue =  (float) ((fillPercent - percent)/100.0 * total);
				//	Toast.makeText(MemmoryFillActivity.this, "zhid da xiao" + fillValue , Toast.LENGTH_SHORT).show();
					fileTotal = (int) fillValue;
					new Thread(new Runnable() {
						@Override
						public void run() {		     
					         Message msg = updateUIHandle.obtainMessage(0x02); 		
					         msg.obj = fileTotal;
					         msg.sendToTarget();
						}
					}).start();				
					final int times = (int) (fillValue / 30); //一次最多填36M
					final int last = (int) (fillValue % 30);
						new Thread(new Runnable() {
							@Override
							public void run() {	
								for(int i = 0;i < times ;i++){
									byte[] data = new byte[(int) (1024 * 1024 * 30)];
								    String Sdata = new String(data);
								    WriteToFile(Sdata);
								    fileHas = (int) 30 * (i + 1);
						         Message msg = updateUIHandle.obtainMessage(0x03); 	
						         msg.obj = fileHas;
						         msg.sendToTarget();
								}
								byte[] data = new byte[(int) (1024 * 1024 * last)];
							    String Sdata = new String(data);
							    WriteToFile(Sdata);
							    fileHas = fileHas + last ;
						         Message msg = updateUIHandle.obtainMessage(0x03); 	
						         msg.obj = fileHas;
						         msg.sendToTarget();
						         
								    DoTimer();
								    submit.setClickable(true);
								    fileTotal = 0;
								    fileHas = 0;	
								    try {
										Thread.sleep(2000);
								         Message msg01 = updateUIHandle.obtainMessage(0x04); 	
								         msg01.sendToTarget();
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
							}
						}).start();


//				    Toast.makeText(MemmoryFillActivity.this, "正在成功", Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(MemmoryFillActivity.this, "你没有输入任何值", Toast.LENGTH_SHORT).show();
				}	
				submit.setClickable(true);
			}
			
			@Override
			public void negativeAction() {
			}
		});
		
	}
	
	public void WriteToFile(String data){
			if(fileRW != null && !fileRW.isFileClosed()){
				fileRW.writeFile(data);			
			}else{
				if(fileRW != null){
					Log.d("yang", "fileRW != null");
					Time t = new Time();
					t.setToNow();
					String rootPath = FileManager.getRootPath();
					if(rootPath != null){
						String fileDir = rootPath
								+ File.separator
								+ FileManager.getROOT_DIR()
								+ File.separator
								+ "FillFile";
						//没有文件夹时,先创建文件夹
						Log.d("yang", fileDir);
						File dir = new File(fileDir);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						File outfile = fileRW
								.createFile(
										this,
										fileDir,
										fileRW.getTimeFileName(t)+FileManager.TestResult_FILE_SUFFIX,
										true);
						fileRW.writeFile(data);
					}
				}
				
			}		
	}	
	private void DoTimer(){
		Date time = new Date();
		long delay = 2000;
		Timer timer = new Timer(false);
		TimerTask task = new TimerTask(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File sdcardDir = Environment.getExternalStorageDirectory();
				StatFs statFs = new StatFs(sdcardDir.getPath());
				/** Block 的 size*/ 
				long blockSize= statFs.getBlockSize(); 
				/** 总 Block 数量 */ 
				long totalBlocks= statFs.getBlockCount(); 
				/** 已使用的 Block 数量 */ 
				long availableBlocks= statFs.getAvailableBlocks();
				total = blockSize*totalBlocks/(1024*1024);
				available = blockSize*availableBlocks/(1024*1024);
				used = total - available;
				percent =  (float)used/(float)total *100;
				BigDecimal bd = new BigDecimal(percent);
				percent2 = bd.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
				value = total+ "M   " + used + "M   " + available + "M         " + percent2 + "%";
				Message msg = updateUIHandle.obtainMessage(0x01);
				msg.obj = value; 			                  
				msg.sendToTarget();		
			}
			
		};
		timer.schedule(task, time);
		//timer.schedule(task, time,delay);
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
    	//this.finish();
    	String rootPath = "/storage/sdcard0";
		String fileDir = rootPath
				+ File.separator
				+ FileManager.getROOT_DIR() + File.separator +"FillFile";
		if(FileManager.isFileExists("FillFile")){	
			try{
				String lastFile;
				File f = new File(fileDir);
				File[] files = f.listFiles();
				if(files != null){
					int count = files.length;
					if(count == 0){
						Toast.makeText(MemmoryFillActivity.this,"没有文件啦", Toast.LENGTH_LONG).show();
					}else{																		
						lastFile = files[0].getPath();									
					    for(int i = 1;i < count ;i++){						    	
						    if(files[i].getName().compareTo(lastFile) > 0){									    	
							    lastFile = files[i].getPath();
						    }
					    }							    
					    File file = new File(lastFile);
	                    if(file.isFile() && file.exists()){
	                    	file.delete();
	                    }
	                    DoTimer();
	                    Toast.makeText(MemmoryFillActivity.this,"删除上次填充成功", Toast.LENGTH_LONG).show();
					}
				
				}			
			}catch(Exception e){			
			}
		}else{
			Toast.makeText(MemmoryFillActivity.this,"文件路径不存在啦", Toast.LENGTH_LONG).show();					
		}

	
    	
    	return false;
    	
    }

}
