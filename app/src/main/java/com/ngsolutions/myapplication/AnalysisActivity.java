package com.ngsolutions.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class AnalysisActivity extends AppCompatActivity {

    double Lat,Long;
    Uri uri;
    int Type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);


        Lat = getIntent().getExtras().getDouble("Lat", 0);
        Long = getIntent().getExtras().getDouble("Long", 0);
        uri = (Uri) getIntent().getExtras().get("imageUri");
        Type = getIntent().getExtras().getInt("Type");


        //Toast.makeText(this, "Loc "+Lat+", "+Long, Toast.LENGTH_SHORT).show();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = "https://soil-health.herokuapp.com/post?Lat=" + Double.toString(Lat) + "&Long=" + Double.toString(Long) + "&date=2022-07-01&end_dt=2022-07-30";
        //String URL = "https://soil-health.herokuapp.com/post?Lat=19.704656&Long=74.248489&date=2022-07-01&end_dt=2022-07-30";
        JSONObject jsonBody = new JSONObject();
        final String mRequestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i("LOG_VOLLEY", response);
                Toast.makeText(AnalysisActivity.this, "Report Received", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(AnalysisActivity.this,TestResultActivity.class);
                i.putExtra("Res",response);
                i.putExtra("Type",Type);
                i.putExtra("Lat",Lat);
                i.putExtra("Long",Long);
                i.putExtra("imageUri",uri);
                startActivity(i);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LOG_VOLLEY", error.toString());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                    return null;
                }
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

}