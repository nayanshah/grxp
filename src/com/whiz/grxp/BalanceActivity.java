package com.whiz.grxp;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class BalanceActivity extends OrmLiteBaseActivity<DbAdapter> {

	RuntimeExceptionDao<Person, String> personDao;
	List<Person> people;
	ListView listView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the balance.xml layout
        setContentView(R.layout.balance);

        personDao = getHelper().getPersonDao();
        listView = (ListView) findViewById(R.id.balance_list);
        updateList();

        // Set click listener on Ok button
        final Button button = (Button) findViewById(R.id.btn_add_person);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	//saveExpense();
            	addPerson();
            }
        });

        registerForContextMenu(listView);
//        listView.setOnItemLongClickListener(new ItemLongClickListener());
        registerReceiver(new Receiver(), new IntentFilter("com.whiz.grxp.UPDATE_BAL"));
    }
    
    public void updateList() {
        // Fetch listView of listView from the database
        people = personDao.queryForAll();

        // Attach listView of listView to the  listView view
        ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, 
        		android.R.layout.simple_list_item_1, people);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

	// Handler for adding a person
    public void addPerson() {
    	EditText textField = (EditText) findViewById(R.id.txt_name);
    	String name = textField.getText().toString();

    	if(name.equals("")) { 
        	Toast.makeText(this, "Enter a name", Toast.LENGTH_SHORT).show();
        	textField.requestFocus();
        	return;
    	}

    	// Reset text box
    	textField.clearFocus();
    	textField.setText("");
    	
    	// Add it to the database
    	try {
	    	personDao.create(new Person(name));
	    	updateList();
	        sendBroadcast(new Intent("com.whiz.grxp.UPDATE_EXP"));
	    
	    	Toast.makeText(BalanceActivity.this, "Person saved", Toast.LENGTH_SHORT).show();

    	} catch (Exception e) {
	    	Toast.makeText(BalanceActivity.this, "Error while saving the person", Toast.LENGTH_SHORT).show();
    	}
    }    
    
	// Confirm and then delete a person
    public void deletePerson(final Person p) {
        new AlertDialog.Builder(this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.delete)
	        .setMessage("Sure you want to delete " + p.getName() + " ?")
	        .setPositiveButton(R.string.delete, 
	        	new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	if(p.getName().equals("Me")) {
			        		Toast.makeText(BalanceActivity.this, "Sorry, no self destruct :)", Toast.LENGTH_SHORT);
		            		return;
		            	}
		            	personDao.delete(p);
		        		Toast.makeText(BalanceActivity.this, p.getName() + " deleted", Toast.LENGTH_SHORT);
		        		updateList();
		                sendBroadcast(new Intent("com.whiz.grxp.UPDATE_EXP"));
		            }
	        })
	        .setNegativeButton(R.string.cancel, null)
	        .show();
    }

	// Confirm and then delete a person
    public void resetBalance(final Person p) {
        new AlertDialog.Builder(this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.reset_balance)
        .setMessage("Sure you reset " + p.getName() + "'s balance ?")
        .setPositiveButton(R.string.reset, 
        	new DialogInterface.OnClickListener() {
	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	p.reset();
	            	personDao.update(p);
	        		Toast.makeText(BalanceActivity.this, p.getName() + "'s balance was reset", Toast.LENGTH_SHORT);
	        		updateList();
	                sendBroadcast(new Intent("com.whiz.grxp.UPDATE_EXP"));
	            }
        })
        .setNegativeButton(R.string.cancel, null)
        .show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    Person p = people.get(info.position);
	    switch (item.getItemId()) {
		    case R.id.mnu_reset:
		        resetBalance(p);
		        return true;
		    case R.id.mnu_delete:
		        deletePerson(p);
		        return true;
		    default:
		      return super.onContextItemSelected(item);
	    }
    }    
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.person_menu, menu);
    }
    
    private class Receiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
			updateList();
    	}
    }
   
}
