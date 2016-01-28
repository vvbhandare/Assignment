package desidime.com.floatingdesidimelauncher.asyncs;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import desidime.com.floatingdesidimelauncher.interfaces.OnBackgroundTaskCompleted;
import desidime.com.floatingdesidimelauncher.utils.DesidimeConstants;

/**
 * Created by Vishal-TS on 26/01/16.
 */
public class BackgroundWork {

    public void downloadOffers(Context context, int appID, final OnBackgroundTaskCompleted listener) {

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = DesidimeConstants.IP_ADDRESS + appID + DesidimeConstants.IP_ADDRESS_EXTENSION;
        Log.v("....URL...", url);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // TODO Auto-generated method stub
                listener.onBackgroundTask(true, response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    // TODO Auto-generated method stub
                listener.onBackgroundTask(false, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                params.put("X-Desidime-Client", DesidimeConstants.DESIDIME_HEADER_KEY);
                return params;
            }
        };
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsObjRequest);
    }
}
