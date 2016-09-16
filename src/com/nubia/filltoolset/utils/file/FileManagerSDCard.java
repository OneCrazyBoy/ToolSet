package com.nubia.filltoolset.utils.file;

import android.os.Environment;
public class FileManagerSDCard {
	
	public static FileManagerSDCard manager = new FileManagerSDCard();

	private FileManagerSDCard() {
	}

	public static FileManagerSDCard getInstance() {
		return manager;
	}
	
	public static String getSDCardRootPath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}
	

	/**
	 * 简单快速判断文件是否相同,可能会有冲突
	 * 
	 * @param fileName
	 *            文件名
	 * @param fileLength
	 *            文件的长度
	 * @return hash值
	 */
	public static int hashCode(String fileName, long fileLength) {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fileLength ^ (fileLength >>> 32));
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		return result;
	}


}
