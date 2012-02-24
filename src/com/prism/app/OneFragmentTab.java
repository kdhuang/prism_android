package com.prism.app;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class OneFragmentTab extends Fragment {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private Uri outputFileUri;
	private TextView text;
	
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

	        	HttpClient client = new DefaultHttpClient();
	        	HttpPost httpost = new HttpPost("http://10.100.1.218:8000/v1/14/1/imagesink/1/");
//          	  	File file = new File(outputFileUri.toString());
          	  	httpost.setHeader("Content-type", "image/jpeg");
          	  	HttpResponse response = null;
          	  	try {
          	  		MultipartEntity entity = new MultipartEntity();
          	  		entity.addPart("image", new FileBody(new File(outputFileUri.toString()), "image/jpeg"));
          	  		httpost.setEntity(entity);
          	  		try {
          	  			response = client.execute(httpost);
          	  			System.out.println(response);
          	  		} catch (ClientProtocolException e) {
          	  			// TODO Auto-generated catch block
          	  			e.printStackTrace();
          	  		} catch (IOException e) {
          	  			// TODO Auto-generated catch block
          	  			e.printStackTrace();
          	  		}
              } catch (Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
              }

	        } else if (resultCode == Activity.RESULT_CANCELED) {
	            // User cancelled the image capture
	        } else {
	            // Image capture failed, advise user
	        }
	    }
	}
	
}
	