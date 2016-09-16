package com.nubia.filltoolset.audioandvideotest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.file.FileManager;
import com.nubia.filltoolset.utils.file.FileRW;
import com.nubia.filltoolset.utils.xml.XMLFileBean;
import com.nubia.filltoolset.utils.xml.XMLPullAudioAndVideoFiles;
import com.nubia.filltoolset.utils.xml.XMLSort;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AudioFragment extends Fragment implements OnItemClickListener{
	View rootView;
	Button btnTest;
	Button btnStop;
	ListView listview;
	TextView textView;
	InputStream is;
	MediaPlayer mPlayer;
	int current = 0;
	int totalTime = 0;
	boolean isclick = false;
	public MyArrayAdapter mAdapter;
	XMLSort xmlSort = null ;
	List<XMLFileBean> XMLFileBeans = null;
	private ArrayList<XMLFileBean> list = new ArrayList<XMLFileBean>();
	Map<String,List<XMLFileBean>> XMLMap = new HashMap<String,List<XMLFileBean>>();
	//文件操作
	private FileRW fileRW = FileRW.getInstance(getActivity());
	public  Handler fileNextHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0x01)
			{
				Log.d("awy", "handlerlimian");		
	           Play();	 
			}					
		}
	
	};
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.audiofragment, container, false);
		listview = (ListView)rootView.findViewById(R.id.list_view);
		mAdapter = new MyArrayAdapter(getActivity(), R.layout.list_item,list);
		listview.setAdapter(mAdapter);
		listview.setOnItemClickListener(this);
		mPlayer = new MediaPlayer();
		textView = (TextView)rootView.findViewById(R.id.showingtv1);
		btnTest = (Button)rootView.findViewById(R.id.btn_test);
		btnTest.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//btnTest.setClickable(isHidden());
				//btnStop.setClickable(isVisible());
				//btnStop.setClickable(isHidden());
				WriteToFile(" 测试开始");
				Play();	
				
			}
		});
		btnStop = (Button)rootView.findViewById(R.id.btn_stop);
		btnStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//btnStop.setClickable(isHidden());
				//btnTest.setClickable(isVisible());
				current = 0 ;
				if(mPlayer != null){
					mPlayer.stop();
				}
					
			}
		});
		
		LoadData();
		
		mPlayer.setOnErrorListener(new OnErrorListener(){
			@Override		
			public boolean onError(MediaPlayer mp,int what,int extra){
				
				Toast.makeText(getActivity(),"播放出错了", Toast.LENGTH_LONG).show();
				//针对错误进行处理
				// .....
				
				return false;
			}
		});
		
		mPlayer.setOnCompletionListener(new OnCompletionListener(){
			@Override
			public void onCompletion(MediaPlayer mp) {
//				if(!isclick){
				if(XMLFileBeans != null && XMLFileBeans.size() > 0){
					WriteToFile(" YES");
					current++;
					if(current >= XMLFileBeans.size()){
						mPlayer.stop();
						textView.setText("测试完成");
						WriteToFile("测试完成");
						return;	
					}
					if(!FileManager.isFileExists(XMLFileBeans.get(current).getFileName())){
						WriteToFile(" 此播放文件不存在");
						Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName() + ",播放文件不存在", Toast.LENGTH_LONG).show();
						 if(!isclick){
							 isclick = false;
						     fileNextHandler.sendEmptyMessage(0x01);
	                   }
					}else{
						 if(!isclick){
							 isclick = false;
						     fileNextHandler.sendEmptyMessage(0x01);
						    
	                   }
					}
				}

					
//				}
					
			}
		});
		

		return rootView;
	}
	
	//当用户单击某列表时激发该回调方法
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
	       RelativeLayout layout = ((RelativeLayout)arg1);
	       //layout.setBackgroundColor(getResources().getColor(R.color.listclick));
	       TextView textView01 = (TextView)layout.getChildAt(1);
	       TextView textView02 = (TextView)layout.getChildAt(2);
	       textView01.setTextColor(android.graphics.Color.GRAY);
	       //textView02.setTextColor(android.graphics.Color.GRAY);
	       isclick = true;
	       current = position;
			if(FileManager.isFileExists(XMLFileBeans.get(current).getFileName())){
		    	
		    	try {
		    		mPlayer.reset();
					mPlayer.setDataSource(FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getAndioAndVideoFiles_DIR_NAME() + File.separator + XMLFileBeans.get(current).getFileName());
					mPlayer.prepare();
					totalTime = mPlayer.getDuration();
					mPlayer.start();
	                if(totalTime > 5000){
	                	Timer mTimer = new Timer();
	                	mTimer.schedule(new TimerTask(){
	                		@Override
	                		public void run(){
	                			mPlayer.stop();
	                			return;
	                		}
	                	}, 5000);
	                	
	                }
		    	} catch (IllegalArgumentException e1) {
					e1.printStackTrace();
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (IllegalStateException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+",播放失败", Toast.LENGTH_LONG).show();
					e1.printStackTrace();
				}
				
				

		}else{
			Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+",此播放文件不存在", Toast.LENGTH_LONG).show();
		}
	}
		
	public void Play(){
				
		try {				    	
			    if(XMLFileBeans != null && !XMLFileBeans.isEmpty()){
					if(current >= XMLFileBeans.size()){
						mPlayer.stop();
						WriteToFile("测试完成");
						return;	
					}
					else if(FileManager.isFileExists(XMLFileBeans.get(current).getFileName())){
				    	mPlayer.reset();
				    	mPlayer.setDataSource(FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getAndioAndVideoFiles_DIR_NAME() + File.separator + XMLFileBeans.get(current).getFileName());
						mPlayer.prepare();
						totalTime = mPlayer.getDuration();
						Log.d("yang", "" + totalTime);
						//mPlayer.getTrackInfo().toString();
						mPlayer.start();
                        if(totalTime > 5000){
    	                	Timer mTimer = new Timer();
    	                	mTimer.schedule(new TimerTask(){
    	                		@Override
    	                		public void run(){
    	   							WriteToFile(" YES");
        							current++;
        							if(current >= XMLFileBeans.size()){
        								//textView.setText("测试完成");
        								mPlayer.stop();
        								WriteToFile("测试完成");
        								return;	
        							} else{
        								fileNextHandler.sendEmptyMessage(0x01);
        							}
        							
    	                		}
    	                	}, 5000);
//    						try {
//    							Thread.sleep(20000);
//    							WriteToFile(" YES");
//    							current++;
//    							if(current >= XMLFileBeans.size()){
//    								textView.setText("测试完成");
//    								mPlayer.stop();
//    								WriteToFile("测试完成");
//    								return;	
//    							} else{
////    								 if(mPlayer.isPlaying()){
//    									 fileNextHandler.sendEmptyMessage(0x01);
// //   									 }
//    								 }
//						    } catch (InterruptedException e) {
//							   e.printStackTrace();
//						    }
                        	
                        }
//						try {
//							Thread.sleep(totalTime/8);
//							mPlayer.seekTo(6*totalTime/8);
//							Log.d("yang", "" + 6*totalTime/8);
//							mPlayer.start();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//						try {
//							Thread.sleep(totalTime/4);
//							mPlayer.seekTo(2*totalTime/4);
//							Log.d("yang", "" + 2*totalTime/4);
//							mPlayer.start();
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						
					}else{
						WriteToFile(" 此播放文件不存在");
						Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+",此播放文件不存在", Toast.LENGTH_LONG).show();
						current++;
						if(current >= XMLFileBeans.size()){
							mPlayer.stop();
							return;	
						}				
//						 if(mPlayer.isPlaying()){
						 fileNextHandler.sendEmptyMessage(0x01);
 //                      }
						Log.d("awy", "sendEmptyMessage");
					}
		    	
			    }else{
			
			    	Toast.makeText(getActivity(),"请加载xml文件", Toast.LENGTH_LONG).show();
			    }

		}  catch (IOException e) {
			
			Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+" 播放失败", Toast.LENGTH_LONG).show();
			WriteToFile(" NO");
			current++;
			if(current >= XMLFileBeans.size()){
				mPlayer.stop();
				//textView.setText("测试完成");
				WriteToFile(" 测试完成");
				return;	
			}
			
