package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
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
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class IntroductoryActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
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
            fstore = FirebaseFirestore.getInstance();
            DocumentReference documentReference = fstore.collection("Mics").document("version info");
            documentReference.addSnapshotListener(IntroductoryActivity.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(value.get("version").toString().equals("1.24.4"))
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
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                               finish();
                            }
                        }, 3000);
                    }
                }
            });
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
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
    }
}