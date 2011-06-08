/*package ar2.com;

import android.app.Activity;
import android.os.Bundle;

public class ar2 extends Activity {
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}*/

package colorfight.com;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import colorfight.com.R;

public class TakePicture extends Activity {
	private SurfaceView preview=null;
	private SurfaceHolder previewHolder=null;
	private Camera mCamera=null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("color", "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		preview=(SurfaceView)findViewById(R.id.surface_camera); 
		previewHolder=preview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		Log.d("color", "onCreate end");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//Colorfight.startedCam =false;
		Colorfight.updateSurface = true;
		Log.d("color", "Takepic onDestroy");
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		//-- When we touch the screen, it will auto-focus again
		Log.d("color", "dispatchTouchEvent");
		if(mCamera != null){
			mCamera.cancelAutoFocus(); //release the previous auto-focus
			mCamera.autoFocus(new Camera.AutoFocusCallback(){
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					Log.d("HOME", "isAutofoucs " + Boolean.toString(success));					
				}
			} );
			
		}
		
		return super.dispatchTouchEvent(ev);
		
	
	}

	//--Define which keys will "take the picture"
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("color", "onKeyDown: " + keyCode);

		if (keyCode==KeyEvent.KEYCODE_CAMERA ||	keyCode==KeyEvent.KEYCODE_SEARCH ||	keyCode==23) {
			takePicture();
			
			return(true);
		}
		
		return(super.onKeyDown(keyCode, event));
	}
	
	private void takePicture() {
		mCamera.takePicture(null, null, photoCallback);
	}
	
	SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
		
		
		public void surfaceCreated(SurfaceHolder holder) {
			Log.d("color", "surfaceCreated");
			mCamera=Camera.open();
			Log.d("color", "surfaceCreated nach open");
			try {
				mCamera.setPreviewDisplay(previewHolder);
			}
			catch (Throwable t) {
				Log.e("PictureDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
				Log.d("color", "Exception in setPreviewDisplay()", t);
				Toast
					.makeText(TakePicture.this, t.getMessage(), Toast.LENGTH_LONG)
					.show();
			}
			Log.d("color", "surfaceCreated end");
		}
		
		public void surfaceChanged(SurfaceHolder holder,int format, int width, int height) {
			Log.d("color", "surfaceChanged");
			Camera.Parameters parameters=mCamera.getParameters();
			
			//Customize width/height here - otherwise defaults to screen width/height
			parameters.setPreviewSize(width, height);
			parameters.setPictureFormat(PixelFormat.JPEG);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
			parameters.setJpegQuality(100);

			//mCamera.setParameters(parameters);
			mCamera.startPreview();
			
			Log.d("color", "pre autofocus");
			//-- Must add the following callback to allow the camera to autofocus.
			mCamera.autoFocus(new Camera.AutoFocusCallback(){
				@Override
				public void onAutoFocus(boolean success, Camera camera) {
					Log.d("HOME", "isAutofoucs " + Boolean.toString(success));					
				}
			} );
			Log.d("color", "surfaceChanged end");
		}
		
		public void surfaceDestroyed(SurfaceHolder holder) {

		}
	};
	
	Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d("color", "photoCallback");
			Intent mIntent = new Intent();
			mIntent.putExtra("picture", data);
			setResult(1, mIntent); //-- 1 for result code = ok
			
			Colorfight.ownPicturemon = BitmapFactory.decodeByteArray(data, 0, data.length);
			/*Color mColor;

			for(int x=0; x<10; x++){
				for(int y=0; y<10; y++){
					int red = pic.getPixel(x, y) & 0x00FF0000 >> 16 ;
					Log.d("color", "color: " + red );
					
				}
			}
			
			for(int i=0; i<data.length; i++){
				//Log.d("color", "i: " + data[i]);
			}*/
			
			//-- This segment moved from surfaceDestroyed - otherwise the Camera is not properly released
			if(mCamera != null){
				mCamera.stopPreview();
				mCamera.release();
				mCamera=null;
			}

			finish();
			Log.d("color", "photoCallback end");
		}
	};
	

}