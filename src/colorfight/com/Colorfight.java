package colorfight.com;
import java.io.ByteArrayOutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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

    private static int screenWidth;
    private static int screenHeight;
    private static int RGB[];
    //private static final int WIDTH = 50;
    //private static final int HEIGHT = 50;
    //private static final int STRIDE = 64;   // must be >= WIDTH
    public static Boolean startedCam =false;
    public static Boolean updateSurface = true;
	public static Bitmap ownPicturemon = null;
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       requestWindowFeature(Window.FEATURE_NO_TITLE);  
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);  
       SpaceWarView gameView = new SpaceWarView(getApplicationContext());
       setContentView(gameView);
       
       screenWidth = gameView.width;
       screenHeight = gameView.height;
       RGB = new int [3];
    }
	
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
    static public void saveOwnPicturemon(Bitmap picFromCam){
    	ownPicturemon = getResizedBitmap(picFromCam,screenWidth/2,screenHeight); 
    	getRGBValues(ownPicturemon);
    }
    static public void getRGBValues(Bitmap pic){
		int r=0;
		int g=0;
		int b=0;
		int numberOfPixels = pic.getWidth()*pic.getHeight();
    	for(int x=0; x<pic.getWidth(); x++){
			for(int y=0; y<pic.getHeight(); y++){
				r += pic.getPixel(x, y) & 0x00FF0000 >> 16;
			}
		}
    	r/=numberOfPixels;
    	g/=numberOfPixels;
    	b/=numberOfPixels;
    	RGB[0]=r;
    	RGB[1]=g;
    	RGB[2]=b;
    	Log.d("color", "durchschnittlicher R wert: " + r);
    	Log.d("color", "durchschnittlicher g wert: " + g);
    	Log.d("color", "durchschnittlicher b wert: " + b);
    }
    static public Bitmap getResizedBitmap(Bitmap bm, int newWidth,int newHeight) {

	    int width = bm.getWidth();
	    int height = bm.getHeight();
	
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	
	    // create a matrix for the manipulation
	    Matrix matrix = new Matrix();
	
	    // resize the bit map
	    matrix.postScale(scaleWidth, scaleHeight);
	
	    // recreate the new Bitmap
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    
	    return resizedBitmap;
    }

    public void startCamera(){
    	Intent camIntent = new Intent (this,TakePicture.class);
    	startActivity(camIntent);
    	
    }
    
    public  class SpaceWarView extends View implements Runnable
    {
    	Bitmap enemyPicturemon;

    	Bitmap presstoplay;
    	Bitmap presstoplayScaled;
    	
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	//View current=getCurrentFocus();// findFocus();// findViewById(1);
    	private int width= display.getWidth(); 
    	private int height= display.getHeight(); 
		
        private int[]    mColors;
        private Paint    textPaint;


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
   
            textPaint = new Paint();
            textPaint.setStyle(Paint.Style.FILL);
			//turn antialiasing on
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(16);
            textPaint.setColor(Color.RED);
            textPaint.setDither(true);


    		
	    	Thread spacewarThread = new Thread(this);
	        spacewarThread.start();
    	}
    	
    	
    	public void onDraw(Canvas canvas)
    	{	
    		Log.d("color", "drawing");
    		
            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(enemyPicturemon,0,0,null);
            
            //canvas.drawBitmap(presstoplay, null, new Rect(0,height,width/2,0) , null);
            if(ownPicturemon!=null){
            	canvas.drawBitmap(ownPicturemon, width/2,0, null);
            	textPaint.setColor(Color.RED);
            	canvas.drawText("Your Red value: " + RGB[0],width/2,100, textPaint);
            	textPaint.setColor(Color.BLUE);
            	canvas.drawText("Your Green value: " + RGB[1],width/2,150, textPaint);
            	textPaint.setColor(Color.GREEN);
            	canvas.drawText("Your Blue value: " + RGB[2],width/2,200, textPaint);
    		}
        	else	
        		//canvas.drawBitmap(presstoplay, null, new Rect(0,100,100,0) , null);
        		canvas.drawBitmap(presstoplay,width/2,0,null);
           

    			
    	}
    	public void run()
        {
	         while(true)
	         {
	         
	        	  //draw(null);
	        	 if(updateSurface)
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
    		Log.d("color", "touch 1");
    		if(!startedCam){
    			startedCam=true;
    			updateSurface =false;
    			startCamera();
    		}
    		Log.d("color", "touch 2");

    		
    		invalidate();
    		return true;
    	}

	    	
    	
    	
    }

}