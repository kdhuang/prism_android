package com.prism.app;

import com.prism.app.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class PrismActivity extends Activity {
	protected static String uri;
	protected static final String TAG = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        LoadPreferences();
        
        ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        bar.setDisplayShowHomeEnabled(false); //hide title bars
//        bar.setDisplayShowTitleEnabled(false); //hide app title
        
        ActionBar.Tab tab1 = bar.newTab().setText("Capture"); // setIcon(R.drawable.ic_tab)
        ActionBar.Tab tab2 = bar.newTab().setText("See");
        ActionBar.Tab tab3 = bar.newTab().setText("Understand");
        
        Fragment fragment1 = new OneFragmentTab();
        Fragment fragment2 = new TwoFragmentTab();
        Fragment fragment3 = new ThreeFragmentTab();
        
        tab1.setTabListener(new MyTabsListener(fragment1));
        tab2.setTabListener(new MyTabsListener(fragment2));
        tab3.setTabListener(new MyTabsListener(fragment3));

        bar.addTab(tab1);
        bar.addTab(tab2);
        bar.addTab(tab3);
    }
    
    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
      
    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        uri = sharedPreferences.getString("URL", "");
        if (uri == "") {
        	uri = "http://iapi-staging.prismsl.net/v1/15/1/imagesink/7/";
        }
    }
    
    protected class MyTabsListener implements ActionBar.TabListener {
        private Fragment fragment;

        public MyTabsListener(Fragment fragment) {
            this.fragment = fragment;
        }

        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
        }

        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
            ft.add(R.id.fragment_place, fragment, null);
        }

        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
        	ft.remove(fragment);
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
            	showDialog();
                return true;
            case R.id.signout:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    void showDialog() {    	
    	final AlertDialog.Builder alert = new AlertDialog.Builder(this);

    	alert.setTitle("Settings");
    	alert.setMessage("Current Server:\n"+ uri);
    	
    	LayoutInflater inflater = LayoutInflater.from(this);
        final View view=inflater.inflate(R.layout.my_dialog_layout, null);
    	alert.setView(view);
    	
    	alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			EditText input = (EditText) view.findViewById(R.id.base_url_input);
    			EditText input1 = (EditText) view.findViewById(R.id.account_number_input);
    			EditText input2 = (EditText) view.findViewById(R.id.sink_number_input);
    			
    			String base_url = input.getText().toString();
    			String account = input1.getText().toString();
    			String imagesink = input2.getText().toString();
    			
    			Context context = getApplicationContext();
    			int duration = Toast.LENGTH_SHORT;
    			Toast toast;
    			
    			if (base_url.isEmpty() || account.isEmpty() || imagesink.isEmpty()) {
    				CharSequence text = "Sorry, some fields were not completed. Form not saved.";
    				toast = Toast.makeText(context, text, duration);
    				//make it so dialog does not close?
    			} else {
    				uri = "http://" + base_url + "/v1/" + account + "/1/imagesink/" + imagesink;
    				SavePreferences("URL", uri);
    				OneFragmentTab.getURL();
    				CharSequence text = uri + " saved.";
    				toast = Toast.makeText(context, text, duration);
    			}
    			toast.show();
    			dialog.dismiss();
    		}
    	});

    	alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			// Canceled.
    			dialog.dismiss();
    		}
    	});
    	alert.show();
    	// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
    }
    
    public static String getURL() {
    	return uri;
    }
}