package colorfight.com;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
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
    
    public class SpaceWarView extends SurfaceView implements Runnable
    {
    	Bitmap tieFighter;
    	Bitmap tieFighterGreen;
    	Bitmap cross;
    	Bitmap crossGreen;
    	
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	//View current=getCurrentFocus();// findFocus();// findViewById(1);
    	private int width= display.getWidth(); 
    	private int height= display.getHeight(); 

    	private int posX=width/2;
		private int posY=height/2;
		private int crossX=posX;
		private int crossY=posY;
		private int crossSpeedX=0;
		private int crossSpeedY=0;
		private int targetX = posX;
		private int targetY = posY;
		private int speedX = 0;
		private int speedY = 0;
		
		
		int CHtodraw=0;
		
		
    	public SpaceWarView(Context context)
    	{
    		super(context);
	    	Thread spacewarThread = new Thread(this);
	        spacewarThread.start();
    	}
    	
    	
    	public void draw(Canvas canvas)
    	{	
    		
    		if(CHtodraw==0){
    			canvas.drawBitmap(tieFighter,posX-30, posY-19, null);
    			canvas.drawBitmap(cross, crossX-104, crossY-105, null);
    		}
    		else if(CHtodraw==1){
    			canvas.drawBitmap(tieFighterGreen,posX-30, posY-19, null);
    			canvas.drawBitmap(crossGreen, crossX-104, crossY-105, null);
    		}
    			
    	}
    	public void run()
        {
	         while(true)
	         {
	        	 if (posX == targetX && posY == targetY)
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
			  	  }
	         
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
    		if(posX < crossX+20 && posX > crossX-20){
				if(posY < crossY+20 && posY > crossY-20){
					posX=(int)(Math.random()*width);
		    		posY=(int)(Math.random()*height);
				}
    		}

    		
    		invalidate();
    		return true;
    	}

	    	
    	
    	
    }

}