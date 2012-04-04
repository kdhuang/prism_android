package com.prism.app;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class OneFragmentTab extends Fragment {
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
	private Uri outputFileUri;
	private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 1000*60*1; //each minute
    private CameraPreview mpreview;
    private Camera camera;
    
    //context = getActivity().getApplicationContext()
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        final View view = inflater.inflate(R.layout.fragment_one, container, false);
        
        final Button start = (Button) view.findViewById(R.id.button1);
        final Button stop = (Button) view.findViewById(R.id.button2);
        start.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
              // Action on click
        		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        		File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
        		outputFileUri = Uri.fromFile(file);
        		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        	}
        });
        stop.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
              // Action on click
        	}
        });
        
        camera = getCameraInstance();
        camera.setDisplayOrientation(90);
        mpreview = new CameraPreview(getActivity().getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mpreview);
        
        return view;
    }
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == Activity.RESULT_OK) {
	        	// post image to server
	        	new Thread(new Runnable() {
					public void run() {
						try {
							DefaultHttpClient httpClient = new DefaultHttpClient();
							HttpPost post = new HttpPost("http://iapi-staging.prismsl.net/v1/15/1/imagesink/7/");  //-X PUT
							post.setEntity(new FileEntity(new File(Environment.getExternalStorageDirectory(), "test.jpg"), "image/jpeg"));  //@ - absolute path
							httpClient.execute(post);
						} catch(Exception e) {
							//-f, fail silently
							//http://stackoverflow.com/questions/9487115/hitting-java-web-service-curl-or-urlconnection
						}
					}
				}).start();

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
	               
	        }
		}, 0, UPDATE_INTERVAL);
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance -- open(int) depending on camera #
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
}
	