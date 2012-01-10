package com.whiz.grxp;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ExpenseActivity extends  OrmLiteBaseActivity<DbAdapter> {
	
	RuntimeExceptionDao<Person, String> personDao;
    List<Person> people;
	ListView listView;
	Spinner spinner;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the expense.xml layout
        setContentView(R.layout.expense);

        // Fetch listView of listView from the database
        personDao = getHelper().getPersonDao();
        people = personDao.queryForAll();
        listView = (ListView) findViewById(R.id.people_list);
        spinner = (Spinner) findViewById(R.id.spinner);
        
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateList();
        
        // Set click listener on Add expense button
        final Button button = (Button) findViewById(R.id.btn_add_expense);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // Perform action on clicks
            	saveExpense();
            }
        });

        registerReceiver(new Receiver(), new IntentFilter("com.whiz.grxp.UPDATE_EXP"));
    }

    public void updateList() {
        people = personDao.queryForAll();

        ArrayAdapter<Person> adapter = new ArrayAdapter<Person>(this, 
        		android.R.layout.simple_list_item_multiple_choice, people);
        listView.setAdapter(adapter);

        adapter = new ArrayAdapter<Person>(this, 
        		android.R.layout.simple_spinner_item, people);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

	// Handler for saving expense
    public void saveExpense() {
    	EditText textField = (EditText) findViewById(R.id.txt_amount);
    	String amountStr = textField.getText().toString();
    	
    	if(amountStr.equals("")) { 
        	Toast.makeText(this, "Enter some amount", Toast.LENGTH_SHORT).show();
        	textField.requestFocus();
        	return;
    	}
    	List<Person> paidFor = new ArrayList<Person>();
    	//SparseBooleanArray selected = listView.getCheckedItemPositions();
    	for(int i=0; i<people.size(); i++) {
        	if(listView.isItemChecked(i)) {
        		paidFor.add((Person) listView.getItemAtPosition(i));
    		}
    	}
    	double total = Integer.parseInt(amountStr);
    	double amount = - total / paidFor.size();
    	for(Person p : paidFor) {
    		p.addAmount(amount);
    		personDao.update(p);
    	}
    	
    	Person paidBy = (Person) spinner.getSelectedItem();
    	paidBy.addAmount(total);
    	personDao.update(paidBy);

    	textField.clearFocus();
    	textField.setText("");
    	listView.clearChoices();
    	updateList();
        sendBroadcast(new Intent("com.whiz.grxp.UPDATE_BAL"));

        // Notify user and open balances
    	Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
    }
    
    private class Receiver extends BroadcastReceiver {
    	@Override
    	public void onReceive(Context context, Intent intent) {
			updateList();
    	}
    }
}
