package com.prism.app;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ThreeFragmentTab extends Fragment {
	TextView text;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_three, container, false);
        text = (TextView) view.findViewById(R.id.text);
        text.setText("We're working on this feature.");
		return view;
	}
}
