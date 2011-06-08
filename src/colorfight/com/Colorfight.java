package colorfight.com;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Colorfight extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);  
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
       SpaceWarView gameView = new SpaceWarView(getApplicationContext());
       setContentView(gameView);
    }

    //private static final int WIDTH = 50;
    //private static final int HEIGHT = 50;
    //private static final int STRIDE = 64;   // must be >= WIDTH

    private static int[] createColors(final int w, final int h) {
    	final int STRIDE = w;
    	int[] colors = new int[STRIDE * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int r = x * 255 / (w - 1);
                int g = y * 255 / (h - 1);
                int b = 255 - Math.min(r, g);
                int a = Math.max(r, g);
                colors[y * STRIDE + x] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }
        return colors;
    }

    public  class SpaceWarView extends View implements Runnable
    {
    	Bitmap enemyPicturemon;
    	Bitmap ownPicturemon;
    	Bitmap presstoplay;
    	Bitmap presstoplayScaled;
    	
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	//View current=getCurrentFocus();// findFocus();// findViewById(1);
    	private int width= display.getWidth(); 
    	private int height= display.getHeight(); 
		
        private Bitmap[] mBitmaps;
        private Bitmap[] mJPEG;
        private Bitmap[] mPNG;
        private int[]    mColors;
        private Paint    mPaint;

        private Bitmap codec(Bitmap src, Bitmap.CompressFormat format,int quality) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            src.compress(format, quality, os);

            byte[] array = os.toByteArray();
            return BitmapFactory.decodeByteArray(array, 0, array.length);
        }

    	public SpaceWarView(Context context)
    	{
    		super(context);
    		Log.d("color", "in constr 1");
            setFocusable(true);

            mColors = createColors(width/2, height);
            int[] colors = mColors;
            Log.d("color", "in constr 2");
            //mBitmaps = new Bitmap[6];
            enemyPicturemon = Bitmap.createBitmap(colors, 0, width/2, width/2, height,Bitmap.Config.ARGB_8888);
            presstoplay = BitmapFactory.decodeResource(getResources(), R.drawable.presstoplay);
            //presstoplayScaled = presstoplayScaled.createScaledBitmap (presstoplay,  width/2, height, false);
            
            Log.d("color", "in constr 3");
            /*
            // these three are initialized with colors[]
            mBitmaps[0] = Bitmap.createBitmap(colors, 0, STRIDE, WIDTH, HEIGHT,
                                              Bitmap.Config.ARGB_8888);
            mBitmaps[1] = Bitmap.createBitmap(colors, 0, STRIDE, WIDTH, HEIGHT,
                                              Bitmap.Config.RGB_565);
            mBitmaps[2] = Bitmap.createBitmap(colors, 0, STRIDE, WIDTH, HEIGHT,
                                              Bitmap.Config.ARGB_4444);

            // these three will have their colors set later
            mBitmaps[3] = Bitmap.createBitmap(WIDTH, HEIGHT,
                                              Bitmap.Config.ARGB_8888);
            mBitmaps[4] = Bitmap.createBitmap(WIDTH, HEIGHT,
                                              Bitmap.Config.RGB_565);
            mBitmaps[5] = Bitmap.createBitmap(WIDTH, HEIGHT,
                                              Bitmap.Config.ARGB_4444);
            for (int i = 3; i <= 5; i++) {
                mBitmaps[i].setPixels(colors, 0, STRIDE, 0, 0, WIDTH, HEIGHT);
            }
			*/
            mPaint = new Paint();
            mPaint.setDither(true);

            // now encode/decode using JPEG and PNG
            /*mJPEG = new Bitmap[mBitmaps.length];
            mPNG = new Bitmap[mBitmaps.length];
            for (int i = 0; i < mBitmaps.length; i++) {
                mJPEG[i] = codec(mBitmaps[i], Bitmap.CompressFormat.JPEG, 80);
                mPNG[i] = codec(mBitmaps[i], Bitmap.CompressFormat.PNG, 0);
            }*/
    		
	    	Thread spacewarThread = new Thread(this);
	        spacewarThread.start();
    	}
    	
    	
    	public void onDraw(Canvas canvas)
    	{	
    		Log.d("color", "drawing");
    		
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(enemyPicturemon,0,0,null);
            
            //canvas.drawBitmap(presstoplay, null, new Rect(0,height,width/2,0) , null);
            canvas.drawBitmap(presstoplay,width/2,0,null);
            
            /*for (int i = 0; i < mBitmaps.length; i++) {
                canvas.drawBitmap(mBitmaps[i], 0, 0, null);
                canvas.drawBitmap(mJPEG[i], 80, 0, null);
                canvas.drawBitmap(mPNG[i], 160, 0, null);
                canvas.translate(0, mBitmaps[i].getHeight());
            }

            // draw the color array directly, w/o craeting a bitmap object
            canvas.drawBitmap(mColors, 0, STRIDE, 0, 0, WIDTH, HEIGHT,
                              true, null);
            canvas.translate(0, HEIGHT);
            canvas.drawBitmap(mColors, 0, STRIDE, 0, 0, WIDTH, HEIGHT,
                              false, mPaint);*/

    			
    	}
    	public void run()
        {
	         while(true)
	         {
	        	 /*if (posX == targetX && posY == targetY)
	        	 {
	        		 targetX = (int)(Math.random()*width);
	        		 targetY = (int)(Math.random()*height);
	        		 speedX = 5;
	        		 speedY = 5;
	        	 }
	        	 speedX += (Math.random()-0.25)*5;
	        	 speedY += (Math.random()-0.25)*5;
	        	 
	        	 if (speedX < 5)
	        		 speedX = 5;
	        	 
	        	 if (speedY < 5)
	        		 speedY = 5;
	        	 
	        	 speedX = Math.min(speedX, Math.abs(targetX-posX));
	        	 speedY = Math.min(speedY, Math.abs(targetY-posY));
	        	 
				 posX += speedX * Math.signum(targetX - posX);
				 posY += speedY * Math.signum(targetY - posY);
				
				  if(posX < crossX+30 && posX > crossX-30){
					  if(posY < crossY+30 && posY > crossY-30)
					  {
						  CHtodraw=1;
				  	  }
				  }
				  else{
			  		  CHtodraw=0;
			  	  }*/
	         
	        	  //draw(null);
		          postInvalidate();
		          
		          try { Thread.sleep(100); }
		          catch (InterruptedException e)
		          {
		              e.printStackTrace();
		          }
	         }
        }
    	public boolean onTouchEvent (MotionEvent event)
    	{
    		/*if(posX < crossX+20 && posX > crossX-20){
				if(posY < crossY+20 && posY > crossY-20){
					posX=(int)(Math.random()*width);
		    		posY=(int)(Math.random()*height);
				}
    		}*/
    		Log.d("color", "touch");

    		
    		invalidate();
    		return true;
    	}

	    	
    	
    	
    }

}