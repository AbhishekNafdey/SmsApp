package com.message.android.messages;

import java.util.HashMap;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
 
public class WelcomeActivity extends Activity {
	static ArrayAdapter<String> adapter;
	static MultiAutoCompleteTextView textView;
	private static EditText filterText;
	static final int REQUEST_CODE = 1;
	static HashMap  contactsMap ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcomeactivity);
		
		final Button showlistbutt = (Button) findViewById(R.id.showmessages);
		final Button markAllRead = (Button) findViewById(R.id.markread);
		filterText =(EditText) findViewById(R.id.findText);
		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		final RadioGroup fromTable = (RadioGroup) findViewById(R.id.radiogroupfromtable);
		final RadioGroup readSMS = (RadioGroup) findViewById(R.id.radiogroupstatus);
		textView = (MultiAutoCompleteTextView) findViewById(R.id.autocomplete);
		textView.setThreshold(1);
		textView.addTextChangedListener(textChecker);
		textView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		
		/*ContentResolver contentResolver = getContentResolver();
		Cursor cursor = contentResolver.query(
		    Uri.parse("content://sms/sent"), null, null, null, null);

		String[] columnNames = cursor.getColumnNames();*/
		
		showlistbutt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent showList = new Intent(getApplicationContext(), MessageListActivity.class);
				Bundle bundle = new Bundle();
				bundle.putCharSequence("filterTXT", filterText.getText().toString());
				bundle.putCharSequence("include",( (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId())).getText());
				bundle.putCharSequence("fromTb",( (RadioButton) findViewById(fromTable.getCheckedRadioButtonId())).getText());
				bundle.putCharSequence("read",( (RadioButton) findViewById(readSMS.getCheckedRadioButtonId())).getText());
				bundle.putCharSequence("contacts",getContactsTond());
				showList.putExtras(bundle);
				startActivityForResult(showList,REQUEST_CODE);
				    
			}
		});
		
		markAllRead.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ContentValues values = new ContentValues();
				values.put("read","1");
				int count =getContentResolver().update(Uri.parse("content://sms"),values," read = '0' ",null);
				Toast.makeText(getApplicationContext(), count+" Rows Affected",3000).show();
			}
			});
	}
	
	
			
			
		
	@Override
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_CODE) {
	      if (resultCode == RESULT_OK) {
	         Bundle extras = data.getExtras();
	              if (extras != null) {
	            	  filterText.setText(extras.getString(("filterTXT")));
	              }
	      }
	    }
	  }

	private  final String getContactsTond(){
		if(textView.getText().toString().equals("")){
			return "";
		}
		String[] strAyy = {People.NUMBER};
		StringBuffer buffer = new StringBuffer();
		Uri myPerson = Contacts.Phones.CONTENT_URI;
		Cursor cur = managedQuery(myPerson, strAyy, getInString(textView.getText().toString(),"'"," NAME "), null,
				People.NAME + " ASC");
		
		for (boolean hasData2 = cur.moveToFirst(); hasData2; hasData2 = cur
				.moveToNext()) {
			
			buffer.append( cur.getString(cur.getColumnIndexOrThrow("NUMBER"))+",");
		}
		
		return getInString(buffer.toString(),"'"," address ");
		
	}
	
	
	private static final String getInString(String contacts, String singleCote,
            String columname) {
      contacts = contacts.trim();
      StringBuffer buffer = new StringBuffer();
      if (contacts.length() == 0) {
            return null;
      } else if (contacts.contains(",")) {
            buffer.append(" " + columname);
            buffer.append(" IN (" + singleCote + "0" + singleCote + ",");
            String str[] = contacts.split(",");
            for (String s : str) {
                  if (s.trim().length() != 0) {
                        buffer.append(singleCote + "" + s.trim() + "" + singleCote
                                    + ",");
                  }
            }
            buffer.deleteCharAt(buffer.length() - 1);
            buffer.append(")");

      }
      return buffer.toString();
}

	
	
	final TextWatcher textChecker = new TextWatcher() {
		public void afterTextChanged(Editable s) {
			//adapter.notifyDataSetChanged();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter = new ArrayAdapter<String>(getApplicationContext(),
					android.R.layout.simple_dropdown_item_1line,
					DynaList(s.toString()));
			textView.setAdapter(adapter);
		}
	};

	private final String[] DynaList(String s) {
		String[] strAyy = { People.NAME };
		try {
			if (s.length() == 0) {
				return strAyy;
			}
			Uri myPerson = People.CONTENT_URI;
			Cursor cur = managedQuery(myPerson, strAyy, getLikeString(s), null,
					People.NAME + " ASC");
			strAyy = new String[cur.getCount()];
			int i = 0;
			for (boolean hasData2 = cur.moveToFirst(); hasData2; hasData2 = cur
					.moveToNext()) {
				strAyy[i] = cur.getString(cur.getColumnIndexOrThrow("name"));
				i++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strAyy;
	}

	private static final String getLikeString(String inputLike) {
		inputLike = inputLike.trim();
		StringBuffer buffer = new StringBuffer();
		if (inputLike.length() == 0) {
			return null;
		} else if (!inputLike.contains(",")) {
			return " NAME LIKE '" + inputLike + "%' ";
		} else {
			String str[] = inputLike.split(",");
			for (String s : str) {
				buffer.append(" NAME LIKE '" + s.trim() + "%' OR ");
			}
			buffer.delete(buffer.length() - 3, buffer.length());
			return buffer.toString();
		}
	}

}
