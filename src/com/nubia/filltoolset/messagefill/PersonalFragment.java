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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.TextView.OnEditorActionListener;

public class PersonalFragment extends Fragment implements OnItemClickListener{
	View rootView;
	private EditText etContent;
	private Button btnSubmit;
	private TextView tvWordsLimit;
	private final int TOTAL_WORDS = 100;	
	private String comment = null;
	ProgressBar progressbar;
	private EditText etUserNumber;
	private EditText etAmount;
	private static Random random = null;
	String phoneNumber;
	int fillCount,i = 0;
	String detailContent;
	
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
				etUserNumber.setText("");
				etAmount.setText("");
				etContent.setText("");
				Toast.makeText(getActivity(), "填充已完成", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.GONE);
			}			
		}
	
	};

	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_presonal, container, false);
		initView();
		return rootView;
    }
	private void initView() {
		etContent = (EditText)rootView.findViewById(R.id.eText_FeedBack_comment);
		btnSubmit = (Button)rootView.findViewById(R.id.btn_FeedBack_Submit);
		tvWordsLimit = (TextView)rootView.findViewById(R.id.tv_wordsLimit);
		
		etUserNumber = (EditText)rootView.findViewById(R.id.txtUserName);
		etAmount = (EditText)rootView.findViewById(R.id.txtPassword);
		progressbar = (ProgressBar)rootView.findViewById(R.id.progress1);

		/**
		 * 需要先检查输入框非空才可进行提交。
		 */
		btnSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doSend();
			}
		});
		
		/**
		 * 为输入框添加字数限制功能
		 */
		etContent.addTextChangedListener(new TextWatcher() {
			private CharSequence tmp;
			private int selectionStart;
			private int selectionEnd;
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				tmp = s;
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// 输入框右下角的字数统计TextView的刷新
				tvWordsLimit.setText("" + s.length() + "/" + TOTAL_WORDS);
				selectionStart = etContent.getSelectionStart();
				selectionEnd = etContent.getSelectionEnd();
				// 若输入字数大于限制值，则“退格”
				if (tmp.length() > TOTAL_WORDS) {
					s.delete(selectionStart - 1 , selectionEnd);
					etContent.setText(s);
					etContent.setSelection(TOTAL_WORDS);
				}
			}
		});
		
		etContent.setOnEditorActionListener(new OnEditorActionListener() {	
			@Override
			// 禁止输入回车
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
			}
		});

	}
	
	/**
	 * 退出应用程序
	 */
	private void doSend() {
		ShowDialogUtil.showDialog(getActivity(), "确认发送吗？", null, "确定", "取消", new DialogAction() {		
			@Override
			public void positiveAction() {
				phoneNumber = etUserNumber.getText().toString().trim();
				String sFillCount = etAmount.getText().toString().trim();
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
				if(phoneNumber.length() == 0){
					Toast.makeText(getActivity(), "请输入手机号码", Toast.LENGTH_SHORT).show();
					return;
				}else if(!checkeInformation()){
					Toast.makeText(getActivity(), "信息内容为空，将随机填充30字内容", Toast.LENGTH_SHORT).show();
				}
	
					detailContent = etContent.getText().toString().trim();
					new Thread(new Runnable() {
						@Override
						public void run() {	
							for(int j = 0;j< fillCount;j++ ){
								SmsManager smsManager =  SmsManager.getDefault();
								if(!checkeInformation()){
									smsManager.sendTextMessage(phoneNumber, null, getRandomString(30), null, null);
									
								}else{
							        if(detailContent.length() > 70) {
							            List<String> contents = smsManager.divideMessage(detailContent);
							            for(String sms : contents) {
							                smsManager.sendTextMessage(phoneNumber, null, sms, null, null);
							            }
							        }else {
							         smsManager.sendTextMessage(phoneNumber, null, detailContent, null, null);
							        }	
									
								}

						         Message msg = updateUIHandle.obtainMessage(0x03); 	
						         msg.obj = j + 1;
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
				
//					Toast.makeText(getActivity(), "信息内容发送成功",Toast.LENGTH_SHORT).show();
//				}


			}
			
			@Override
			public void negativeAction() {
			}
		});
		
	}
	
	private void submit(){
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
	
	

	/**
	 * 先获取所输入信息，然后检查非空，
	 * 而暂时仅查“comment”非空，
	 * @return 若空则返回false，反之true
	 */
	private boolean checkeInformation() { 
		comment = etContent.getText().toString();
		
		if (comment.length() > 0)
			return true;
		return false;
	}	


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}
}