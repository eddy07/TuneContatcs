package com.example.tunecontacts;


import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements OnClickListener{
   
	private Button updateBtn = null;
	Cursor cursor=null;
	ListView l = null;
	ListView list1 = null;
	ListView list2 = null;
	TextView text=null;
	TextView text2=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Cursor curseur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		updateBtn = (Button) findViewById(R.id.button1);
		text2 = (TextView) findViewById(R.id.textView2);
		text2.setBackgroundColor(TRIM_MEMORY_UI_HIDDEN);
		text2.setText("");
		l = (ListView) findViewById(R.id.listView1);
		list1 = l;
		list2 = (ListView) findViewById(R.id.listView2);
		text = (TextView) findViewById(R.id.textView1);
		listContacts(curseur,l,0);
		updateBtn.setOnClickListener(this);
	}
	
	public void onClick(View view) {
		// TODO Auto-generated method stub
		try{
		updateBtn.setBackgroundColor(TRIM_MEMORY_UI_HIDDEN);
		updateBtn.setText("");
		text.setText("");
		cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		while (cursor.moveToNext()) {
						String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
			if (Integer.parseInt( hasPhone) > 0) {

				Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);

				while (phones.moveToNext()) { 
					String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
					String oldPhoneNumber = phoneNumber;

					if((has8Chars(phoneNumber)) || (has12Chars(phoneNumber)) || (has13Chars(phoneNumber))
							|| has8CharsForCamtel(phoneNumber) || has12CharsForCamtel(phoneNumber) || has13CharsForCamtel(phoneNumber)){

						ContentValues values = new ContentValues();
						values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
						values.putNull(ContactsContract.Data.RAW_CONTACT_ID);
						values.put(ContactsContract.Data.RAW_CONTACT_ID,contactId);

						phoneNumber = newPhoneNumber(phoneNumber);
						values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);
						values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM);
						String mSelectionClause = ContactsContract.CommonDataKinds.Phone.NUMBER +  "= ?";
						String[] mSelectionArgs = {oldPhoneNumber};
						getContentResolver().update(ContactsContract.Data.CONTENT_URI,values,mSelectionClause,mSelectionArgs);
					}
				}

				phones.close(); 

			}

		}
		Cursor cursor1 = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
		listContacts(cursor1,list1,1);
		Cursor cursor2 = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
	
		text2.setText("Contacts");
		listContacts(cursor2,list2,0);
		Toast.makeText(getApplicationContext(),"Contacts updated",Toast.LENGTH_LONG).show();
		}catch(Exception e){
			Toast.makeText(getApplicationContext(),"Error while updating Contacts",Toast.LENGTH_LONG).show();
		}

	}


	public String newPhoneNumber(String phoneNumber){
		String number = null;
		String  prefixNumber = null;
		String surfixNumber =null;

		if(has8Chars(phoneNumber)) {
			number = "6"+phoneNumber;
		}
		else if(has8CharsForCamtel(phoneNumber)){
			number = "2"+phoneNumber;
		}
		else if(has12Chars(phoneNumber)) {
			prefixNumber = phoneNumber.substring(0,4);
			surfixNumber = phoneNumber.substring(4, 12);
			number = prefixNumber+"6"+surfixNumber;
		}
		else if(has12CharsForCamtel(phoneNumber)){
			prefixNumber = phoneNumber.substring(0,4);
			surfixNumber = phoneNumber.substring(4, 12);
			number = prefixNumber+"2"+surfixNumber;
		}
		else if (has13Chars(phoneNumber)) {
			prefixNumber = phoneNumber.substring(0,5);
			surfixNumber = phoneNumber.substring(5, 13);
			number = prefixNumber+"6"+surfixNumber;
		}
		else if(has13CharsForCamtel(phoneNumber)){
			prefixNumber = phoneNumber.substring(0,5);
			surfixNumber = phoneNumber.substring(5, 13);
			number = prefixNumber+"2"+surfixNumber;
		}

		return number;
	}

	public boolean has8Chars(String number){
		int testValue=0;
		if(number.length()==8){
			if((number.startsWith("5"))||(number.startsWith("6"))||(number.startsWith("7"))||(number.startsWith("9"))){
				testValue = 1;
			}
		}
		return testValue==1; 
	}

	public boolean has8CharsForCamtel(String number){
		int testValue=0;
		if(number.length()==8){
			if((number.startsWith("2"))||(number.startsWith("3"))){
				testValue = 1;
			}
		}
		return testValue==1; 
	}
	public boolean has12Chars(String number){
		int testValue=0;
		if(number.length()==12){
			if((number.startsWith("+237")) && has8Chars(number.substring(4,12))){
				testValue = 1;
			}
		}
		return testValue==1;
	}
	public boolean has12CharsForCamtel(String number){
		int testValue=0;
		if(number.length()==12){
			if((number.startsWith("+237")) && has8CharsForCamtel(number.substring(4,12))){
				testValue = 1;
			}
		}
		return testValue==1;
	}

	public boolean has13Chars(String number){
		int testValue=0;
		if(number.length()==13){
			if(((number.startsWith("00237"))) && has8Chars(number.substring(5,13))){
				testValue = 1;
			}
		}
		return testValue==1; 
	}
	public boolean has13CharsForCamtel(String number){
		int testValue=0;
		if(number.length()==13){
			if(((number.startsWith("00237"))) && has8CharsForCamtel(number.substring(5,13))){
				testValue = 1;
			}
		}
		return testValue==1; 
	}


	public  void listContacts(Cursor cursor,ListView l,int t){
	
		List<String> contacts = new ArrayList<String>();
		while (cursor.moveToNext()) {

			String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); 
			String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); 
			if (Integer.parseInt( hasPhone) > 0) {

				Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, 
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ contactId, null, null);

				while (phones.moveToNext()) { 
					String phoneNumber = phones.getString(phones.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER));
					//on affiche
					String contact = name+"\n"+phoneNumber;
					contacts.add(contact);
				}
				phones.close(); 
				
				if(t!=0){
					ArrayAdapter<String> adapter = null;
					l.setAdapter(adapter);
				}
				else{
					ArrayAdapter<String> adapt = new ArrayAdapter<String>(this,R.layout.list_view,contacts.toArray(new String[contacts.size()])); 
					l.setAdapter(adapt);
				}
				

			}
		}
	}
}
//images_207,180,050,008,100,025,014,049 -- transparent  background images 
