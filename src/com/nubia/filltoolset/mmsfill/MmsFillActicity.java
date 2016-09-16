package com.nubia.filltoolset.mmsfill;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.contactsfill.ContactsFillActivity;
import com.nubia.filltoolset.mmsfill.Telephony.Mms;
import com.nubia.filltoolset.mmsfill.Telephony.Threads;
import com.nubia.filltoolset.mmsfill.Telephony.Mms.Addr;
import com.nubia.filltoolset.mmsfill.Telephony.Mms.Part;
import com.nubia.filltoolset.utils.DialogAction;
import com.nubia.filltoolset.utils.ShowDialogUtil;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MmsFillActicity extends Activity{
	private Button btnSubmit;
	private EditText etAmount;
	private ProgressBar progressbar;
	private int i = 0;
	
    private static final String IMAGE_NAME_1 = "image_1.jpeg";
    private static final String IMAGE_NAME_2 = "image_2.jpeg";
    private static final String SMIL_TEXT_IMAGE = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"2000ms\"><text src=\"text_1.txt\" region=\"Text\"/><img src=\"%s\" region=\"Image\"/></par><par dur=\"2000ms\"><text src=\"text_2.txt\" region=\"Text\"/><img src=\"%s\" region=\"Image\"/></par></body></smil>";
    private static final String IMAGE_CID = "<img_cid>";
    private static final String AUDIO_NAME = "audio_1.ogg";
    private static final String SMIL_TEXT_AUDIO = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"21500ms\"><text src=\"text_1.txt\" region=\"Text\"/><audio src=\""
            + AUDIO_NAME + "\" dur=\"21500\" /></par></body></smil>";
    private static final String AUDIO_CID = "<300k>";
    private static final String VIDEO_NAME = "video_1.3gp";
    private static final String SMIL_TEXT_VIDEO = "<smil><head><layout><root-layout width=\"320px\" height=\"480px\"/><region id=\"Text\" left=\"0\" top=\"320\" width=\"320px\" height=\"160px\" fit=\"meet\"/><region id=\"Image\" left=\"0\" top=\"0\" width=\"320px\" height=\"320px\" fit=\"meet\"/></layout></head><body><par dur=\"21500ms\"><text src=\"text_1.txt\" region=\"Text\"/><VIDEO src=\""
            + VIDEO_NAME + "\" dur=\"21500\" /></par></body></smil>";
    private static final String VIDEO_CID = "<300k>";
    private static final String FROM_NUM = "146";
	
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
				Toast.makeText(MmsFillActicity.this, "填充已完成", Toast.LENGTH_SHORT).show();
				progressbar.setVisibility(View.GONE);
			}			
		}
	
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mmsfill);
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
				ShowDialogUtil.showDialog(MmsFillActicity.this, "确定填充吗", null, "确定", "取消", new DialogAction() {		
					@Override
					public void positiveAction() {
						final int fillCount;
						String sFillCount = etAmount.getText().toString().trim();
						if(sFillCount.length()> 0){
							fillCount = Integer.parseInt(sFillCount);
							if(fillCount < 1){
								Toast.makeText(MmsFillActicity.this, "请输入正确的条数", Toast.LENGTH_SHORT).show();
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
										mmsWrite();
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
	
	private void mmsWrite(){
		//insert(Mms.MESSAGE_BOX_INBOX,AttachmentType.AUDIO,0);
		//insert(Mms.MESSAGE_BOX_SENT,AttachmentType.IMAGE,1);
		insert(Mms.MESSAGE_BOX_INBOX,AttachmentType.VIDEO,2);
	}
	
	private void insert(int msgBoxType,AttachmentType type,int idx){
        try {
            long threadId = Threads.getOrCreateThreadId(MmsFillActicity.this, FROM_NUM + idx);
            Log.e("", "threadId = " + threadId);

            String name_1 = null;
            String name_2 = null;
            String smil_text = null;
            ContentValues cv_part_1 = null;
            ContentValues cv_part_2 = null;

            switch (type) {
                case IMAGE:
                    name_1 = IMAGE_NAME_1;
                    name_2 = IMAGE_NAME_2;
                    smil_text = String.format(SMIL_TEXT_IMAGE, name_1, name_2);
                    cv_part_1 = createPartRecord(0, "image/jpeg", name_1, IMAGE_CID, name_1, null, null);
                    cv_part_2 = createPartRecord(0, "image/jpeg", name_2, IMAGE_CID.replace("cid", "cid_2"), name_2, null, null);
                    break;
                case AUDIO:
                    name_1 = AUDIO_NAME;
                    smil_text = String.format(SMIL_TEXT_AUDIO, name_1);
                    cv_part_1 = createPartRecord(0, "audio/ogg", AUDIO_NAME, AUDIO_CID, AUDIO_NAME, null, null);
                    break;
                case VIDEO:
                    name_1 = VIDEO_NAME;
                    smil_text = String.format(SMIL_TEXT_VIDEO, name_1);
                    cv_part_1 = createPartRecord(0, "video/3gpp", VIDEO_NAME, VIDEO_CID, VIDEO_NAME, null, null);
                    break;
            }

            // make MMS record
            ContentValues cvMain = new ContentValues();
            cvMain.put(Mms.THREAD_ID, threadId);

            cvMain.put(Mms.MESSAGE_BOX, msgBoxType);
            cvMain.put(Mms.READ, 1);
            cvMain.put(Mms.DATE, System.currentTimeMillis());
            cvMain.put(Mms.SUBJECT, "MMS test " + idx);
            
            cvMain.put(Mms.CONTENT_TYPE, "application/vnd.wap.multipart.related");
            cvMain.put(Mms.MESSAGE_CLASS, "personal");
            cvMain.put(Mms.MESSAGE_TYPE, 132); // Retrive-Conf Mms
            //cvMain.put(Mms.MESSAGE_SIZE, getSize(name_1) + getSize(name_2) + 512);  // suppose have 512 bytes extra text size
            cvMain.put(Mms.MESSAGE_SIZE, 512 * 3);
            cvMain.put(Mms.PRIORITY, String.valueOf(129));
            cvMain.put(Mms.READ_REPORT, String.valueOf(129));
            cvMain.put(Mms.DELIVERY_REPORT, String.valueOf(129));
            Random random = new Random();
            cvMain.put(Mms.MESSAGE_ID, String.valueOf(random.nextInt(100000)));
            cvMain.put(Mms.TRANSACTION_ID, String.valueOf(random.nextInt(120000)));

            long msgId = 0;
            msgId = ContentUris.parseId(getContentResolver().insert(Mms.CONTENT_URI, cvMain));


            // make parts
            ContentValues cvSmil = createPartRecord(-1, "application/smil", "smil.xml", "<siml>", "smil.xml", null, smil_text);
            cvSmil.put(Part.MSG_ID, msgId);

            cv_part_1.put(Part.MSG_ID, msgId);
           // cv_part_2.put(Part.MSG_ID, msgId);

//            ContentValues cv_text_1 = createPartRecord(0, "text/plain", "text_1.txt", "<text_1>", "text_1.txt", null, null);
//            cv_text_1.put(Part.MSG_ID, msgId);
//            cv_text_1.remove(Part.TEXT);
//            cv_text_1.put(Part.TEXT, "slide 1 text");
//            cv_text_1.put(Part.CHARSET, "106");

//            ContentValues cv_text_2 = createPartRecord(0, "text/plain", "text_2.txt", "<text_2>", "text_2.txt", null, null);
//            cv_text_2.put(Part.MSG_ID, msgId);
//            cv_text_2.remove(Part.TEXT);
//            cv_text_2.put(Part.TEXT, "slide 2 text");
//            cv_text_2.put(Part.CHARSET, "106");

            // insert parts
            Uri partUri = Uri.parse("content://mms/" + msgId + "/part");
            try {
                getContentResolver().insert(partUri, cvSmil);

                Uri dataUri_1 = getContentResolver().insert(partUri, cv_part_1);
                if (!copyData(dataUri_1, name_1)) {
                    Log.e("", "write " + name_1 + " failed");
                    return;
                }
              //  getContentResolver().insert(partUri, cv_text_1);

//                Uri dataUri_2 = getContentResolver().insert(partUri, cv_part_2);
//                if (!copyData(dataUri_2, name_2)) {
//                    Log.e("", "write " + name_2 + " failed");
//                    return;
//                }
              //  getContentResolver().insert(partUri, cv_text_2);
            } catch (Exception e) {
                Log.e("", "insert part failed", e);
                return;
            }

            // to address
            ContentValues cvAddr = new ContentValues();
//            cvAddr.put(Addr.MSG_ID, msgId);
//            cvAddr.put(Addr.ADDRESS, "703");
//            cvAddr.put(Addr.TYPE, "151");
//            cvAddr.put(Addr.CHARSET, 106);
//            getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);

            // from address
            cvAddr.clear();
            cvAddr.put(Addr.MSG_ID, msgId);
            cvAddr.put(Addr.ADDRESS, FROM_NUM + idx);
            cvAddr.put(Addr.TYPE, "137");
            cvAddr.put(Addr.CHARSET, 106);
            getContentResolver().insert(Uri.parse("content://mms/" + msgId + "/addr"), cvAddr);
            
        } catch (Exception e) {
            Log.e("", "insert pdu record failed", e);
            Toast.makeText(this, "发送彩信拒绝成功", Toast.LENGTH_LONG).show();
            return;
        }
	}
	
    private int getSize(final String name) {
        InputStream is = null;
        int size = 0;

        try {
            is = getAssets().open(name);
            byte[] buffer = new byte[1024];
            for (int len = 0; (len = is.read(buffer)) != -1;)
                size += len;
            return size;
        } catch (FileNotFoundException e) {
            Log.e("", "failed to found file?", e);
            return 0;
        } catch (IOException e) {
            Log.e("", "write failed..", e);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                Log.e("", "close failed...");
            }
        }
        return 0;
    }

    private ContentValues createPartRecord(int seq, String ct, String name, String cid, String cl, String data,
            String text) {
        ContentValues cv = new ContentValues(8);
        cv.put(Part.SEQ, seq);
        cv.put(Part.CONTENT_TYPE, ct);
        cv.put(Part.NAME, name);
        cv.put(Part.CONTENT_ID, cid);
        cv.put(Part.CONTENT_LOCATION, cl);
        if (data != null)
            cv.put(Part._DATA, data);
        if (text != null)
            cv.put(Part.TEXT, text);
        return cv;
    }

    private boolean copyData(final Uri dataUri, final String name) {
        InputStream input = null;
        OutputStream output = null;

        try {
            input = getAssets().open(name);
            output = getContentResolver().openOutputStream(dataUri);

            byte[] buffer = new byte[1024];
            for (int len = 0; (len = input.read(buffer)) != -1;) 
                output.write(buffer, 0, len);
        } catch (FileNotFoundException e) {
            Log.e("", "failed to found file?", e);
            return false;
        } catch (IOException e) {
            Log.e("", "write failed..", e);
            return false;
        } finally {
            try {
                if (input != null)
                    input.close();
                if (output != null)
                    output.close();
            } catch (IOException e) {
                Log.e("", "close failed...");
                return false;
            }
        }
        return true;
    }
	
    enum AttachmentType {
        IMAGE, AUDIO, VIDEO;
    }

}
