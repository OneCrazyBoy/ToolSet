package com.nubia.filltoolset.utils.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class FileRW {

	//读文件的reader
	private FileReader fr;
	private BufferedReader br;
	//写文件的Writer
	private FileWriter fw;
	private BufferedWriter bWriter;
	//Time类对象
	private Context context;

	private FileRW() {
	}
	
	private String TAG = "FileRW";

	private FileRW(Context context) {
		this.context = context;
	}
	
	/**
	 * 创建文件读写类对象
	 * @param ctx  创建对象的上下文
	 * @return	FileRW文件读写对象
	 */
	public static FileRW getInstance(Context ctx)
	{
		return new FileRW(ctx);
	}

	/**
	 * 打开需要读取的文件
	 * @param str	需要打开的文件名，可以包含相对路径
	 * @return true表示打开文件成功,反之为打开文件失败
	 */
	public BufferedReader openReadFile(String str) {

		String sDCardState = Environment.getExternalStorageState();
		if (sDCardState.equals(Environment.MEDIA_MOUNTED)) {
			File sdFile = Environment.getExternalStorageDirectory();
			File readFile = new File(sdFile.getAbsoluteFile() + File.separator
					+ str);
			if (!readFile.exists()) {
//				Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
				return null;
			}

			// FileWriter fw;
			try {
				fr = new FileReader(readFile);
				br = new BufferedReader(fr);
//				Toast.makeText(context, "新建reader成功", Toast.LENGTH_SHORT)
//						.show();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}

		}
		return br;
	}

	/**
	 * 停止读文件，关闭相应的reader
	 */
	public void stopReadFile() {

		try {
			if (br != null) {
				br.close();
				br = null;
			}
			if (fr != null) {
				fr.close();
				fr = null;
			}
//			Toast.makeText(context, "停止读文件", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			Log.d(TAG, e.toString());
		}
	}
	
	/**
	 * 创建以当前时间为名字的文件 
	 * @param t	Time类对象
	 * @return	表示文件名的字符串
	 */
	public String getTimeFileName(Time t)
	{
		StringBuilder sbBuilder = new StringBuilder(30);
		
		sbBuilder.append(String.valueOf(t.year)).append("-")
				.append(String.valueOf(t.month + 1)).append("-")	
				.append(String.valueOf(t.monthDay)).append("-")
				.append(String.valueOf(t.hour)).append("-")
				.append(String.valueOf(t.minute)).append("-")
				.append(String.valueOf(t.second));
		return sbBuilder.toString();
	}

	/**
	 * 创建要写入内容的文件,如果已存在直接打开,在写文件完毕后要调用stopWriteFile(),否则写入的内容
	 * 无法保存到文件
	 * @param context	创建文件的上下文
	 * @param dir 文件所在的绝对路径
	 * @param fileName  要写入内容文件的文件名
	 * @param writeOrAppend true为追加至文件末尾,反之为覆盖文件
	 * @return true表示创建文件成功,反之为创建文件失败
	 */
	public File createFile(Context context, String dir, String fileName,boolean writeOrAppend) {
		if (fileName == null || fileName.equalsIgnoreCase("")) {
			return null;
		}
		File outFile = null;
		File directory;
		if (dir != null && !dir.equals("")) {
			directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			outFile = new File(directory, fileName);
		}else {
			return null;
		}
		if (outFile != null && !outFile.exists()) {
			try {
				outFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			fw = new FileWriter(outFile, writeOrAppend);// 第2个参数false为覆盖，true为追加
			bWriter = new BufferedWriter(fw);
			if (context != null) {
				//Toast.makeText(context, "新建writer成功", Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return outFile;
	}

	
	/**
	 * 停止写文件，关闭相应的writer
	 */
	public void stopWriteFile() {

		try {
			if (bWriter != null) {
				bWriter.close();
				bWriter = null;
			}
			if (fw != null) {
				fw.close();
				fw = null;
				if (context != null) {
					Toast.makeText(context, "文件关闭", Toast.LENGTH_SHORT).show();
				}
			}
		} catch (IOException e) {
			Log.d(TAG, e.toString());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将字符串写入文件
	 * @param str	需要写入的字符串
	 */
	public void writeFile(String str) {
		try {
			if (bWriter != null) {
				bWriter.write(str);
				bWriter.newLine();
				bWriter.flush(); //每写入一段都将缓充中的数据写入文件
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isFileClosed()
	{
		return fw == null || bWriter == null;
	}
	
}