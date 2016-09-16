package com.nubia.filltoolset.contactsfill;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.DialogAction;
import com.nubia.filltoolset.utils.ShowDialogUtil;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ContactsFillActivity extends Activity implements OnItemClickListener{
	private Button btnSubmit;
	private EditText etAmount;
	private ProgressBar progressbar;
	private static Random random = null;
	private long waitTime = 2000;
	private long touchTime = 0;
	private int i = 0;
	
	public  Handler updateUIHandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0x01)
			{
				if(msg.obj != null){
				}
				
			}else if(msg.what == 0x02){
				if(msg.obj != null){
//					Toast.makeText(ContactsFillActivity.this, "设置初始值：" + msg.obj, Toast.LENGTH_SHORT).show();
					progressbar.setVisibility(View.VISIBLE);
					progressbar.setMax((Integer) msg.obj);
					progressbar.setProgress(0);
				}

			}else if(msg.what == 0x03){
				if(msg.obj != null){
//					Toast.makeText(ContactsFillActivity.this, "进度：" + msg.obj + "M", Toast.LENGTH_SHORT).show();
					progressbar.setProgress((Integer) msg.obj);
				}	
			}else if(msg.what == 0x04)	{
				etAmount.setText("");
				Toast.makeText(ContactsFillActivity.this, "填充已完成", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.GONE);
			}			
		}
	
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactsfill);
		initView();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void initView(){
		etAmount = (EditText)findViewById(R.id.txtPassword);
		btnSubmit = (Button)findViewById(R.id.btn_FeedBack_Submit);
		progressbar = (ProgressBar)findViewById(R.id.progress1);
		/**
		 * 
		 */
		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ShowDialogUtil.showDialog(ContactsFillActivity.this, "确定填充吗", null, "确定", "取消", new DialogAction() {		
					@Override
					public void positiveAction() {
						final int fillCount;
						String sFillCount = etAmount.getText().toString().trim();
						if(sFillCount.length()> 0){
							fillCount = Integer.parseInt(sFillCount);
							if(fillCount < 1){
								Toast.makeText(ContactsFillActivity.this, "请输入正确的条数", Toast.LENGTH_SHORT).show();
								return;
							}
						}else{
							fillCount = 1;
						}
						new Thread(new Runnable() {
							@Override
							public void run() {		     
						         Message msg = updateUIHandle.obtainMessage(0x02); 	
						         msg.obj = fillCount;
						         msg.sendToTarget();
							}
						}).start();	
						new Thread(new Runnable() {
							@Override
							public void run() {	
								for(i = 0;i < fillCount;i++){
									try {
										addContacts();
									} catch (Exception e) {
										e.printStackTrace();
									}	
							         Message msg = updateUIHandle.obtainMessage(0x03); 	
							         msg.obj = i + 1;
							         msg.sendToTarget();
								}
							    try {
									Thread.sleep(2000);
							         Message msg01 = updateUIHandle.obtainMessage(0x04); 	
							         msg01.sendToTarget();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
		
							}
						}).start();	
						//Toast.makeText(ContactsFillActivity.this, "填充成功", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void negativeAction() {
						
					}
				});
			}
		});
	}
    public void addContacts() throws Exception {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri)
            .withValue("account_name", null)
            .build();
        operations.add(op1);
        
        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri)
            .withValueBackReference("raw_contact_id", 0)
            .withValue("mimetype", "vnd.android.cursor.item/name")
            .withValue("data2", getChinese() + getChinese())
            .build();
        operations.add(op2);
        
        ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri)
            .withValueBackReference("raw_contact_id", 0)
            .withValue("mimetype", "vnd.android.cursor.item/phone_v2")
            .withValue("data1", "10000" + i)            
            .withValue("data2", "2")
            .build();
        operations.add(op3);
        
        ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri)
        .withValueBackReference("raw_contact_id", 0)
        .withValue("mimetype", "vnd.android.cursor.item/email_v2")
        .withValue("data1", "abc"+ i +"@163.com")            
        .withValue("data2", "2")
        .build();
    operations.add(op4);
        
        resolver.applyBatch("com.android.contacts", operations);
    }
	private static Random getRandomInstance(){
		if(random == null){
			random = new Random(new Date().getTime());
		}
		return random;
	}
    public static String getChinese() {  
        String str = null;  
        int highPos, lowPos;  
        Random random = getRandomInstance();  
        highPos = (176 + Math.abs(random.nextInt(39)));  
        lowPos = 161 + Math.abs(random.nextInt(93));  
        byte[] b = new byte[2];  
        b[0] = (new Integer(highPos)).byteValue();  
        b[1] = (new Integer(lowPos)).byteValue();  
        try {  
            str = new String(b, "GB2312");  
        } catch (UnsupportedEncodingException e) {  
            e.printStackTrace();  
        }  
        return str;  
    } 

    // 菜单项被选择事件  
    @Override  
    public boolean onOptionsItemSelected(MenuItem item) { 
    	this.finish();
    	return false;
    	
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}


}
