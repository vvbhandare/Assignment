package desidime.com.floatingdesidimelauncher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import desidime.com.floatingdesidimelauncher.services.FloatIconService;

public class ServiceLaunchingBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        Log.v("......onReceive.......", ".....Fired.....");
        Intent intentForServiceCheckIcon = new Intent(context, FloatIconService.class);
        context.startService(intentForServiceCheckIcon);
	}

}
