package com.prism.app;

import com.prism.app.R;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class PrismActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
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
}