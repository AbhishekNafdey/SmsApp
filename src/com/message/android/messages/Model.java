package com.message.android.messages;

import android.graphics.Bitmap;

public class Model {
	private String name;
	private String labelname;
	private boolean selected;
	private String theId;
	Bitmap bitmap;

	public Model(String name,String labelname,String theId,Bitmap b) {
		this.name = name;
		selected = false;
		this.labelname=labelname;
		this.theId=theId;
		this.bitmap=b;
	}

	public String getName() {
		return name;
	}
	public String getLabelName() {
		return labelname;
	}
	public String getTheId() {
		return theId;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setTheId(String theId) {
		this.theId = theId;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setBitmap(Bitmap b) {
		this.bitmap = b;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
