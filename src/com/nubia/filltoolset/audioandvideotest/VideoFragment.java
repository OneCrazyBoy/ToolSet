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

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoFragment extends Fragment implements OnClickListener,OnItemClickListener{
	View rootView = null;
	SurfaceView surfaceView;
	Button play,stop;
	MediaPlayer mPlayer;
	int position = 0;
	VideoView videoView;
	MediaController mController;
	int current = 0;
	int totalTime = 0;
	boolean isclick = false;
	ListView listview;
	TextView textView;
	InputStream is;
	//文件操作
	private FileRW fileRW = FileRW.getInstance(getActivity());
	public MyArrayAdapter mAdapter;
	XMLSort xmlSort = null ;
	List<XMLFileBean> XMLFileBeans = null;
	private ArrayList<XMLFileBean> list = new ArrayList<XMLFileBean>();
	Map<String,List<XMLFileBean>> XMLMap = new HashMap<String,List<XMLFileBean>>();
	
	public  Handler fileNextHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0x01)
			{			
			    Log.d("awy", "handlerlimian");		
	            play();	 
			}					
		}
	
	};
	
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
	 {
		  rootView = inflater.inflate(R.layout.videofragment, container,false);
		  listview = (ListView)rootView.findViewById(R.id.list02_view);
		  mAdapter = new MyArrayAdapter(getActivity(), R.layout.list_item,list);
		  listview.setAdapter(mAdapter);
		  listview.setOnItemClickListener(this);
		  textView = (TextView)rootView.findViewById(R.id.showingtv1);
		  play = (Button)rootView.findViewById(R.id.btn_test);
//		  pause = (ImageButton)rootView.findViewById(R.id.pause);
		  stop = (Button)rootView.findViewById(R.id.btn_stop);
		  play.setOnClickListener(this);
//		  pause.setOnClickListener(this);
		  stop.setOnClickListener(this);	
		  mPlayer = new MediaPlayer();
//		  videoView = (VideoView) rootView.findViewById(R.id.surfaceView);
		  surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
		  surfaceView.getHolder().setKeepScreenOn(true);
		  surfaceView.getHolder().addCallback(new SurfaceListener());
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
//					if(!isclick){
					if(XMLFileBeans != null && XMLFileBeans.size() > 0){
						isclick = false;
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

						
//					}
				}
			});
			