//			 if(mPlayer.isPlaying()){
			 fileNextHandler.sendEmptyMessage(0x01);
//           }
			e.printStackTrace();
		}		
		
	}
	

	public void LoadData(){
		
		String xmlPath = FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getXMLFile_DIR_NAME()+File.separator + FileManager.getInstance().getXML_Name();
		File xmlFile = new File(xmlPath); 
		if(xmlFile.exists()){
			Log.d("awy", "cunzai wenjian"+"||");
			try {
				is = new FileInputStream(xmlPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			XMLMap = XMLPullAudioAndVideoFiles.parseSceneXML(is);

			XMLFileBeans = XMLMap.get("COMP_TYPE_AUDIO");		
			for(XMLFileBean xmlBeans : XMLFileBeans){
				Log.v("wy", xmlBeans.getFileName()+"||"); 
			    }	
			list.addAll(XMLFileBeans);
			mAdapter.notifyDataSetChanged();
		}else{
			Toast.makeText(getActivity(), "请加载xml文件", Toast.LENGTH_SHORT).show();
		}	
	}
	
	public void WriteToFile(String passOrfail){
		
		if(fileRW != null && !fileRW.isFileClosed()){
			Log.d("yang", "记录文件");
			if(passOrfail.trim().equalsIgnoreCase("测试开始") || passOrfail.trim().equalsIgnoreCase("测试完成")){
				fileRW.writeFile(passOrfail);
			}else{	
				if(current >= XMLFileBeans.size()){
					return;	
				}else{
					fileRW.writeFile(XMLFileBeans.get(current).getFileName()+"," + passOrfail);
				}
				
			}
			
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
							+ FileManager.getTestResult_DIR_NAME();
					//没有文件夹时,先创建文件夹
					Log.d("yang", fileDir);
					File dir = new File(fileDir);
					if (!dir.exists()) {
						dir.mkdirs();
					}
					File outfile = fileRW
							.createFile(
									getActivity(),
									fileDir,
									"audio" + fileRW.getTimeFileName(t)+FileManager.TestResult_FILE_SUFFIX,
									true);
					if(passOrfail.trim().equalsIgnoreCase("测试开始") || passOrfail.trim().equalsIgnoreCase("测试完成")){
						fileRW.writeFile(passOrfail);
					}else{	
						if(XMLFileBeans != null){
							fileRW.writeFile(XMLFileBeans.get(current).getFileName()+"," + passOrfail);
						}
						
					}
				}
			}
			
		}
		
	}


}
