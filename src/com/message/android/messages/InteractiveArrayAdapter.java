package com.message.android.messages;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InteractiveArrayAdapter extends ArrayAdapter<Model> {

	private final List<Model> list;
	private final Activity context;
	private Boolean checkAll = false;

	public InteractiveArrayAdapter(Activity context, List<Model> list) {
		super(context, R.layout.checkboxlayout, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder {
		protected TextView text;
		protected TextView labeltext;
		protected CheckBox checkbox;
		protected ImageView imageView;

	}

	public int getCount() {
		return this.list.size();
	}

	public void setCheckAll(Boolean b) {
		this.checkAll = b;
	}

	public boolean isCheckAll() {
		return this.checkAll;
	}

	public String getChecked() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("(0,");
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).isSelected()) {
				buffer.append(list.get(i).getTheId() + ",");
			}
			buffer.append("0)");
		}
		return buffer.toString();
	}

	public int DeleteMesg() {
		int count = 0;
		try {
			StringBuffer buffer = new StringBuffer();
			buffer.append(" (0,");
			for (Model object : list) {
				if (object.isSelected()) {
					buffer.append(object.getTheId() + ",");
				}
			}
			buffer.deleteCharAt(buffer.length() - 1);
			buffer.append(")");

			Uri deleteUri = Uri.parse("content://sms");
			count = context.getContentResolver().delete((deleteUri),
					"_id in " + buffer.toString(), null);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if (convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(R.layout.checkboxlayout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.text = (TextView) view.findViewById(R.id.label);
			viewHolder.labeltext = (TextView) view.findViewById(R.id.labelname);
			viewHolder.imageView = (ImageView) view.findViewById(R.id.personimg);

			try {
				viewHolder.text.setOnClickListener(new View.OnClickListener() {

					public void onClick(View v) {
						AlertDialog.Builder alertbox = new AlertDialog.Builder(context);				
						 alertbox.setMessage( viewHolder.text.getText());
						 alertbox.setTitle("Message From :- "+viewHolder.labeltext.getText());
						 alertbox.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
							 public void onClick(DialogInterface dialog, int which) {
							 
							    } 
				            });
						 alertbox.show();
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}

			viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);
			viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							Model element = (Model) viewHolder.checkbox
									.getTag();
							element.setSelected(buttonView.isChecked());

						}
					});
			view.setTag(viewHolder);
			viewHolder.checkbox.setTag(list.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.text.setText(list.get(position).getName());
		holder.labeltext.setText(list.get(position).getLabelName());
		holder.imageView.setImageBitmap(list.get(position).getBitmap());
		if (checkAll) {
			holder.checkbox.setChecked(true);
		} else {
			holder.checkbox.setChecked(false);
		}
		return view;
	}
}
