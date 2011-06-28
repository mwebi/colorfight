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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Colorfight extends Activity {

    private static int screenWidth;
    private static int screenHeight;
    //private static final int WIDTH = 50;
    //private static final int HEIGHT = 50;
    //private static final int STRIDE = 64;   // must be >= WIDTH
    public static Boolean startedCam =false;
    public static Boolean updateSurface = true;
    public static Boolean rdyToFight = false;
	public static Bitmap ownPicturemon = null;
	public static Bitmap enemyPicturemon;

    private static int ownRGB[] = new int [3];
	private static int enemyRGB[] = new int [3];

	// armor, health, damage in %
	private static float warrior[] = {0.50f,0.15f,0.350f}; // red
	private static float rogue[] = {0.35f,0.50f,0.15f}; // green
	private static float mage[] = {0.15f,0.35f,0.50f}; // blue
	
	// warrior, rogue, mage
	private static float playerSkills[] = {0.10f,0.80f,0.10f};
	private static int playerStats[] = new int[3];
	private static int playerClass = 0;
	
	private static float enemySkills[] = {0.30f,0.30f,0.20f};
	private static int enemyStats[] = new int[3];
	private static int enemyClass = 0;
	
    
    public static int GameState;  
	
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
       
       GameState = 0;
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d("color", "onKeyDown: " + keyCode);

		if (keyCode==KeyEvent.KEYCODE_MENU)
		{
			startCamera();
			return(true);
		}
		
		return(super.onKeyDown(keyCode, event));
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
    	ownRGB = getRGBValues(ownPicturemon);
    	updateSurface = true;
    }
    
    public static void fight(Canvas canvas, Paint textPaint)
    {
    	canvas.drawColor(Color.WHITE);
    	textPaint.setColor(Color.BLACK);
    	textPaint.setStrokeWidth(1);
    	textPaint.setTextSize(12);
    	
    	float sumOfColors = ownRGB[0] + ownRGB[1] + ownRGB[2];
    	
    	float percentageOfColor[] = {
    			ownRGB[0]/sumOfColors,
    			ownRGB[1]/sumOfColors,
    			ownRGB[2]/sumOfColors
    	};
    	

    	//Log.d("fight", "---PLAYER---");
    	canvas.drawText("You" ,screenWidth/2+20,20, textPaint);
    	canvas.drawText("Skills: " + (int)(playerSkills[0]*100) + " Warrior, " + (int)(playerSkills[1]*100) + " Rogue, " + (int)(playerSkills[2]*100) + " Mage" ,screenWidth/2+20,40, textPaint);
    	canvas.drawText("Color%: " + (int)(percentageOfColor[0]*100) + ", " + (int)(percentageOfColor[1]*100) + ", " + (int)(percentageOfColor[2]*100) ,screenWidth/2+20,60, textPaint);

    	//Log.d("fight", "skills: " + playerSkills[0]*100 + " Warrior, " + playerSkills[1]*100 + " Rogue, " + playerSkills[2]*100 + " Mage");
    	//Log.d("fight", "percentageOfColor: " + percentageOfColor[0]*100 + ", " + percentageOfColor[1]*100 + ", " + percentageOfColor[2]*100);
    	
    	if (ownRGB[0] > ownRGB[1] && ownRGB[0] > ownRGB[2])
    	{
    		//Log.d("fight", "WARRIOR");
    		canvas.drawText("WARRIOR" ,screenWidth/2+20,80, textPaint);
    		playerClass = 0;
    	}
    	else if (ownRGB[1] > ownRGB[0] && ownRGB[1] > ownRGB[2])
    	{
    		//Log.d("fight", "ROGUE");
    		canvas.drawText("ROGUE" ,screenWidth/2+20,80, textPaint);
    		playerClass = 1;
    	}
    	else
    	{
    		//Log.d("fight", "MAGE");
    		canvas.drawText("MAGE" ,screenWidth/2+20,80, textPaint);
    		playerClass = 2;
    	}
    	
    	
    		

    	playerStats[0] = (int)((
    			percentageOfColor[0] * warrior[0] * playerSkills[0] +
				percentageOfColor[0] * rogue[0] * playerSkills[1] +
				percentageOfColor[0] * mage[0] * playerSkills[2]) * 100);
    	playerStats[1] = (int)((
    			percentageOfColor[1] * warrior[1] * playerSkills[0] +
    			percentageOfColor[1] * rogue[1] * playerSkills[1] +
    			percentageOfColor[1] * mage[1] * playerSkills[2]) * 100);
    	playerStats[2] = (int)((
    			percentageOfColor[2] * warrior[2] * playerSkills[0] +
    			percentageOfColor[2] * rogue[2] * playerSkills[1] + 
    			percentageOfColor[2] * mage[2] * playerSkills[2]) * 100);
    	
    	
    	//Log.d("fight", "---ENEMY---");
    	canvas.drawText("Enemy" ,20,20, textPaint);

    	sumOfColors = enemyRGB[0] + enemyRGB[1] + enemyRGB[2];
    	
		percentageOfColor[0] = enemyRGB[0]/sumOfColors;
		percentageOfColor[1] = enemyRGB[1]/sumOfColors;
		percentageOfColor[2] = enemyRGB[2]/sumOfColors;


    	//Log.d("fight", "skills: " + enemySkills[0]*100 + " Warrior, " + enemySkills[1]*100 + " Rogue, " + enemySkills[2]*100 + " Mage");
    	//Log.d("fight", "percentageOfColor: " + percentageOfColor[0]*100 + ", " + percentageOfColor[1]*100 + ", " + percentageOfColor[2]*100);
		canvas.drawText("skills: " + (int)(enemySkills[0]*100) + " Warrior, " + (int)(enemySkills[1]*100) + " Rogue, " + (int)(enemySkills[2]*100) + " Mage" ,20,40, textPaint);
		canvas.drawText("percentageOfColor: " + (int)(percentageOfColor[0]*100) + ", " + (int)(percentageOfColor[1]*100) + ", " + (int)(percentageOfColor[2]*100) ,20,60, textPaint);
		
		
    	if (enemyRGB[0] > enemyRGB[1] && enemyRGB[0] > enemyRGB[2])
    	{
    		//Log.d("fight", "WARRIOR");
    		canvas.drawText("WARRIOR",20,80, textPaint);
    		enemyClass = 0;
    	}
    	else if (enemyRGB[1] > enemyRGB[0] && enemyRGB[1] > enemyRGB[2])
    	{
    		//Log.d("fight", "ROGUE");
    		canvas.drawText("ROGUE",20,80, textPaint);
    		enemyClass = 1;
    	}
    	else
    	{
    		//Log.d("fight", "MAGE");
    		canvas.drawText("MAGE",20,80, textPaint);
    		enemyClass = 2;
    	}
		
		enemyStats[0] = (int)((
				percentageOfColor[0] * warrior[0] * enemySkills[0] +
				percentageOfColor[0] * rogue[0] * enemySkills[1] +
				percentageOfColor[0] * mage[0] * enemySkills[2]) * 100);
		enemyStats[1] = (int)((
			percentageOfColor[1] * warrior[1] * enemySkills[0] +
			percentageOfColor[1] * rogue[1] * enemySkills[1] +
			percentageOfColor[1] * mage[1] * enemySkills[2]) * 100);
		enemyStats[2] = (int)((
			percentageOfColor[2] * warrior[2] * enemySkills[0] +
			percentageOfColor[2] * rogue[2] * enemySkills[1] + 
			percentageOfColor[2] * mage[2] * enemySkills[2]) * 100);
		

		playerStats[1] *= 10;
		enemyStats[1] *= 10;
		
		//Log.d("fight", "playerStats: " + playerStats[0] + " Armor, " + playerStats[1] + " Health, " + playerStats[2] + " Damage");
    	//Log.d("fight", "enemyStats: " + enemyStats[0] + " Armor, " + enemyStats[1] + " Health, " + enemyStats[2] + " Damage");
    	
    	canvas.drawText("playerStats: " + playerStats[0] + " Armor, " + playerStats[1] + " Health, " + playerStats[2] + " Damage",screenWidth/2+20,100, textPaint);
    	canvas.drawText("enemyStats: " + enemyStats[0] + " Armor, " + enemyStats[1] + " Health, " + enemyStats[2] + " Damage",20,100, textPaint);
    	
    	int round = 1;
    	int damageToPlayer;
    	int damageToEnemy;


		damageToPlayer = (int)(enemyStats[2] - enemyStats[2] / 100.0f  * (playerStats[0] / 31.0f * 100.0f));
		damageToEnemy = (int)(playerStats[2] - playerStats[2] / 100.0f  * (enemyStats[0] / 31.0f * 100.0f));
		
		if (playerClass == enemyClass)
		{
			//Log.d("fight", "same classes no changes");
			canvas.drawText("same classes no changes",screenWidth/2-40,120, textPaint);
		}
		else if ( 	(playerClass == 0 && enemyClass == 1) ||
    			(playerClass == 1 && enemyClass == 2) ||
    			(playerClass == 2 && enemyClass == 0)
    	)
    	{
    		//Log.d("fight", "player is in advantage -> damageToEnemy*2");
    		canvas.drawText("player is in advantage -> damageToEnemy*2",screenWidth/2-40,120, textPaint);
    		damageToEnemy *= 2;
    	}
    	else
    	{
    		//Log.d("fight", "player is in disadvantage -> damageToEnemy/2");
    		canvas.drawText("player is in disadvantage -> damageToEnemy/2",screenWidth/2-40,120, textPaint);
    		damageToEnemy /= 2;
    	}
		
		
		//Log.d("fight", "damageToPlayer: " + damageToPlayer);
		canvas.drawText("damageToPlayer: "+ damageToPlayer,screenWidth/2-40,140, textPaint);
		//Log.d("fight", "damageToEnemy: " + damageToEnemy);
		canvas.drawText("damageToEnemy: " + damageToEnemy,screenWidth/2-40,160, textPaint);
		
    	while (true)
    	{
    		playerStats[1] -= damageToPlayer;
    		enemyStats[1] -= damageToEnemy;
    		
    		
    		if (playerStats[1] <= 0 || enemyStats[1] <= 0)
    			break;
    		else
    			round++;
    	}
    	

		//Log.d("fight", "### ROUND " + round + " ###");
		//Log.d("fight", "playerStats: " + playerStats[0] + " Armor, " + playerStats[1] + " Health, " + playerStats[2] + " Damage");
    	//Log.d("fight", "enemyStats: " + enemyStats[0] + " Armor, " + enemyStats[1] + " Health, " + enemyStats[2] + " Damage");

    	canvas.drawText("ROUND " + round ,screenWidth/2,180, textPaint);
    	canvas.drawText("playerStats: " + playerStats[0] + " Armor, " + playerStats[1] + " Health, " + playerStats[2] + " Damage" + round ,screenWidth/2+20,200, textPaint);
    	canvas.drawText("enemyStats: " + enemyStats[0] + " Armor, " + enemyStats[1] + " Health, " + enemyStats[2] + " Damage" + round ,20,200, textPaint);
    	
    	
    	for (int i = 0; i < 3; i++)
    	{
    		if (i == playerClass)
    			playerSkills[i] += 0.02;
    		else
    			playerSkills[i] -= 0.01;
    		
    		if (i == enemyClass)
    			enemySkills[i] += 0.02;
    		else
    			enemySkills[i] -= 0.01;
    	}
    	
    	//Log.d("fight", "---------------------------------------------------------");
    	
    	canvas.drawText("Click to fight next round" ,screenWidth/2-20,280, textPaint);
    	//updateSurface = true;
    	
    }
    
    static public int[] getRGBValues(Bitmap pic){
		int r=0;
		int g=0;
		int b=0;
		int numberOfPixels = pic.getWidth()*pic.getHeight();
		int[] RGB = new int[3];
    	for(int x=0; x<pic.getWidth(); x++){
			for(int y=0; y<pic.getHeight(); y++){
				//r += pic.getPixel(x, y) & 0x00FF0000 >> 16;
				r += (pic.getPixel(x, y) >> 16) & 0xff;
				g += (pic.getPixel(x, y) >> 8) & 0xff;
                b += pic.getPixel(x, y) & 0xff;
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
    	
    	return RGB;
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
    	Bitmap presstoplay;
    	Bitmap presstoplayScaled;
    	
    	Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    	//View current=getCurrentFocus();// findFocus();// findViewById(1);
    	private int width= display.getWidth(); 
    	private int height= display.getHeight(); 
		
        private int[]    mColors;
        private Paint    textPaint;
        
        private Boolean didFight = false;

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
            
            enemyRGB = getRGBValues(enemyPicturemon);
            
            presstoplay = BitmapFactory.decodeResource(getResources(), R.drawable.presstoplay);
            //presstoplayScaled = presstoplayScaled.createScaledBitmap (presstoplay,  width/2, height, false);
            
            Log.d("color", "in constr 3");
   
            textPaint = new Paint();
            textPaint.setStyle(Paint.Style.STROKE);
			//turn antialiasing on
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(16);
            textPaint.setColor(Color.RED);
            textPaint.setStrokeWidth(2);
            textPaint.setDither(true);


    		
	    	Thread spacewarThread = new Thread(this);
	        spacewarThread.start();
    	}
    	
    	
    	public void onDraw(Canvas canvas)
    	{	
    		//Log.d("color", "drawing");
    		if(GameState == 0){
	            canvas.drawColor(Color.WHITE);
	            canvas.drawBitmap(enemyPicturemon,0,0,null);
	        	
	            textPaint.setColor(Color.RED);
	        	canvas.drawText("Enemy Red value: " + enemyRGB[0],20,100, textPaint);
	        	textPaint.setColor(Color.GREEN);
	        	canvas.drawText("Enemy Green value: " + enemyRGB[1],20,130, textPaint);
	        	textPaint.setColor(Color.BLUE);
	        	canvas.drawText("Enemy Blue value: " + enemyRGB[2],20,160, textPaint);
	        	
	            //canvas.drawBitmap(presstoplay, null, new Rect(0,height,width/2,0) , null);
	            if(ownPicturemon!=null){
	            	canvas.drawBitmap(ownPicturemon, width/2,0, null);
	            	textPaint.setColor(Color.RED);
	            	canvas.drawText("Your Red value: " + ownRGB[0],width/2+20,100, textPaint);
	            	textPaint.setColor(Color.GREEN);
	            	canvas.drawText("Your Green value: " + ownRGB[1],width/2+20,130, textPaint);
	            	textPaint.setColor(Color.BLUE);
	            	canvas.drawText("Your Blue value: " + ownRGB[2],width/2+20,160, textPaint);
	            	
	            	textPaint.setColor(Color.LTGRAY);
	            	canvas.drawText("Touch again to continue fighting" ,screenWidth/2-70,280, textPaint);
	    		}
	        	else	
	        		//canvas.drawBitmap(presstoplay, null, new Rect(0,100,100,0) , null);
	        		canvas.drawBitmap(presstoplay,width/2,0,null);
    		}
    		else if(GameState == 2){
    			if(!didFight){
    				fight(canvas, textPaint);
    				updateSurface = false;
        			//didFight = true;
    			}
    			//postInvalidate();
    			//updateSurface = false;
    		}

    			
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
    		else if(ownPicturemon!=null)
    			GameState = 2;
    		
    		Log.d("color", "touch 2");

    		
    		invalidate();
    		return true;
    	}

	    	
    	
    	
    }

}