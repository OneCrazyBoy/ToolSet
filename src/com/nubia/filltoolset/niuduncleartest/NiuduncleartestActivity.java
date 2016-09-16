package com.nubia.filltoolset.niuduncleartest;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import java.io.File;  
import java.util.ArrayList;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.file.FileManager;
import com.nubia.filltoolset.utils.file.FileRW;

import android.app.AlertDialog;    
import android.content.DialogInterface;    
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;  
import android.view.View.OnClickListener;
import android.widget.AdapterView;  
import android.widget.Button;
import android.widget.ListView;  
import android.widget.TextView;  
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class NiuduncleartestActivity extends Activity{// implements OnItemClickListener{
	
    private ArrayList<String> items = new ArrayList<String>();//存放名称  
    private ArrayList<String> paths = new ArrayList<String>();//存放路径  
    private ArrayList<String> items01 = new ArrayList<String>();//存放名称  
    private ArrayList<String> paths01 = new ArrayList<String>();//存放路径 
    
    private ArrayList<String> fileNames = new ArrayList<String>();//存放wenjian
    private ArrayList<String> fileNames01 = new ArrayList<String>();//存放wenjian
    private ArrayList<String> folderNamesSDCard0 = new ArrayList<String>();
    private ArrayList<String> folderNamesSDCard1 = new ArrayList<String>();
    private ArrayList<String> currentFiles = new ArrayList<String>(); //所有文件 currentFolders
    private ArrayList<String> currentFolders = new ArrayList<String>(); 
    private ArrayList<String> outFiles = new ArrayList<String>();
    private ArrayList<String> outfolders = new ArrayList<String>();
    private String rootPath = null;  
    private String rootPath01 = null;  
    String path = null;
    private TextView tv;  
    private Button tv02;
    private Button button;
    private Button btn_result;
    private Button btn_file;
    private Button btn_findallfiles;
	private long waitTime = 2000;
	private long touchTime = 0;
   
    ListView listview;
    ListView listview01;
	//文件操作
	private FileRW fileRW = FileRW.getInstance(this);
	private FileRW fileRW01 = FileRW.getInstance(this);
    
	public  Handler updateUIHandle = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			if(msg.what == 0x01)
			{
				if(msg.obj != null){
					//button.setText("清理后测试开始");
					traverFiles(rootPath,(String) msg.obj);
					traverFiles(rootPath01,(String) msg.obj);
					Log.d("ao", "button的内容 ： " + button.getText().toString().trim());
					if(button.getText().toString().trim().equals("请稍等 ...")){
						button.setText("清理后测试开始");
            			Message msg01 = updateUIHandle.obtainMessage(0x03);
            			String text = "初次记录文件夹和文件数:" + folderNamesSDCard0.size() + " , " + fileNames.size();
						msg01.obj = text;
						msg01.sendToTarget();
					}else if(button.getText().toString().trim().equals("请再稍等 ...")){
						button.setText("导出结果");
            			Message msg01 = updateUIHandle.obtainMessage(0x03);
            			String text = "再次记录文件夹和文件数：" + folderNamesSDCard1.size() + " , " + fileNames01.size();
						msg01.obj = text;
						msg01.sendToTarget();
					}			
					button.setClickable(true);
				}

			}else if(msg.what == 0x02){
				for(int s=0;s < folderNamesSDCard0.size();s++ ){
					outfolders.add(folderNamesSDCard0.get(s));
				}
				Log.d("ao", "folderNamesSDCard0.size()： " + folderNamesSDCard0.size() + ";" + folderNamesSDCard1.size());
				for(int m = 0;m < folderNamesSDCard0.size();m++){
					for(int n = 0;n < folderNamesSDCard1.size();n++){
						if(folderNamesSDCard0.get(m).trim().equals(folderNamesSDCard1.get(n).trim())){
							outfolders.remove(folderNamesSDCard0.get(m));
							folderNamesSDCard1.remove(folderNamesSDCard0.get(m));
							Log.d("ao", "folderNamesSDCard1.remove： " + outfolders.size() + ";" + folderNamesSDCard0.get(m));
						}
					}
				}
				for(int t = 0;t < fileNames.size();t++){
					outFiles.add(fileNames.get(t));
				}
				Log.d("ao", "fileNames.size()： " + fileNames.size() + ";" + fileNames01.size());
				for(int i = 0;i < fileNames.size();i++){
					for(int j = 0;j< fileNames01.size();j++){
						if(fileNames.get(i).trim().equals(fileNames01.get(j).trim())){
							outFiles.remove(fileNames.get(i));
							fileNames01.remove(fileNames.get(i));
							Log.d("ao", "outFiles.remove： " + outFiles.size() + ";" + fileNames.get(i));
						}
					}
				}
				WriteToFile("测试开始","清理测试");
				WriteToFile("一共清除了" + outfolders.size() + "个文件夹  , " + outFiles.size() + "个文件","清理测试");
				WriteToFile("清除的文件夹：","清理测试");
				for(int q = 0;q <outfolders.size();q++){
					WriteToFile((q+1) + ") " +outfolders.get(q),"清理测试");
					Log.d("ao", "删除的文件夹 ： " + outfolders.get(q));
				}				
				WriteToFile("清除的文件：","清理测试");
				for(int p = 0;p <outFiles.size();p++){
					WriteToFile((p+1) + ") " +outFiles.get(p),"清理测试");
					Log.d("ao", "删除的文件 ： " + outFiles.get(p));
				}	
    			Message msg01 = updateUIHandle.obtainMessage(0x03);
    			String text = " 已清除文件夹和文件数：   " + outfolders.size()+ " , " + outFiles.size();
				msg01.obj = text;
				msg01.sendToTarget();
				WriteToFile("测试结束","清理测试");
				button.setText("清理前测试开始");
				button.setClickable(true);
			}else if(msg.what == 0x03){
				if(msg.obj != null){
					Log.d("ao","msg.obj ");
					tv02.setText(msg.obj.toString());
				}
				
			}else if(msg.what == 0x04){
				Log.d("ao", "0x04");
				if(msg.obj != null){
					Log.d("ao", "!= null");
					traverFiles(rootPath,(String) msg.obj);
				    traverFiles(rootPath01,(String) msg.obj);
					WriteToFile("测试开始","记录所有文件夹");
					WriteToFile("一共有" + currentFolders.size() + "个文件夹  , " + currentFiles.size() + "个文件","记录所有文件夹");
					WriteToFile("所有文件夹：","记录所有文件夹");	
					for(int q = 0;q <currentFolders.size();q++){
						WriteToFile((q+1) + ") " +currentFolders.get(q),"记录所有文件夹");
						Log.d("ao", "文件夹 ： " + currentFolders.get(q));
					}	
					WriteToFile("所有文件：","记录所有文件夹");
					for(int p = 0;p <currentFiles.size();p++){
						WriteToFile((p+1) + ") " +currentFiles.get(p),"记录所有文件夹");
						Log.d("ao", "文件 ： " + currentFiles.get(p));
					}					
				    btn_file.setText("记录当前所有文件");
				    btn_file.setClickable(true);
				}		
			}else if(msg.what == 0x05){
				//Toast.makeText(this,"播放出错了", Toast.LENGTH_LONG).show();
			}
		}
	
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_niuduncleartest);
		 tv = (TextView)findViewById(R.id.TextView); 
		 tv.setText("注意事项：\r\n    1) 使用此APK进行测试前需要将其添加到牛盾白名单中，以免被清理  ;\r\n    2) 导出结果后的保存路径为 : /storage/sdcard0/TestData/ClearTestResult/*.txt ;\r\n    3) 支持手机内部存储卡和SD卡清理测试 。\r\n备注： 当文件较多时 , 运行稍慢 , 若测试过程中遇到任何问题 ，请IM 敖武阳 , 谢谢  !");
