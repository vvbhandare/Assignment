package desidime.com.floatingdesidimelauncher.universals;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;

/**
 * Created by Vishal-TS on 25/01/16.
 */
public class UniversalFunction {

    private Context context;

    public UniversalFunction(Context context) {
        this.context = context;
    }

    public boolean checkDeviceVersion() {
        return ((android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) ? true : false);
    }

    public boolean grantAccess() {
        AppOpsManager appOps = (AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;
    }

    public String printForegroundTask() {
        String currentApp = "";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0); // get list of running tasks
            currentApp = foregroundTaskInfo.topActivity
                    .getPackageName();
        }

        Log.e(" Current App in foreground is: ", currentApp);
        return currentApp;
    }

    public int detectPackage(String receivedPackageName) {
        if (receivedPackageName.equalsIgnoreCase(DesidimeConstants.arrayForApps[0]))
            return DesidimeConstants.FLIPKART_APP_ID;
        else if (receivedPackageName.equalsIgnoreCase(DesidimeConstants.arrayForApps[1]) ||
                receivedPackageName.equalsIgnoreCase(DesidimeConstants.arrayForApps[2]))
            return DesidimeConstants.AMAZON_APP_ID;
        return DesidimeConstants.FLIPKART_APP_ID;
    }
}
