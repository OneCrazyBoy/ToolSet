package com.nubia.filltoolset.utils;

import com.nubia.filltoolset.R;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

public class ShowDialogUtil {
	
	private static final double ratio = 0.85;
	
	/**
	 * 选择对话框。
	 * @param context	上下文。
	 * @param title		标题。
	 * @param msg	具体内容。如果不需要此项，则输入 null！
	 * @param yes	确认。
	 * @param no	取消。如果不需要此项，则输入 null！
	 * @param dialogAction 处理方法。
	 */
	public static void showDialog(Context context, String title, String msg,String yes,String no,
			final DialogAction dialogAction) {

		LayoutInflater inflater = LayoutInflater.from(context);
		final AlertDialog dialog = new AlertDialog.Builder(context).create();
		View convertView = inflater.inflate(R.layout.alert_dialog, null);
		TextView titleView = (TextView) convertView.findViewById(R.id.title);
		TextView msgView = (TextView) convertView.findViewById(R.id.msg);
		TextView yesTv = (TextView) convertView.findViewById(R.id.yes);
		TextView noTv = (TextView) convertView.findViewById(R.id.no);

		titleView.setText(title);
		if(msg == "" || msg == null) {	// 没有具体内容就隐藏。
			msgView.setVisibility(TextView.GONE);
		} else {
			msgView.setText(msg);
		}
		yesTv.setText(yes);
		if(no == "" || no == null) {
			noTv.setVisibility(TextView.GONE);
		} else {
			noTv.setText(no);
		}

		// dialog.setView(convertView,0,0,0,0);
		dialog.show();
		
		dialog.setContentView(convertView);
		int maxWid = dip2px(context, 300);
		int wid = (int) (context.getResources().getDisplayMetrics().widthPixels * ratio);
		if (wid > maxWid) {
			wid = maxWid;
		}

		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = wid ;
		params.height = LayoutParams.WRAP_CONTENT;
		dialog.getWindow().setAttributes(params);


		yesTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				dialogAction.positiveAction();
			}
		});

		noTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				dialogAction.negativeAction();

			}
		});

	}
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
