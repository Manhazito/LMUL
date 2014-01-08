package org.feup.bondpoint;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class BaseListElement {

	private Drawable icon;
	private String text1;
	private String text2;
	private int requestCode;

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getText1() {
		return text1;
	}

	public void setText1(String text1) {
		this.text1 = text1;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public String getText2() {
		return text2;
	}

	public void setText2(String text2) {
		this.text2 = text2;
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	public BaseListElement(Drawable icon, String text1, String text2,
			int requestcode) {
		super();
		this.icon = icon;
		this.text1 = text1;
		this.text2 = text2;
		this.requestCode = requestcode;
	}

	private BaseAdapter adapter;

	public void setAdapter(BaseAdapter adapter) {
		this.adapter = adapter;
	}

	protected abstract View.OnClickListener getOnClickListener();

}
