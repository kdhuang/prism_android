package com.prism.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.prism.app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PrismActivity extends Activity {
	private static final long UPDATE_INTERVAL = 1000*10*1; //each minute (60)
	private static final int IMAGE_WIDTH = 1280;
	public static final String FOCUS_MODE_AUTO = "auto";
	protected static String uri;
	protected static final String TAG = null;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Timer timer;
    private CameraPreview mpreview;
    private Camera camera;
    private boolean startstop;
    FrameLayout preview;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final Button start = (Button) findViewById(R.id.button1);
        final Button rotate = (Button) findViewById(R.id.button2);
        
        startstop = true;
        start.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		TextView tv = (TextView) findViewById(R.id.button1);
        		if (startstop) {
        			timer = new Timer();
        			takeSnapshot();
        			tv.setText("Stop");
        			startstop = false;
        		} else {
        			timer.cancel();
        			tv.setText("Start");
        			startstop = true;
        		}
        	}
        });
        rotate.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		
        	}
        });
        
        // set up camera
        camera = getCameraInstance();
        Camera.Parameters params = camera.getParameters();
		List<Size> sizes = params.getSupportedPictureSizes();
		for (int i=0;i<sizes.size();i++){
			if (sizes.get(i).width == IMAGE_WIDTH) {
				params.setPictureSize(sizes.get(i).width, sizes.get(i).height);
				break;
			}
		}
		params.setFocusMode(FOCUS_MODE_AUTO);
		camera.setParameters(params);
        camera.setDisplayOrientation(90);
        mpreview = new CameraPreview(this, camera);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mpreview);
        
        LoadPreferences();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (camera == null) {
    		camera = Camera.open();
			camera.setDisplayOrientation(90);
	        mpreview = new CameraPreview(this, camera);
	        preview.removeAllViews();
	        preview.addView(mpreview);
			camera.startPreview();
    	} else {
    	}
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
    
    private void releaseCamera(){
        if (camera != null){
            camera.release();
            camera = null;
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
        	case R.id.accounts:
        		return true;
            case R.id.settings:
            	showDialog();
                return true;
            case R.id.signout:
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    
	private void postHTTP() {		
    	new Thread(new Runnable() {
			public void run() {
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost post = new HttpPost(uri);  //-X PUT
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
	    	try {
	    		Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
    				uri = "http://" + base_url + "/v1/" + account + "/1/imagesink/" + imagesink + "/";
    				SavePreferences("URL", uri);
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
}