//		 tv01 = (TextView)findViewById(R.id.TextView01);
//		 listview = (ListView)findViewById(R.id.list_view);
//		 listview01 = (ListView)findViewById(R.id.list_view01);
		 button = (Button)findViewById(R.id.button_start);
		 tv02 = (Button)findViewById(R.id.text_display);
		 btn_findallfiles = (Button)findViewById(R.id.btn_findallfiles);
		 tv02.setText("执行显示");
//		 listview.setOnItemClickListener(this);
//		 listview01.setOnItemClickListener(this);
//		 mAdapter = new MyArrayAdapter(this, R.layout.list_item,items);
//		 mAdapter01 = new MyArrayAdapter(this, R.layout.list_item,items);
//		 listview.setAdapter(mAdapter);
//		 listview.setAdapter(mAdapter01);
		 //rootPath = FileManager.getInstance().getRootPath();// + File.separator + FileManager.getInstance().getROOT_DIR();// + File.separator + FileManager.getInstance().getXMLFile_DIR_NAME();//+File.separator + FileManager.getInstance().getXML_Name();
		 //rootPath = Environment.getDataDirectory().getAbsolutePath() ;
		 rootPath = "/storage/sdcard0";
		 //rootPath = "/mnt/data";
		 rootPath01 = "/storage/sdcard1";
		 Log.d("awy", rootPath);
