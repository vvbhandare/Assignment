package desidime.com.floatingdesidimelauncher.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import desidime.com.floatingdesidimelauncher.R;
import desidime.com.floatingdesidimelauncher.receivers.ServiceLaunchingBroadcastReceiver;
import desidime.com.floatingdesidimelauncher.services.FloatIconService;
import desidime.com.floatingdesidimelauncher.universals.UniversalFunction;
import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;


public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder builder;
    private Context context = MainActivity.this;
    private UniversalFunction universalFunction;
    private CoordinatorLayout coordinatorLayout;
    private Snackbar snackbar;
    private Toolbar toolbar;
    private TextView textViewHeading, textViewClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        universalFunction = new UniversalFunction(context);
        initializeControllers();
    }

    private void initializeControllers() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewHeading = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textViewClose = (TextView) toolbar.findViewById(R.id.textView_close);
        textViewClose.setVisibility(View.GONE);
        textViewHeading.setText(getResources().getString(R.string.welcome_label));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (universalFunction.checkDeviceVersion()) {
            if (!universalFunction.grantAccess()) {
                showDialogForPermissionDetails();
            } else {
                launchPopUp(getResources().getString(R.string.usage_access_permission_already_on_label), true);
            }
        } else {
            launchPopUp(getResources().getString(R.string.usage_access_permission_not_required_label), true);
        }
    }

    private void launchPopUp(String message, final boolean flagCheckForPermission) {
        snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.ok_label),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                        if (!flagCheckForPermission)
                                            showDialogForPermissionDetails();
                                        else
                                            initiateService();
                                    }
                                });
        snackbar.show();
    }

    private void initiateService() {
        Intent intentForServiceCheckIcon = new Intent(context, FloatIconService.class);
        context.startService(intentForServiceCheckIcon);
        finish();
    }

    private void showDialogForPermissionDetails() {
        builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.dialog_title_label));
        builder.setMessage(getResources().getString(R.string.dialog_description_label));
        builder.setCancelable(false);
        builder.setPositiveButton(getResources().getString(R.string.dialog_permit_label),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        requestPermission();
                    }
                });
        builder.setNegativeButton(getResources().getString(R.string.dialog_cancel_label),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        dialog.dismiss();
                        launchPopUp(getResources().getString(R.string.receiving_notification_off_label), false);
                    }
                });
        builder.show();
    }

    private void requestPermission() {
        startActivityForResult(
                new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),
                DesidimeConstants.MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case DesidimeConstants.MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (universalFunction.grantAccess()){
                    initiateAppOpenCheckEvent();
                    launchPopUp(getResources().getString(R.string.receiving_notification_on_label), true);
                } else {
                    requestPermission();
                }
                break;
        }
    }

    private void initiateAppOpenCheckEvent() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(MainActivity.this, ServiceLaunchingBroadcastReceiver.class);
        sendBroadcast(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
