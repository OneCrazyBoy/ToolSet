package com.nubia.filltoolset.utils.file;

import java.io.File;

import android.os.Environment;
import android.util.Log;

public class FileManager {
	
	//各种指定文件所在的子目录
	public static String TestResult_DIR_NAME = "TestResult";
	public static String AndioAndVideoFiles_DIR_NAME = "AudioAndVideoFiles";
	public static String XML_Name = "files.xml";
	public static String XMLFile_DIR_NAME = "XMLFile";
	public static String ROOT_DIR = "TestData";
	//Android SD card dir
	public static String SDCARD_PATH = "/mnt/sdcard/";
	public static String TestResult_FILE_SUFFIX = ".txt";
	public static FileManager manager = new FileManager();
	
	private FileManager() {
	}
	
	public static FileManager getInstance()
	{
		return manager;
	}
	
	/**
	 * 
	 * @param ROOT_DIR	存储器根目录
	 * @param AndioAndVideoFiles_DIR_NAME  音视频文件目录
	 * @param XMLFile_DIR_NAME	XML文件目录
	 * @param TestResult_DIR_NAME	测试结果目录
	 */
	public FileManager(String ROOT_DIR, String AndioAndVideoFiles_DIR_NAME, String XMLFile_DIR_NAME, String TestResult_DIR_NAME,String XML_Name ) {
		super();
		this.ROOT_DIR = ROOT_DIR;
		this.AndioAndVideoFiles_DIR_NAME = AndioAndVideoFiles_DIR_NAME;
		this.XMLFile_DIR_NAME = XMLFile_DIR_NAME;
		this.TestResult_DIR_NAME = TestResult_DIR_NAME;
		this.XML_Name = XML_Name;
	}

	public static  String getAndioAndVideoFiles_DIR_NAME() {
		return AndioAndVideoFiles_DIR_NAME;
	}
	public void setAndioAndVideoFiles_DIR_NAME(String AndioAndVideoFiles_DIR_NAME) {
		this.AndioAndVideoFiles_DIR_NAME = AndioAndVideoFiles_DIR_NAME;
	}
	public static String getTestResult_DIR_NAME() {
		return TestResult_DIR_NAME;
	}
	public void setTestResult_DIR_NAME(String TestResult_DIR_NAME) {
		this.TestResult_DIR_NAME = TestResult_DIR_NAME;
	}
	public static String getXMLFile_DIR_NAME() {
		return XMLFile_DIR_NAME;
	}
	public void setXMLFile_DIR_NAME(String XMLFile_DIR_NAME) {
		this.XMLFile_DIR_NAME = XMLFile_DIR_NAME;
	}

	public static String getROOT_DIR() {
		return ROOT_DIR;
	}
	public void setROOT_DIR(String ROOT_DIR) {
		this.ROOT_DIR = ROOT_DIR;
	}
	public static String getXML_Name() {
		return XML_Name;
	}
	public void setXML_Name(String XML_Name) {
		this.XML_Name = XML_Name;
	}
	public static String getSDCardRootPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	
	/**
	 * 获取系统的根路径
	 * @return
	 */
	public static String getRootPath()
	{
		String rootPath = null;
		String sDCardState = Environment.getExternalStorageState();
		if (sDCardState.equals(Environment.MEDIA_MOUNTED)) {
			File sdFile = Environment.getExternalStorageDirectory();
			rootPath = sdFile.getAbsolutePath();
			Log.d("awy", "sDCard");
		}else{
			rootPath = Environment.getDataDirectory().getAbsolutePath();
			Log.d("awy", "数据卡");
		}
		return rootPath;
	}
	
	public static boolean isFileExists(String str){
		File file = new File(getRootPath() + File.separator + getROOT_DIR()  + File.separator + str);
		if (!file.exists()) {
			return false;
		}else{
			return true;
		}	
	}
	
}
