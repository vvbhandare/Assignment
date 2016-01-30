package desidime.com.floatingdesidimelauncher.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import desidime.com.floatingdesidimelauncher.R;
import desidime.com.floatingdesidimelauncher.clients.BasicDealsWebClient;
import desidime.com.floatingdesidimelauncher.universals.ConnectionDetector;
import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;

/**
 * Created by Vishal-TS on 29/01/16.
 */
public class DealsWebViewActivity extends AppCompatActivity
                    implements View.OnClickListener {

    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private TextView textViewHeading, textViewClose;
    private WebView webViewDeals;
    private ConnectionDetector connectionDetector;
    private Context context = DealsWebViewActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.deals_webview_screen);

        connectionDetector = new ConnectionDetector(context);
        initialiazeControllers();
    }

    private void initialiazeControllers() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        webViewDeals = (WebView) findViewById(R.id.webView_deals);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textViewHeading = (TextView) toolbar.findViewById(R.id.toolbar_title);
        textViewClose = (TextView) toolbar.findViewById(R.id.textView_close);
        textViewClose.setOnClickListener(this);
        textViewHeading.setText(getResources().getString(R.string.deals_browsing_label));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (connectionDetector.isConnectingToInternet())
            openChromClient();
        else {
                launchPopUp(getResources().getString(R.string.internet_unavailable_label));
            }
    }

    private void openChromClient() {
        // Enable Javascript
        WebSettings webSettings = webViewDeals.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        / Stop local links and redirects from opening in browser instead of WebView
        webViewDeals.setWebViewClient(new BasicDealsWebClient());
        webViewDeals.loadUrl((!getIntent().getStringExtra(DesidimeConstants.DEALS_LINK_KEY).isEmpty())
                                ? getIntent().getStringExtra(DesidimeConstants.DEALS_LINK_KEY) :
                                    DesidimeConstants.DUMMY_URL);
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
