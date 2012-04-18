package com.prism.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Fragment;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class OneFragmentTab extends Fragment {
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Timer timer = new Timer();
    private static final long UPDATE_INTERVAL = 1000*10*1; //each minute (60)
	protected static final String TAG = null;
    private CameraPreview mpreview;
    private Camera camera;
    private static String url;
    
    //context = getActivity().getApplicationContext()
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
        final View view = inflater.inflate(R.layout.fragment_one, container, false);
//        getURL();
        final Button start = (Button) view.findViewById(R.id.button1);
        final Button stop = (Button) view.findViewById(R.id.button2);
        start.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
              // Action on click
        		takeSnapshot();
        	}
        });
        stop.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
              // Action on click
        		timer.cancel();
//        		camera.release();
        	}
        });
        
        camera = getCameraInstance();
        camera.setDisplayOrientation(90);
        mpreview = new CameraPreview(getActivity().getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) view.findViewById(R.id.camera_preview);
        preview.addView(mpreview);
//        Camera.Parameters params = camera.getParameters();
//        params.setJpegQuality(100);
//        camera.setParameters(params);
        return view;
    }
	
	
	public void onPause() {
//		FrameLayout preview = (FrameLayout) (ViewGroup)getView().findViewById(R.id.camera_preview);
//		preview.removeView(mpreview);
		super.onPause();
	}
	
//	public void onResume() {
//		FrameLayout preview = (FrameLayout) (ViewGroup)getView().findViewById(R.id.camera_preview);
//		preview.addView(mpreview);
//		super.onResume();
//	}
	
//	public static void getURL() {
//		url = PrismActivity.getURL();
//	}
	
	private void postHTTP() {		
    	new Thread(new Runnable() {
			public void run() {
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost post = new HttpPost(url);  //-X PUT
					post.setEntity(new FileEntity(new File(Environment.getExternalStorageDirectory(), "Prism/test.jpg"), "image/jpeg"));  //@ - absolute path
					httpClient.execute(post);
				} catch(Exception e) {
					//-f, fail silently
					//http://stackoverflow.com/questions/9487115/hitting-java-web-service-curl-or-urlconnection
				}
			}
		}).start();
	}

	private void takeSnapshot() {
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
                // get an image from the camera
                camera.takePicture(null, null, picture);
	        }
		}, 0, UPDATE_INTERVAL);
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance -- open(int) depending on camera #
	    }
	    catch (Exception e) {
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private PictureCallback picture = new PictureCallback() {

	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	    	camera.startPreview();
	        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
	        if (pictureFile == null){
	            Log.d(TAG, "Error creating media file, check storage permissions: ");
	            return;
	        }

	        try {
	            FileOutputStream fos = new FileOutputStream(pictureFile);
	            fos.write(data);
	            fos.close();
	            postHTTP();
	        } catch (FileNotFoundException e) {
	            Log.d(TAG, "File not found: " + e.getMessage());
	        } catch (IOException e) {
	            Log.d(TAG, "Error accessing file: " + e.getMessage());
	        }
	    }
	};
	
	// Saving files

	private static File getOutputMediaFile(int type){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "Prism");

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d("Prism", "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    File mediaFile;
	    if (type == MEDIA_TYPE_IMAGE){
	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "test.jpg");
//	    } else if(type == MEDIA_TYPE_VIDEO) {
//	        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
	    } else {
	        return null;
	    }
	    return mediaFile;
	}
}
	