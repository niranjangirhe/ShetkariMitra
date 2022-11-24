package com.ngsolutions.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Locale;

public class IntroductoryActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    private RequestQueue mQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);
        loadLocale();

    }
    @Override
    protected void onStart() {
        super.onStart();
        loadLocale();
        if(isConnected()) {
            Auth = FirebaseAuth.getInstance();
            FirebaseUser user = Auth.getCurrentUser();
            mQueue = null;
            mQueue = Volley.newRequestQueue(this);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, "https://raw.githubusercontent.com/niranjangirhe/shetkarimitratemprepo/main/version.json", null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try{
                                //Toast.makeText(IntroductoryActivity.this, response.getJSONObject(0).getString("version"), Toast.LENGTH_SHORT).show();
                                if(response.getJSONObject(0).getString("version").equals("2.0.0"))
                                {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (user != null) {
                                                sendToHome();
                                            } else {
                                                sendToMain();
                                            }
                                        }
                                    }, 3000);
                                }
                                else
                                {
                                    Toast.makeText(IntroductoryActivity.this, R.string.update_app, Toast.LENGTH_SHORT).show();
                                    update();
                                }

                            } catch (JSONException e) {

                            }
                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(IntroductoryActivity.this, R.string.errorfecting, Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 3000);
                }
            });
            mQueue.add(request);


        }
        else
        {
            connectDialog();
        }
    }


    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobCon = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiCon!=null && wifiCon.isConnected()) || ((mobCon!=null && mobCon.isConnected())))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void sendToHome() {
        Intent intent = new Intent(IntroductoryActivity.this,HomePage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    private void sendToMain() {
        Intent intent = new Intent(IntroductoryActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
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
    private void loadLocale()
    {
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
    private void connectDialog()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        final View namePopupView = getLayoutInflater().inflate(R.layout.popupconnect,null);
        dialogBuilder.setView(namePopupView);
        dialog =dialogBuilder.create();
        dialog.show();
        Button Quit = dialog.findViewById(R.id.buttonDialog);
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
    public void update()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        final View namePopupView = getLayoutInflater().inflate(R.layout.popupconnect,null);
        dialogBuilder.setView(namePopupView);
        dialog =dialogBuilder.create();
        dialog.show();
        Button Quit = dialog.findViewById(R.id.buttonDialog);
        LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.lottie1);
        lottieAnimationView.setAnimation(R.raw.update);
        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("https://github.com/niranjangirhe/ShetkariMitraApp/releases"));
                startActivity(viewIntent);
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
}