//       getFileDir(rootPath);
//       getFileDir01(rootPath01);//获取rootPath目录下的文件
		 btn_result = (Button)findViewById(R.id.btn_result);
		 btn_file = (Button)findViewById(R.id.btn_file);
		 btn_findallfiles.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					
					String fileDir = rootPath
							+ File.separator
							+ FileManager.getROOT_DIR()
							+ File.separator
							+ "CurrentFiles";
					if(FileManager.isFileExists("CurrentFiles")){	
						try{
							String lastFile;
							File f = new File(fileDir);
							File[] files = f.listFiles();
							if(files != null){
								int count = files.length;
								if(count == 0){
									Toast.makeText(NiuduncleartestActivity.this,"没有文件啦", Toast.LENGTH_LONG).show();
								}else{																		
									lastFile = files[0].getPath();									
								    for(int i = 1;i < count ;i++){						    	
									    if(files[i].getName().compareTo(lastFile) > 0){									    	
										    lastFile = files[i].getPath();
									    }
								    }							    
								    File file = new File(lastFile);
							        Intent intent = new Intent("android.intent.action.VIEW");  
							        intent.addCategory("android.intent.category.DEFAULT"); 
							        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							        intent.setDataAndType(Uri.fromFile(file), "text/plain");
							        startActivity(intent);	
								}
							
							}
							
							
						}catch(Exception e){			
						}
					}else{
						Toast.makeText(NiuduncleartestActivity.this,"文件路径不存在啦", Toast.LENGTH_LONG).show();					
					}

				}			 
		 });		 
		 btn_result.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					
					String fileDir = rootPath
							+ File.separator
							+ FileManager.getROOT_DIR()
							+ File.separator
							+ "ClearTestResult";
					if(FileManager.isFileExists("ClearTestResult")){	
						try{
							String lastFile;
							File f = new File(fileDir);
							File[] files = f.listFiles();
							if(files != null){
								int count = files.length;
								if(count == 0){
									Toast.makeText(NiuduncleartestActivity.this,"没有文件啦", Toast.LENGTH_LONG).show();
								}else{																		
									lastFile = files[0].getPath();									
								    for(int i = 1;i < count ;i++){						    	
									    if(files[i].getName().compareTo(lastFile) > 0){									    	
										    lastFile = files[i].getPath();
									    }
								    }							    
								    File file = new File(lastFile);
							        Intent intent = new Intent("android.intent.action.VIEW");  
							        intent.addCategory("android.intent.category.DEFAULT"); 
							        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							        intent.setDataAndType(Uri.fromFile(file), "text/plain");
							        startActivity(intent);	
								}
							
							}
							
							
						}catch(Exception e){			
						}
					}else{
						Toast.makeText(NiuduncleartestActivity.this,"文件路径不存在啦", Toast.LENGTH_LONG).show();					
					}

				}			 
		 });
		 btn_file.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					btn_file.setClickable(false);
					btn_file.setText("稍等一下 ...");
					currentFiles.clear();
					currentFolders.clear();
					new Thread(new Runnable(){
						@Override
						public void run(){
							Message msg = updateUIHandle.obtainMessage(0x04);
							String fn = "currentFiles"; 
							msg.obj = fn;
							msg.sendToTarget();
							
						}
					}).start();
					
					
				}			 
		 });
		 
 		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(button.getText().toString().trim().equals("清理前测试开始")){
					button.setClickable(false);
					button.setText("请稍等 ...");
					fileNames.clear();	
					outFiles.clear();
					outfolders.clear();
					folderNamesSDCard0.clear();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message msg = updateUIHandle.obtainMessage(0x01);
							String fn = "fileNames"; 
							msg.obj = fn;
							msg.sendToTarget();
							
						}
					}).start();
				}else if(button.getText().toString().trim().equals("清理后测试开始")){
					button.setClickable(false);
					button.setText("请再稍等 ...");
					fileNames01.clear();	
					folderNamesSDCard1.clear();
					new Thread(new Runnable() {
						@Override
						public void run() {
							Message msg = updateUIHandle.obtainMessage(0x01);
							String fn = "fileNames01"; 
							msg.obj = fn;
							msg.sendToTarget();
							
						}
					}).start();
				}else if(button.getText().toString().trim().equals("导出结果")){
					button.setClickable(false);
					button.setText("导出结果稍等 ...");
					Message msg = updateUIHandle.obtainMessage(0x02);
					msg.sendToTarget();			
				}
			}
		});
	}
	
	public void traverFiles(String address,String fn){
		try{
			File f = new File(address); 
			File[] files = f.listFiles();
			// 将所有文件存入list中  
            if(files != null){  
                int count = files.length;// 文件个数  
                Log.d("awy", "文件个数" + count );
                for (int i = 0; i < count; i++) {
                	if(!files[i].isDirectory()){
                		File file = files[i]; 
                		if(fn.equals("fileNames")){
                			fileNames.add(file.getPath());
                			Log.d("ao", fileNames.size()+"  文件名:" + file.getPath());
                		}else if(fn.equals("fileNames01")){
                			fileNames01.add(file.getPath());               			
                			Log.d("ao", fileNames01.size()+"  文件名" + file.getPath());
                		}else if(fn.equals("currentFiles")){
                			currentFiles.add(file.getPath());
                			Log.d("ao", currentFiles.size()+"  文件名" + file.getPath());
                    		   			
                		}            		
                		//Toast.makeText(this,fileNames.size()+"", Toast.LENGTH_LONG).show();
                		
                	}else{ 
                		if(fn.equals("fileNames")){
                			folderNamesSDCard0.add(files[i].getPath());
                		}else if(fn.equals("fileNames01")){
                			folderNamesSDCard1.add(files[i].getPath());
                		}else if(fn.equals("currentFiles")){
                			currentFolders.add(files[i].getPath());
                		}
                		
                		//fileNames.add(files[i].getPath() + "/" + files[i].getName());
                    	traverFiles(files[i].getPath(),fn);
                    }
                }  
            }
			
			
		}catch(Exception e){
		}
		
	}

		
