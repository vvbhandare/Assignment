    package desidime.com.floatingdesidimelauncher.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import desidime.com.floatingdesidimelauncher.R;
import desidime.com.floatingdesidimelauncher.activities.DisplayContentsActivity;
import desidime.com.floatingdesidimelauncher.activities.MainActivity;
import desidime.com.floatingdesidimelauncher.universals.UniversalFunction;
import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;

public class FloatIconService extends Service implements OnClickListener {
	
	private WindowManager windowManager;  
	private ImageView floatingFaceBubble;
	private Timer mTimer;
    private UniversalFunction universalFunction;
    private Context context = FloatIconService.this;
    private Handler handler;
    private int appID = 1;
    private String receivedPackageName = "";
    private boolean shouldClick = false;

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void onCreate() {
        handler = new Handler();
        super.onCreate();

        universalFunction = new UniversalFunction(context);
        mTimer = new Timer();
        mTimer.schedule(timerTask, 2000, 2 * 1000);
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        windowManager.removeView(floatingFaceBubble);
        floatingFaceBubble = null;
        windowManager = null;
        Intent intentDisplayCopouns = new Intent(context, DisplayContentsActivity.class);
        intentDisplayCopouns.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDisplayCopouns);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            Log.e("Log", "Running");
            receivedPackageName = universalFunction.printForegroundTask();
            if (!receivedPackageName.isEmpty()) {
                if ((receivedPackageName.equalsIgnoreCase(DesidimeConstants.FLIPKART_PACKAGE_NAME)) ||
                        (receivedPackageName.equalsIgnoreCase(DesidimeConstants.AMAZON_PACKAGE_NAME)) ||
                        (receivedPackageName.equalsIgnoreCase(DesidimeConstants.AMAZON_IN_PACKAGE_NAME))) {
                    appID = universalFunction.detectPackage(receivedPackageName);
                    DesidimeConstants.DEAL_ID = appID;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (windowManager != null && floatingFaceBubble != null) {
                                //nothing to do here, leave this space
                            } else {
                                formIconCoupon();
                            }
                        }
                    });
                } else {
                    if (windowManager != null && floatingFaceBubble != null) {
//                        if (floatingFaceBubble.isAttachedToWindow()) {
                            windowManager.removeView(floatingFaceBubble);
                            floatingFaceBubble = null;
                            windowManager = null;
//                        }
                    }
                }
            } else {
                if (!universalFunction.grantAccess()) {
                    mTimer.cancel();
                    timerTask.cancel();
                    stopSelf();

                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
		};

    private void formIconCoupon() {
        //a face floating bubble as imageView
        floatingFaceBubble = new ImageView(this);
        floatingFaceBubble.setImageResource(R.drawable.desidime);

        //here is all the science of params
        windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        final LayoutParams myParams = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_PHONE,
                LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x=0;
        myParams.y=100;

        // add a floatingfacebubble icon in window
        windowManager.addView(floatingFaceBubble, myParams);

        try{
            //for moving the picture on touch and slide
            floatingFaceBubble.setOnTouchListener(new View.OnTouchListener() {
                LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //remove face bubble on long press
//	              if(System.currentTimeMillis()-touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX== event.getX()){
//	                   windowManager.removeView(floatingFaceBubble);
////	                   stopSelf();
//	                   return false;
//	              }
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            shouldClick = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (shouldClick)
                                v.performClick();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            shouldClick = false;
                            break;
                    }
                    return true;
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

        floatingFaceBubble.setOnClickListener(this);
    }

    public void onDestroy() {}

}