//		  mController = new MediaController(getActivity());
//		  File video = new File(FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getAndioAndVideoFiles_DIR_NAME() + File.separator + XMLFileBeans.get(current).getFileName());
//		  if(video.exists()){
//			  videoView.setVideoPath(video.getAbsolutePath());
//			  videoView.setMediaController(mController);
//			  mController.setMediaPlayer(videoView);
//			  videoView.requestFocus();
//		  }else{
//			  Toast.makeText(getActivity(), "此播放文件不存在", Toast.LENGTH_SHORT).show(); 
//		  }
		  
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
	
	@Override
	public void onClick(View source)
	{
		try
		{
			switch (source.getId())
			{
			
				case R.id.btn_test:
					//play.setClickable(isHidden());
					//stop.setClickable(isVisible());
					play();
					break;
//				case R.id.pause:
//					if (mPlayer.isPlaying())
//					{
//						mPlayer.pause();
//					}
//					else
//					{
//						mPlayer.start();
//					}
//					break;
				case R.id.btn_stop:
					//stop.setClickable(isHidden());
					//play.setClickable(isVisible());
					current = 0 ;
					if(mPlayer != null){
						mPlayer.stop();
					}
					
					break;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void play() 
	{
		int mPos = 0;
		try {	
			if(XMLFileBeans != null && !XMLFileBeans.isEmpty()){
				if(current >= XMLFileBeans.size()){
					mPlayer.stop();
					WriteToFile("测试完成");
					return;	
				}
				else if(FileManager.isFileExists(XMLFileBeans.get(current).getFileName())){
					mPlayer.reset();
					mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mPlayer.setDataSource(FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getAndioAndVideoFiles_DIR_NAME() + File.separator + XMLFileBeans.get(current).getFileName());
					mPlayer.setDisplay(surfaceView.getHolder());  
					mPlayer.prepare();
					totalTime = mPlayer.getDuration();
//					WindowManager wManager = getActivity().getWindowManager();
//					DisplayMetrics metrics = new DisplayMetrics();
//					wManager.getDefaultDisplay().getMetrics(metrics);
//					surfaceView.setLayoutParams(new LayoutParams(metrics.widthPixels
//						, mPlayer.getVideoHeight() * metrics.widthPixels/ mPlayer.getVideoWidth()));
					//mPlayer.seekTo(mPos);
					mPlayer.start();
					 if(totalTime > 5000){
	    	                	Timer mTimer = new Timer();
	    	                	mTimer.schedule(new TimerTask(){
	    	                		@Override
	    	                		public void run(){
	    	                			Looper.prepare();
//	    	                			
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
	        							Looper.loop();
	    	                		}
	    	                	}, 5000);              	
                     }else{                      	
                     	//WriteToFile(" YES");
//							 if(mPlayer.isPlaying()){
							// fileNextHandler.sendEmptyMessage(0x01);
//                           }
                     }
				}else{
					WriteToFile(" 此播放文件不存在");
					Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+",此播放文件不存在", Toast.LENGTH_LONG).show();
					current++;
					if(current >= XMLFileBeans.size()){
						//textView.setText("测试完成");
						return;	
					}				
					fileNextHandler.sendEmptyMessage(0x01);
					Log.d("awy", "sendEmptyMessage");
				}
				
				}else{
					
			    	Toast.makeText(getActivity(),"请加载xml文件", Toast.LENGTH_LONG).show();
			    }
		
		} catch (IOException e) {
			
			Toast.makeText(getActivity(),XMLFileBeans.get(current).getFileName()+",播放异常", Toast.LENGTH_LONG).show();
			if(fileRW != null && !fileRW.isFileClosed()){
				Log.d("yang", "记录文件");
				fileRW.writeFile(XMLFileBeans.get(current).getFileName()+",播放异常");
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
										"video " + fileRW.getTimeFileName(t)+FileManager.TestResult_FILE_SUFFIX,
										true);
						fileRW.writeFile(XMLFileBeans.get(current).getFileName()+",播放异常");
					}
				}
				
			}
			current++;
			if(current >= XMLFileBeans.size()){
				//textView.setText("测试完成");
				return;	
			}
			
			fileNextHandler.sendEmptyMessage(0x01);
			e.printStackTrace(); 	
		}
	}

	private class SurfaceListener implements SurfaceHolder.Callback
	{
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format,
			int width, int height)
		{
		}

		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			if (position > 0)
			{
				try
				{
					play();
					mPlayer.seekTo(position);
					position = 0;
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
		}
	}

	@Override
	public void onPause()
	{
		if (mPlayer.isPlaying())
		{
			position = mPlayer.getCurrentPosition();
			mPlayer.stop();
		}
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		if (mPlayer.isPlaying()) mPlayer.stop();
		mPlayer.release();
		super.onDestroy();
	}
	
	public void LoadData(){
		
		String xmlPath = FileManager.getInstance().getRootPath() + File.separator + FileManager.getInstance().getROOT_DIR() + File.separator + FileManager.getInstance().getXMLFile_DIR_NAME()+File.separator + FileManager.getInstance().getXML_Name();
		Log.d("wy", xmlPath);
		File xmlFile = new File(xmlPath); 
		if(xmlFile.exists()){
			Log.v("ao", "cunzai wenjian"+"||");
			try {
				is = new FileInputStream(xmlPath);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
			XMLMap = XMLPullAudioAndVideoFiles.parseSceneXML(is);
			XMLFileBeans = XMLMap.get("COMP_TYPE_VIDEO");	  	
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
						fileRW.writeFile(XMLFileBeans.get(current).getFileName()+"," + passOrfail);
					}
				}
			}
			
		}
		
	}



}	