//	 public void getFileDir(String filePath) {  
//         try{  
//             this.tv.setText("当前路径:"+filePath);// 设置当前所在路径  
//             items = new ArrayList<String>();  
//             paths = new ArrayList<String>();  
//    		 mAdapter = new MyArrayAdapter(this, R.layout.list_item,items);
//    		 listview.setAdapter(mAdapter);
//             File f = new File(filePath); 
//             if(f.exists()){
//            	 Log.d("awy", "f cunzai");
//             }
//             Log.d("awy", "文件"  );
//             //items.add(f.getName());
//             File[] files = f.listFiles();// 列出所有文件  
//             Log.d("awy", "文件个数" + files.length );
//             // 如果不是根目录,则列出返回根目录和上一目录选项  
//             if (!filePath.equals(rootPath)) { 
//            	 Log.d("awy", "如果不是根目录");
//                 items.add("返回根目录");  
//                 paths.add(rootPath);  
//                 items.add("返回上一层目录");  
//                 paths.add(f.getParent());  
//             }  
//             // 将所有文件存入list中  
//             if(files != null){  
//                 int count = files.length;// 文件个数  
//                 Log.d("awy", "文件个数" + count );
//                 for (int i = 0; i < count; i++) {  
//                     File file = files[i];  
//                     items.add(file.getName());  
//                     paths.add(file.getPath());  
//                 }  
//             }  
//             mAdapter.notifyDataSetChanged();
//         }catch(Exception ex){  
//             ex.printStackTrace();  
//         }  
//	 }
//	 
//	 public void getFileDir01(String filePath) {  
//         try{  
//             this.tv01.setText("当前路径:"+filePath);// 设置当前所在路径  
//             items01 = new ArrayList<String>();  
//             paths01 = new ArrayList<String>();  
//    		 mAdapter01 = new MyArrayAdapter(this, R.layout.list_item01,items01);
//    		 listview01.setAdapter(mAdapter01);
//             File f = new File(filePath); 
//             if(f.exists()){
//            	 Log.d("awy", "f cunzai");
//             }
//             Log.d("awy", "文件"  );
//             //items.add(f.getName());
//             File[] files = f.listFiles();// 列出所有文件  
//             Log.d("awy", "文件个数" + files.length );
//             // 如果不是根目录,则列出返回根目录和上一目录选项  
//             if (!filePath.equals(rootPath01)) { 
//            	 Log.d("awy", "如果不是根目录");
//                 items01.add("返回根目录");  
//                 paths01.add(rootPath);  
//                 items01.add("返回上一层目录");  
//                 paths01.add(f.getParent());  
//             }  
//             // 将所有文件存入list中  
//             if(files != null){  
//                 int count = files.length;// 文件个数  
//                 Log.d("awy", "文件个数" + count );
//                 for (int i = 0; i < count; i++) {  
//                     File file = files[i];  
//                     items01.add(file.getName());  
//                     paths01.add(file.getPath());  
//                 }  
//             }  
//             mAdapter01.notifyDataSetChanged();
//         }catch(Exception ex){  
//             ex.printStackTrace();  
//         }  
//	 }
	//当用户单击某列表时激发该回调方法
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//			if(arg0.getId() == R.id.list_view){
//				path = paths.get(position);
//		         File file = new File(path);  
//		         //如果是文件夹就继续分解  
//		         if(file.isDirectory()){  
//		             this.getFileDir(path);  
//		         }else{  
//		             new AlertDialog.Builder(this).setTitle("提示").setMessage(file.getName()+" 是一个文件！").setPositiveButton("OK", new DialogInterface.OnClickListener(){  
//		   
//		                 public void onClick(DialogInterface dialog, int which) {  	                                           
//		                 }                     
//		             }).show();  
//		         } 
//			}else if(arg0.getId() == R.id.list_view01){
//				path = paths01.get(position);
//		         File file01 = new File(path);  
//		         //如果是文件夹就继续分解  
//		         if(file01.isDirectory()){  
//		             this.getFileDir01(path);  
//		         }else{  
//		             new AlertDialog.Builder(this).setTitle("提示").setMessage(file01.getName()+" 是一个文件！").setPositiveButton("OK", new DialogInterface.OnClickListener(){  
//		   
//		                 public void onClick(DialogInterface dialog, int which) {  
//		                                           
//		                 }  
//		                   
//		             }).show();  
//		         } 
//			}		
//		}
		
		public void WriteToFile(String passOrfail,String sort){
			if(sort.trim().equals("清理测试")){
				if(fileRW != null && !fileRW.isFileClosed()){
					fileRW.writeFile(passOrfail);			
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
									+ "ClearTestResult";
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
											"deletedFiles" + fileRW.getTimeFileName(t)+FileManager.TestResult_FILE_SUFFIX,
											true);
							fileRW.writeFile(passOrfail);
						}
					}
					
				}
				
			}else if(sort.trim().equals("记录所有文件夹")){

				if(fileRW01 != null && !fileRW01.isFileClosed()){
					fileRW01.writeFile(passOrfail);			
				}else{
					if(fileRW01 != null){
						Log.d("yang", "fileRW01 != null");
						Time t = new Time();
						t.setToNow();
						String rootPath = FileManager.getRootPath();
						if(rootPath != null){
							String fileDir = rootPath
									+ File.separator
									+ FileManager.getROOT_DIR()
									+ File.separator
									+ "CurrentFiles";
							//没有文件夹时,先创建文件夹
							Log.d("yang", fileDir);
							File dir = new File(fileDir);
							if (!dir.exists()) {
								dir.mkdirs();
							}
							File outfile = fileRW01
									.createFile(
											this,
											fileDir,
											"allFiles" + fileRW01.getTimeFileName(t)+FileManager.TestResult_FILE_SUFFIX,
											true);
							fileRW01.writeFile(passOrfail);
						}
					}
					
				}			
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

}
