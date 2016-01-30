package desidime.com.floatingdesidimelauncher.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import desidime.com.floatingdesidimelauncher.R;
import desidime.com.floatingdesidimelauncher.adapters.CustomCopounAdapter;
import desidime.com.floatingdesidimelauncher.asyncs.BackgroundWork;
import desidime.com.floatingdesidimelauncher.helpers.DatabaseHandler;
import desidime.com.floatingdesidimelauncher.interfaces.OnBackgroundTaskCompleted;
import desidime.com.floatingdesidimelauncher.interfaces.RecyclerItemClickListener;
import desidime.com.floatingdesidimelauncher.models.CopounInfo;
import desidime.com.floatingdesidimelauncher.universals.ConnectionDetector;
import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;

/**
 * Created by Vishal-TS on 25/01/16.
 */
public class DisplayContentsActivity extends AppCompatActivity implements
                    OnBackgroundTaskCompleted, View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerViewDisplayCopouns;
    private ProgressBar progressBarBeforeCopounsLoad;
    private static final String TAG = "DisplayContentsActivity";
    private Context context = DisplayContentsActivity.this;
    private ArrayList<CopounInfo> arrayListCopouns = new ArrayList<CopounInfo>();
    private CustomCopounAdapter customCopounAdapter;
    private ConnectionDetector connectionDetector;
    private int previousAppID = 1;
    private DatabaseHandler databaseHandler;
    private Toolbar toolbar;
    private TextView textViewHeading, textViewClose;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_contents_screen);

        connectionDetector = new ConnectionDetector(context);
        databaseHandler = new DatabaseHandler(context);
        initialiazeControllers();
    }

    private void initialiazeControllers() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        recyclerViewDisplayCopouns = (RecyclerView) findViewById(R.id.recyclerView_displaying_copouns);
        progressBarBeforeCopounsLoad = (ProgressBar) findViewById(R.id.progressBar_before_copouns_load);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewHeading = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textViewClose = (TextView) toolbar.findViewById(R.id.textView_close);
        textViewClose.setOnClickListener(this);
        setHeaderTitle(DesidimeConstants.DEAL_ID);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        recyclerViewDisplayCopouns.setLayoutManager(new LinearLayoutManager(context));
        progressBarBeforeCopounsLoad.setVisibility(View.VISIBLE);

        if (connectionDetector.isConnectingToInternet())
            downloadCopouns();
        else {
            if (databaseHandler.getDealsCount(DesidimeConstants.DEAL_ID) > 0)
                loadData();
            else {
                progressBarBeforeCopounsLoad.setVisibility(View.GONE);
                launchPopUp(getResources().getString(R.string.empty_list_error_label));
            }
        }

        setListener();
    }

    private void setListener() {
        recyclerViewDisplayCopouns.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        Log.v("...GOT THE CLICK....", "" + arrayListCopouns.get(position).getCopounLink());
                        Intent intentDealBrowsing = new Intent(context, DealsWebViewActivity.class);
                        intentDealBrowsing.putExtra(DesidimeConstants.DEALS_LINK_KEY,
                                                arrayListCopouns.get(position).getCopounLink());
                        startActivity(intentDealBrowsing);
                    }
                })
        );
    }

    private void setHeaderTitle(int deal_id) {
        textViewHeading.setText((deal_id == 1) ?
                                 getResources().getString(R.string.flipkart_app_heading_label) :
                                 getResources().getString(R.string.amazon_app_hading_label));
    }

    private void clearDealList() {
        arrayListCopouns.clear();
    }

    private void downloadCopouns() {
        clearDealList();
        new BackgroundWork().downloadOffers(context, DesidimeConstants.DEAL_ID, this);
    }

    private void loadData() {
        clearDealList();
        for(CopounInfo copounInfo : databaseHandler.getAllDeals(DesidimeConstants.DEAL_ID))
            arrayListCopouns.add(copounInfo);
        setData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setHeaderTitle(DesidimeConstants.DEAL_ID);
        if (connectionDetector.isConnectingToInternet()) {
            if (previousAppID != DesidimeConstants.DEAL_ID) {
                progressBarBeforeCopounsLoad.setVisibility(View.VISIBLE);
                recyclerViewDisplayCopouns.setVisibility(View.INVISIBLE);
                downloadCopouns();
            }
        } else {
            if (previousAppID != DesidimeConstants.DEAL_ID)
                if (databaseHandler.getDealsCount(DesidimeConstants.DEAL_ID) > 0)
                    loadData();
                else {
                    arrayListCopouns.clear();
                    progressBarBeforeCopounsLoad.setVisibility(View.GONE);
                    launchPopUp(getResources().getString(R.string.empty_list_error_label));
                }
        }
    }

    @Override
    public void onBackgroundTask(boolean flagTaskDone, String response) {
        progressBarBeforeCopounsLoad.setVisibility(View.GONE);
        recyclerViewDisplayCopouns.setVisibility(View.VISIBLE);
        if (flagTaskDone) {
            parseJSONResponse(response);
        } else {
            launchPopUp(getResources().getString(R.string.technical_error_label));
        }
    }

    private void parseJSONResponse(String response) {
        JSONObject jsonObjectMain;
        JSONArray jsonArrayDeals, jsonArrayCopouns, jsonArrayTopics;
        try {
            jsonObjectMain = new JSONObject(response);
            if (jsonObjectMain.has(DesidimeConstants.DEALS_KEY)) {
                jsonArrayDeals = jsonObjectMain.getJSONArray(DesidimeConstants.DEALS_KEY);
                databaseHandler.deleteAll(DesidimeConstants.DEAL_ID);
                for (int i = 0; i < jsonArrayDeals.length(); i++) {
                    CopounInfo copounInfo = new CopounInfo();
                    copounInfo.setCopounName(jsonArrayDeals.getJSONObject(i).getString(DesidimeConstants.TITLE_KEY));
                    copounInfo.setCopounIcon(jsonArrayDeals.getJSONObject(i).getString(DesidimeConstants.ICON_KEY));
                    copounInfo.setOfferID(DesidimeConstants.DEAL_ID);
                    copounInfo.setCopounLink(jsonArrayDeals.getJSONObject(i).getString(DesidimeConstants.DEALS_LINK_KEY));
                    arrayListCopouns.add(copounInfo);
                    databaseHandler.addDeal(copounInfo);
                }

                if (jsonObjectMain.has(DesidimeConstants.COPOUNS_KEY)) {
                    jsonArrayCopouns = jsonObjectMain.getJSONArray(DesidimeConstants.COPOUNS_KEY);
                    for (int i = 0; i < jsonArrayCopouns.length(); i++) {
                        CopounInfo copounInfo = new CopounInfo();
                        copounInfo.setCopounName(jsonArrayCopouns.getJSONObject(i).getString(DesidimeConstants.COPOUN_TITLE_KEY));
                        copounInfo.setCopounIcon(jsonArrayCopouns.getJSONObject(i).getString(DesidimeConstants.ICON_KEY));
                        copounInfo.setOfferID(DesidimeConstants.DEAL_ID);
                        copounInfo.setCopounLink(jsonArrayCopouns.getJSONObject(i).getString(DesidimeConstants.DEALS_LINK_KEY));
                        arrayListCopouns.add(copounInfo);
                        databaseHandler.addDeal(copounInfo);
                    }
                }

                if (jsonObjectMain.has(DesidimeConstants.TOPICS_KEY)) {
                    jsonArrayTopics = jsonObjectMain.getJSONArray(DesidimeConstants.TOPICS_KEY);
                    for (int i = 0; i < jsonArrayTopics.length(); i++) {
                        CopounInfo copounInfo = new CopounInfo();
                        copounInfo.setCopounName(jsonArrayTopics.getJSONObject(i).getString(DesidimeConstants.TITLE_KEY));
                        copounInfo.setCopounIcon(jsonArrayTopics.getJSONObject(i)
                                                    .getJSONObject(DesidimeConstants.TOPICS_USER_KEY)
                                                    .getString(DesidimeConstants.ICON_KEY));
                        copounInfo.setOfferID(DesidimeConstants.DEAL_ID);
                        copounInfo.setCopounLink("http://www.desidime.com/stores");
                        arrayListCopouns.add(copounInfo);
                        databaseHandler.addDeal(copounInfo);
                    }
                }

                setData();
            } else {
                if (!connectionDetector.isConnectingToInternet())
                    launchPopUp(getResources().getString(R.string.internet_unavailable_label));
                else
                    launchPopUp(getResources().getString(R.string.technical_error_label));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            progressBarBeforeCopounsLoad.setVisibility(View.GONE);
            launchPopUp(getResources().getString(R.string.technical_error_label));
        }
    }

    private void setData() {
        progressBarBeforeCopounsLoad.setVisibility(View.GONE);
        previousAppID = DesidimeConstants.DEAL_ID;
        customCopounAdapter = new CustomCopounAdapter(context, arrayListCopouns);
        recyclerViewDisplayCopouns.setAdapter(customCopounAdapter);
    }

    private void launchPopUp(String message) {
        final Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textView_close:
                finish();
                break;
        }
    }
}
