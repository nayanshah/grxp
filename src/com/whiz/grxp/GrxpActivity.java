package com.whiz.grxp;

import android.app.Dialog;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TabHost;

public class GrxpActivity extends TabActivity {
    /** Called when the activity is first created. */
	public enum TABS {BALANCE, EXPENSE};
	public static final int DIALOG_ABOUT_ID = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set layout to main.xml
        setContentView(R.layout.main);

        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab
        // Initialize a TabSpec for each tab and add it to the TabHost

        // Tab 1
        /*
        intent = new Intent().setClass(this, HomeActivity.class);
        spec = tabHost.newTabSpec("home").setIndicator("Home",
                          res.getDrawable(R.drawable.ic_tab_balance))
                      .setContent(intent);
        tabHost.addTab(spec);
        // */
        // Tab 2
        intent = new Intent().setClass(this, BalanceActivity.class);
        spec = tabHost.newTabSpec("balance").setIndicator(res.getText(R.string.balance),
                          res.getDrawable(R.drawable.ic_tab_balance))
                      .setContent(intent);
        tabHost.addTab(spec);

        // Tab 3
        intent = new Intent().setClass(this, ExpenseActivity.class);
        spec = tabHost.newTabSpec("expense").setIndicator(res.getText(R.string.expense),
                          res.getDrawable(R.drawable.ic_tab_expense))
                      .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }
    
    public void switchTab(TABS tab) {
    	getTabHost().setCurrentTab(tab.ordinal());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem menuItem = menu.add("About");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				showDialog(DIALOG_ABOUT_ID);
				return true;
			}
		});
        return true;
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = new Dialog(this);
        switch(id) {
        case DIALOG_ABOUT_ID:
    		dialog.setContentView(R.layout.about_dialog);
    		dialog.setTitle("About");

    		ImageView image = (ImageView) dialog.findViewById(R.id.about_icon);
    		image.setImageResource(R.drawable.ic_launcher);
            break;
        default:
            dialog = null;
        }
        return dialog;
    }    
}