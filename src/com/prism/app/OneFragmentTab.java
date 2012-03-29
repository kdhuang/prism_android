package com.prism.app;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OneFragmentTab extends Fragment {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private Uri outputFileUri;
	private TextView text;
	private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 1000*60*1; //each minute
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.
		ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        text = (TextView) view.findViewById(R.id.text);
        text.setText("HELLO");
        
      final Button button = (Button) view.findViewById(R.id.button1);
      button.setOnClickListener(new View.OnClickListener() {
          public void onClick(View v) {
              // Action on click
        	  // add "extends TimerTask"
        	  // takeSnapshot()
        	  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	  File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        	  outputFileUri = Uri.fromFile(file);
        	  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        	  startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
          }
      });
        return view;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	// post image to server
	        	text.setText(outputFileUri.toString());
	        	
	        	try {
	        	    DefaultHttpClient httpClient = new DefaultHttpClient();
	        	    HttpPost post = new HttpPost("http://10.100.1.236:8000/v1/13/1/imagesink/1/");  //-X PUT
	        	    post.setEntity(new FileEntity(new File(Environment.getExternalStorageDirectory(), "test.jpg"), "image/jpeg"));  //@ - absolute path
	        	    httpClient.execute(post);
	        	} catch(Exception e) {
	        	    //-f, fail silently
	        		//http://stackoverflow.com/questions/9487115/hitting-java-web-service-curl-or-urlconnection
	        	}

//	        	new ImageUploadTask().execute();
//	        	new Thread(new Runnable() {
//	        	    public void run() {
	        		// code here
//	        	    }
//	        	  }).start();

	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	 

	private void takeSnapshot() {
		 timer.scheduleAtFixedRate(new TimerTask() {
			 @Override
	         public void run() {
	               
//				send to server method here
//	        	new ImageUploadTask().execute();			 
	              
	        }
		 }, 0, UPDATE_INTERVAL);
	 }
}
	