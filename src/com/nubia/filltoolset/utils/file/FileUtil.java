package com.nubia.filltoolset.utils.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 管理文件的拷贝、打开、写入等基本操作
 * 
 * @author wuyang.ao
 * 
 */
public class FileUtil {

	/**
	 * 将src文件或文件夹下的所有文件和目录拷贝至des目录下
	 * @param src	源文件或文件夹的绝对路径
	 * @param des	目的文件夹绝对路径
	 */
	public static void copy(String src, String des) {
		if (src == null || des == null) {
			return;
		}
		File srcDir = new File(src);
		File[] listFile = srcDir.listFiles();
		File desDir = new File(des);
		boolean dirExist = false;
		if (!desDir.exists()) {
			dirExist = desDir.mkdirs();
		}
		if (dirExist) {
			if (listFile == null) {
				if (srcDir != null) {
					//src是文件,拷贝至des目录下
					copyFile(src, des+File.separator+srcDir.getName());
				}
			}else {
				//src是文件夹
				for (File f : listFile) {
					if (f.isFile()) {
						copyFile(f.getPath(), des + File.separator + f.getName()); // 调用文件拷贝的方法
					} else if (f.isDirectory()) {
						copy(f.getPath(), des + File.separator + f.getName());
					}
				}
			}
		}
	}

	/**
	 * 将文件从src拷贝到des
	 * @param src	源文件绝对路径
	 * @param des	目的文件绝对路径
	 */
	private static void copyFile(String src, String des) {
		if (src == null || des == null) {
			return ;
		}
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			ps = new PrintStream(new FileOutputStream(des));
			String s = null;
			while ((s = br.readLine()) != null) {
				ps.println(s);
				ps.flush();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
				{
					br.close();
					br = null;
				}
				if (ps != null)
				{
					ps.close();
					ps = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 目标文件不存在则创建,存在则在末尾进行追加
	 * @param src	需拷贝的源文件
	 * @param des	拷贝至的目的文件
	 */
	public static void copyFile(File src, File des) {
		if (src == null || des == null) {
			return ;
		}
		BufferedReader br = null;
		PrintStream ps = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(src)));
			ps = new PrintStream(new FileOutputStream(des,true));
			String s = null;
			while ((s = br.readLine()) != null) {
				ps.println(s);
				ps.flush();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
				{
					br.close();
					br = null;
				}
				if (ps != null)
				{
					ps.close();
					ps = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 删除路径下的所有文件和文件夹
	 * 
	 * @param absoluteFilePath
	 *            文件或文件夹的绝对路径
	 * @return true为删除成功，否则为false
	 */
	public static boolean deleteFile(String absoluteFilePath) {
		File mWorkingPath = new File(absoluteFilePath);
		if (!mWorkingPath.exists()) {
			return false;
		} else {
			boolean deleted = false;
			String[] files = mWorkingPath.list();
			for (int i = 0; i < files.length; i++) {
				File file = new File(absoluteFilePath + File.separator + files[i]);
				if (file.isDirectory()) {
					// 先删除目录内的文件夹和文件
					deleteFile(absoluteFilePath + File.separator + files[i]);
					// 然后再删除当前目录
					deleted = file.delete();
				} else {
					// 如果是文件则直接删除文件
					deleted = file.delete();
				}
			}
			return deleted;
		}
	}
	
	/**
	 * 将directory目录下的所有文件拷贝至fileCopyTo中
	 * @param directory	存放需要拷贝文件的文件夹
	 * @param fileCopyTo	接收拷贝的文件
	 * @return	拷贝是否成功
	 */
	public static boolean copyFiles(File directory,File fileCopyTo)
	{
		if (directory == null || fileCopyTo == null || fileCopyTo.isDirectory()) {
			return false;
		}
		//判断directory
		if (directory.isFile()) {//如果是文件的话，直接做文件拷贝
			copyFile(directory, fileCopyTo);
		}else {
			File[] files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						copyFile(file, fileCopyTo);
					}
				}
			}
		}
		return true;
	}

}
