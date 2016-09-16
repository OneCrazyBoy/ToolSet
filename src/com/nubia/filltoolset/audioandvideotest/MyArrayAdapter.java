package com.nubia.filltoolset.audioandvideotest;

import java.util.ArrayList;

import com.nubia.filltoolset.R;
import com.nubia.filltoolset.utils.xml.XMLFileBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyArrayAdapter extends BaseAdapter  {
	
	private static final String TAG = "MyArrayAdapter";
	private Context mContext;	
	private int id;
	private ArrayList<XMLFileBean> list;
	
    public MyArrayAdapter(Context context, int resource, ArrayList<XMLFileBean> list) {
		this.mContext = context;
	    this.id = resource;
		this.list =list;
	
	}
    

	@Override  
	public  View getView(int position, View convertView, ViewGroup parent) {  
		
		ViewHolder viewHolder = null;
		XMLFileBean XMLinfo = list.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(id, null);
		viewHolder = new ViewHolder(); 
		viewHolder.mTextView01 = (TextView) convertView.findViewById(R.id.tv_tips01);
		viewHolder.mTextView02 = (TextView) convertView.findViewById(R.id.tv_tips02);
		convertView.setTag(viewHolder);
	} else {
		viewHolder = (ViewHolder) convertView.getTag();
	}
			viewHolder.mTextView01.setText(XMLinfo.getFileName());
			viewHolder.mTextView02.setText(XMLinfo.getFileSort());	
			viewHolder.mTextView01.setTextColor(android.graphics.Color.BLACK);
			viewHolder.mTextView02.setTextColor(android.graphics.Color.BLACK);
		return convertView;  
	 }  
	
	    @Override  
	    public int getCount() {  
	       return list.size();
	    }  
	  
	    @Override  
	    public Object getItem(int position) {  
	        return list.get(position - 1);  
    
	   }  
	  
	    @Override  
	    public long getItemId(int position) {  
	        return position;  
	    }
	    
	 class ViewHolder {
		public TextView mTextView02;
		public TextView mTextView01;
	}
}
