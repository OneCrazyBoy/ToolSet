package com.nubia.filltoolset.messagefill;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.DialogAction;
import com.nubia.filltoolset.utils.ShowDialogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SimpleFragment extends Fragment implements OnItemClickListener{
	View rootView;
	private EditText etAmount;
	private EditText etWordNumber;
	ProgressBar progressbar;
	private Button btnSubmit;
	private static Random random = null;
	String phoneNumber;
	private int i = 0;
	int fillCount;
	int wordNumber;
	
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
//					Toast.makeText(MessageFillActivity.this, "设置初始值：" + msg.obj, Toast.LENGTH_SHORT).show();
					progressbar.setVisibility(View.VISIBLE);
					progressbar.setMax((Integer) msg.obj);
					progressbar.setProgress(0);
				}

			}else if(msg.what == 0x03){
				if(msg.obj != null){
//					Toast.makeText(MessageFillActivity.this, "进度：" + msg.obj + "M", Toast.LENGTH_SHORT).show();
					progressbar.setProgress((Integer) msg.obj);
				}	
			}else if(msg.what == 0x04)	{
				etAmount.setText("");
				etWordNumber.setText("");
				Toast.makeText(getActivity(), "填充已完成", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.GONE);
			}			
		}
	
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_simple, container, false);
		initView();
		return rootView;
}
	private void initView() {
		etAmount = (EditText)rootView.findViewById(R.id.txtPassword);
		etWordNumber = (EditText)rootView.findViewById(R.id.txtConfirmPassword);
		btnSubmit = (Button)rootView.findViewById(R.id.btn_FeedBack_Submit);
		progressbar = (ProgressBar)rootView.findViewById(R.id.progress1);

		/**
		 * 需要先检查输入框非空才可进行提交。
		 */
		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doSend();
			}
		});
	}
	
	private void doSend() {
		ShowDialogUtil.showDialog(getActivity(), "确认发送吗？", null, "确定", "取消", new DialogAction() {		
			@Override
			public void positiveAction() {
				String sFillCount = etAmount.getText().toString().trim();
				String sWordNumber = etWordNumber.getText().toString().trim();
				if(sFillCount.length()> 0){
					fillCount = Integer.parseInt(sFillCount);
					if(fillCount < 1){
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
				if(sWordNumber.length() >0){
					wordNumber = Integer.parseInt(sWordNumber);
					if(wordNumber <1){
						return;
					}
				}else{
					wordNumber = 10;
				}	
				new Thread(new Runnable() {
					@Override
					public void run() {	
						for(i = 0;i< fillCount;i++ ){
							if(fillCount > 0){
								phoneNumber = "10000" + i;
							}else{
								phoneNumber = "10000" + i;
							}
							String content = getRandomString(wordNumber);
							SmsManager smsManager =  SmsManager.getDefault();
					        if(content.length() > 70) {
					            List<String> contents = smsManager.divideMessage(content);
					            for(String sms : contents) {
					                smsManager.sendTextMessage(phoneNumber, null, sms, null, null);
					            }
					        } else {
					         smsManager.sendTextMessage(phoneNumber, null, content, null, null);
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

			}
			
			@Override
			public void negativeAction() {
			}
		});
		
	}
	
	private String getRandomString(int length){  
        Random random = new Random();  
          
        StringBuffer sb = new StringBuffer();  
          
        for(int i = 0; i < length; ++i){  
            int number = random.nextInt(4);  
            long result = 0;  
              
            switch(number){  
            case 0:  
                result = Math.round(Math.random() * 25 + 65);  
                sb.append(String.valueOf((char)result));  
                break;  
            case 1:  
                result = Math.round(Math.random() * 25 + 97);  
                sb.append(String.valueOf((char)result));  
                break;  
            case 2:  
                sb.append(String.valueOf(new Random().nextInt(10)));  
                break;  
            case 3:
            	sb.append(getChinese());
            	break;
            	
            }  
        }  
        return sb.toString();     
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
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
	}
}
