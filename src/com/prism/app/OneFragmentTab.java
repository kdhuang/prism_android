package com.prism.app;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OneFragmentTab extends Fragment {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private Uri outputFileUri;
	private TextView text;
	private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 60000; //each minute
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
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
	        	// put the image onto server
	        	text.setText(outputFileUri.toString());
        	    
//	        	try {
//		        	HttpClient httpClient = new DefaultHttpClient();
//		        	HttpContext localContext = new BasicHttpContext();
//	        		HttpPost postRequest = new HttpPost("http://10.100.1.236:8000/v1/14/1/imagesink/1/");
//	        		MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
//
//	        		bm = BitmapFactory.decodeFile("/sdcard/test.jpg");
//	        		Bitmap bmpCompressed = Bitmap.createScaledBitmap(bm, 640, 480, true);
//	        		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//	        		bmpCompressed.compress(CompressFormat.JPEG, 100, bos);
//	        		byte[] bytes = bos.toByteArray();
////	        		ByteArrayBody bab = new ByteArrayBody(data, "test.jpg");
////	        		reqEntity.addPart("uploaded", bab);
////	        		reqEntity.addPart("photoCaption", new StringBody("test"));
//	        		reqEntity.addPart("myImage", new ByteArrayBody(bytes, "temp.jpg")); 
//	        		postRequest.setEntity(reqEntity);
//	        		HttpResponse response = httpClient.execute(postRequest,localContext);
//	        		
//	        		BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent(), "UTF-8"));  
//	                String sResponse = reader.readLine();
//	        	    
//	        	} catch (Exception e) {
//	        		// handle exception here
////	        		Log.e(e.getClass().getName(), e.getMessage());
//	        		Log.v("myApp", "Some error came up");  
//	        	}
	        	new ImageUploadTask().execute();


	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	
	 private class ImageUploadTask extends AsyncTask<URL, Integer, Long> {
	     protected Long doInBackground(URL... urls) {
	        	try{
	        	String url = "http://192.168.1.6:8000/v1/14/1/imagesink/1/";
//	        	File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
	        	FileReader input = new FileReader("/mnt/sdcard/test.jpg");
	        	BufferedReader bufRead = new BufferedReader(input);
	        	String s = null;
	        	int b = 10;
	        	URLConnection connection = new URL(url).openConnection();
	        	connection.setDoOutput(true); // Triggers POST.
	        	connection.setRequestProperty("Content-Type", "image/jpeg");
	        	OutputStream output = null;
	        	try {
	        	   output = connection.getOutputStream();
	        	   while ((s = bufRead.readLine()) != null)   {
	        	     output.write(s.getBytes());
	        	     output.write(b);
	        	   }
	        	} finally {
	        		if (output != null) try { output.close(); } catch (IOException logOrIgnore) {}
	        	}
	        	InputStream response = connection.getInputStream();
	        	}
	        	catch (IOException e) {
	        		Log.e(e.getClass().getName(), e.getMessage());
	        	}
	        	return null;
	     }

	     protected void onProgressUpdate(Integer... progress) {

	     }

	     protected void onPostExecute(Long result) {

	     }
	 }
	 
	 private void takeSnapshot() {
		 timer.scheduleAtFixedRate(new TimerTask() {
			 @Override
	         public void run() {
	               
				 // send to server method here          
	              
	        }
		 }, 0, UPDATE_INTERVAL);
	 }
}
	