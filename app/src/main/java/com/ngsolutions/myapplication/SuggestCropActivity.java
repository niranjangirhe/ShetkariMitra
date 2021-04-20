package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SuggestCropActivity extends AppCompatActivity {

    ImageButton backButton;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    WebView webview;
    String cropListUrl="";
    TextView cropNowText;
    Button addCropButton;
    String cropCodeInt;
    String cropNum;
    String[] crop= new String[6];
    ProgressBar progressBar;
    String userName;
    String url="https://www.soilhealth.dac.gov.in/calculator/calculator";
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_suggest_crop);
        cropNowText=findViewById(R.id.CropNowText);
        addCropButton = findViewById(R.id.AddCropBtn);
        backButton = findViewById(R.id.BackBtnWebView);
        webview = (WebView) findViewById(R.id.CropSuggestwebview);
        progressBar = findViewById(R.id.PageLoadProgreessAtCropSuggest);
        progressBar.setVisibility(View.INVISIBLE);



        webview.setWebViewClient(new WebViewClient());
        webview.setBackgroundColor(Color.TRANSPARENT);
        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setDisplayZoomControls(true);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        String userID = Auth.getCurrentUser().getUid();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMyCropActivity = new Intent(SuggestCropActivity.this, MyCropActivity.class);
                openMyCropActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMyCropActivity, 0);
            }
        });

        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(SuggestCropActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                cropNum="000000";
                if(value.exists()) {
                    cropNum = value.get("cropNum").toString();
                    crop[0]=value.get("crop0").toString();
                    crop[1]=value.get("crop1").toString();
                    crop[2]=value.get("crop2").toString();
                    crop[3]=value.get("crop3").toString();
                    crop[4]=value.get("crop4").toString();
                    crop[5]=value.get("crop5").toString();
                    }
                    try {
                        //Toast.makeText(SuggestCropActivity.this, "problem in catch", Toast.LENGTH_SHORT).show();
                        userName = value.get("userName").toString();
                    }
                    catch (Exception e)
                    {
                        userName="";
                        //Toast.makeText(SuggestCropActivity.this, "No problem in catch", Toast.LENGTH_SHORT).show();
                    }

                if(cropNum=="000000")
                {

                    for(int i=0;i<6;i++)
                    {
                        crop[i]="";
                    }
                    Log.d("niranjanHere","here due");
                }
            }

        });
        addCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag=true;
                for(int i=0;i<6;i++)
                {
                    if(crop[i].equals(cropCodeInt))
                        flag=false;
                }
                if(flag) {
                    int index = cropNum.indexOf("0");
                    if (index != -1) {
                        cropNum = cropNum.substring(0, index)
                                + "1"
                                + cropNum.substring(index + 1);
                        Log.d("niranjanCropNum", cropNum);
                        crop[index] = cropCodeInt;
                        Map<String, Object> user = new HashMap<>();
                        user.put("cropNum", cropNum);
                        user.put("crop0", crop[0]);
                        user.put("crop1", crop[1]);
                        user.put("crop2", crop[2]);
                        user.put("crop3", crop[3]);
                        user.put("crop4", crop[4]);
                        user.put("crop5", crop[5]);
                        if(!userName.isEmpty())
                            user.put("userName",userName);
                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SuggestCropActivity.this, getString(R.string.crop_added), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d("niranjanCropNumUpdate", cropNum);
                    } else {
                        Toast.makeText(SuggestCropActivity.this, getString(R.string.Limit_reached), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(SuggestCropActivity.this, getString(R.string.crop_already), Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(cropNowText.getText().toString().equals("123"))
        {
            cropNowText.setVisibility(View.GONE);
            addCropButton.setVisibility(View.GONE);
        }
        webview.loadUrl(url);
        mQueue = Volley.newRequestQueue(this);
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setIndeterminate(true);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onPageFinished(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
            {
                String tempurl = request.getUrl().toString();
                if(tempurl.contains("https://www.soilhealth.dac.gov.in/HealthCard/CropSet/BindCrop?")){
                    cropListUrl = tempurl;
                    Log.d("niranjanUrl", cropListUrl);
                    //jsonAddAll("https://raw.githubusercontent.com/niranjangirhe/shetkarimitratemprepo/main/cropdb.json");
                }
                if(tempurl.contains("https://www.soilhealth.dac.gov.in/HealthCard/CropSet/GetFGR")) {
                    int k =tempurl.indexOf("Crop_code=");
                    k=k+10;
                    String cropCode=tempurl.substring(k);
                    k=cropCode.indexOf("&");
                    cropCodeInt = cropCode.substring(0,k);
                    if(!cropListUrl.isEmpty()) {
                        jsonParse(cropListUrl,cropCodeInt);
                    }
                    Log.d("niranjanCode", cropCodeInt);

                }
                return null;
            }
        });

    }

    private void jsonParse(String url,String cropCodeInt) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++) {
                            try {
                                if(response.getJSONObject(i).getInt("Value")==Integer.parseInt(cropCodeInt)) {
                                    String cropName = response.getJSONObject(i).getString("Text");
                                    Log.d("niranjanCrop", cropName);
                                    cropNowText.setVisibility(View.VISIBLE);
                                    cropNowText.setText(cropName);
                                    addCropButton.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }
    private void jsonAddAll(String url) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++) {
                            try {
                                String cropname = response.getJSONObject(i).getString("cropname");
                                String cropmarathi = response.getJSONObject(i).getString("cropmarathi");
                                String crophindi = response.getJSONObject(i).getString("crophindi");
                                String cropcode = response.getJSONObject(i).getString("cropcode");
                                String cultivation = response.getJSONObject(i).getString("cultivation");
                                String project = response.getJSONObject(i).getString("project");
                                DocumentReference planner = fstore.collection("plan").document(cropcode);
                                Map<String, Object> def = new HashMap<>();
                                def.put("cropname", cropname);
                                def.put("cropmarathi", cropmarathi);
                                def.put("crophindi", crophindi);
                                def.put("cultivation", cultivation);
                                def.put("project", project);
                                planner.set(def).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("niranjanAdded",cropname);
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        mQueue.add(request);
    }
    @Override
    public void onBackPressed() {
        Intent openMyCropActivity = new Intent(SuggestCropActivity.this, MyCropActivity.class);
        openMyCropActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMyCropActivity, 0);
    }
    private void setLocale(String lang)
    {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences.Editor editor = getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_lang",lang);
        editor.apply();
    }
    private  void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}