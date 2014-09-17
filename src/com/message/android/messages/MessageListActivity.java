package com.message.android.messages;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MessageListActivity extends Activity {
	ArrayAdapter<Model> adapter;
	static EditText filterText;
	static String contactsToInclude = "";
	static String include;
	static String frmTbl;
	static String readSms;

	public void onCreate(Bundle welcomeAct) {
		super.onCreate(welcomeAct);

		setContentView(R.layout.main);
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
		filterText = (EditText) findViewById(R.id.findText);

		final Button buttonFind = (Button) findViewById(R.id.find);
		final Button buttonSelAll = (Button) findViewById(R.id.selectall);
		final Button buttonDel = (Button) findViewById(R.id.delete);

		buttonFind.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Find();
			}
		});

		buttonDel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				int count = ((InteractiveArrayAdapter) adapter).DeleteMesg();
				Toast toast = Toast.makeText(getApplicationContext(), count
						+ " mSMS deleted !", Toast.LENGTH_SHORT);
				Find();
				toast.show();
			}
		});

		buttonSelAll.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (!((InteractiveArrayAdapter) adapter).isCheckAll()) {
						buttonSelAll.setText("UnCheck All");
					} else {
						buttonSelAll.setText("Check All");
					}
					((InteractiveArrayAdapter) adapter)
							.setCheckAll(!((InteractiveArrayAdapter) adapter)
									.isCheckAll());
					adapter.notifyDataSetChanged();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			filterText.setText(extras.getString("filterTXT"));
			contactsToInclude = extras.getString("contacts");
			include = extras.getString("include");
			frmTbl = extras.getString("fromTb");
			readSms=extras.getString("read");
			Find();
		}

	}

	public void Find() {
		try {
			adapter = new InteractiveArrayAdapter(this, getModel());
			ListView listView = (ListView) findViewById(R.id.fakelist);
			listView.setAdapter(adapter);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private List<Model> getModel() {
		String[] columnNames = { "_id", "body", "address" ,"person"};
		List<Model> list = new ArrayList<Model>();
		Uri uri;
		Uri myPerson;
		String address = "";
		String body = "";
		String theId = "";
		String contactDisplayName = "";
		String peronId="";
		Cursor c = null;
		Cursor cur = null;
		String[] projection = new String[] { People.NAME };

		if (frmTbl.equals("All")) {
			uri = Uri.parse("content://sms");
		} else {
			uri = Uri.parse("content://sms/" + frmTbl);
		}
		try {
			c = managedQuery(uri, columnNames, getLikeString(filterText
					.getText().toString()), null, "date DESC");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			final TextView listheader = (TextView) findViewById(R.id.listheader);
			listheader.setText(" Total:- " + String.valueOf(c.getCount())
					+ " Results");
			for (boolean hasData = c.moveToFirst(); hasData; hasData = c.moveToNext()) {
				address = c.getString(c.getColumnIndex("address"));
				body = c.getString(c.getColumnIndexOrThrow("body"));
				theId = c.getString(c.getColumnIndexOrThrow("_id"));
				peronId=c.getString(c.getColumnIndexOrThrow("person"));
				myPerson = Uri
						.withAppendedPath(Contacts.Phones.CONTENT_FILTER_URL,
								Uri.encode(address));
				cur = getContentResolver().query(myPerson, projection, null,
						null, null);
				
				for (boolean hasData2 = cur.moveToFirst(); hasData2; hasData2 = cur
						.moveToNext()) {
					contactDisplayName = cur.getString(cur
							.getColumnIndexOrThrow("name"));
				}

				if (contactDisplayName == null || contactDisplayName.equals("")) {
					contactDisplayName = address;
				}
			Bitmap b=	getFacebookPhoto(peronId,address);
				list.add(get(body, "" + contactDisplayName, theId,b));

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		c.close();

		return list;
	}

	private Model get(String s, String sl, String theId,Bitmap b) {
		return new Model(s, sl, theId,b);
	}

	private static final String getLikeString(String inputLike) {
		inputLike = inputLike.trim();
		StringBuffer buffer = new StringBuffer();
		if (inputLike.length() == 0) {
			buffer.append(" ADDRESS NOT  LIKE 'x'  ");
		} else if (!inputLike.contains(",")) {
			buffer.append(" BODY LIKE '%" + inputLike + "%' ");
		} else {
			String str[] = inputLike.split(",");
			for (String s : str) {
				buffer.append(" BODY LIKE '%" + s.trim() + "%' OR ");
			}
			buffer.delete(buffer.length() - 3, buffer.length());
		}
		if(!include.equals("All")){
		if (contactsToInclude.trim().length()>0) {
			if(include.equals("Include")){
			buffer.append(" AND "+ contactsToInclude);
			}else{
				buffer.append(" AND "+  contactsToInclude.replace("IN", " NOT IN "));
			}
		}
		}
		
		if(!readSms.equals("All")){
				if(readSms.equals("Read")){
				buffer.append(" AND read = 0 ");
				}else{
					buffer.append(" AND read = 1");
				}
			}
		
		
		return buffer.toString();
	}

	protected void onPause() {
		super.onPause();
		Intent result = new Intent();
		result.putExtra("filterTXT", filterText.getText().toString());
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, result);
		} else {
			getParent().setResult(Activity.RESULT_OK, result);
		}
		finish();
	}

	@Override
	public void onBackPressed() {
		Intent result = new Intent();
		result.putExtra("filterTXT", filterText.getText().toString());
		if (getParent() == null) {
			setResult(Activity.RESULT_OK, result);
		} else {
			getParent().setResult(Activity.RESULT_OK, result);
		}
		super.onBackPressed();
	}
	
	
	public Bitmap getFacebookPhoto(String person,String addr) {
		 Uri photoUri = null;
		 ContentResolver cr = this.getContentResolver();
	    
	    if(person!=null){
	    	if(!person.equals("null")){
	    		long userId = Long.valueOf(person);
		        photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
	    	} else {
		        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
		        return defaultPhoto;
		    }
	    }else{
	    	Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(addr));
		    Cursor contact = cr.query(phoneUri,
		    new String[] { ContactsContract.Contacts._ID }, null, null, null);

		    if (contact.moveToFirst()) {
		        long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
		        photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userId);
		    }
		    else {
		        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
		        return defaultPhoto;
		    }
	    	
	    }
	  
	    if (photoUri != null) {
	        InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(
	                cr, photoUri);
	        if (input != null) {
	            return BitmapFactory.decodeStream(input);
	        }
	    } else {
	        Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(),  R.drawable.personicon);
	        return defaultPhoto;
	    }
	    Bitmap defaultPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.personicon);
	    return defaultPhoto;
	}
}