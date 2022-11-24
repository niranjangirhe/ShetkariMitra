package com.ngsolutions.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

import static com.ngsolutions.myapplication.Setting_Activity.restartActivity;

public class MainActivity extends AppCompatActivity {
    Button loginButton,changelang;
    FirebaseAuth Auth;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        loginButton = findViewById(R.id.LoginBtn);
        changelang = findViewById(R.id.ChangeMainLangBtn);
        Auth = FirebaseAuth.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SendOTPActivity.class);
                startActivity(intent);
            }
        });
        changelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                langDialog();
            }
        });
    }
    private void langDialog()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        final View langPopupView = getLayoutInflater().inflate(R.layout.popuplang,null);
        RadioButton r1=langPopupView.findViewById(R.id.radioButton);
        RadioButton r2=langPopupView.findViewById(R.id.radioButton2);
        RadioButton r3=langPopupView.findViewById(R.id.radioButton3);
        RadioButton r4=langPopupView.findViewById(R.id.radioButton4);
        RadioButton r5=langPopupView.findViewById(R.id.radioButton5);
        RadioButton r6=langPopupView.findViewById(R.id.radioButton6);

        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        if(lang.equals("mr"))
        {
            r1.setChecked(true);
        }
        else if(lang.equals("hi"))
        {
            r2.setChecked(true);
        }
        else if(lang.equals("gu"))
        {
            r4.setChecked(true);
        }
        else if(lang.equals("pa"))
        {
            r5.setChecked(true);
        }
        else if(lang.equals(("ta")))
        {
            r6.setChecked(true);
        }
        else
        {
            r3.setChecked(true);
        }
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                r6.setChecked(false);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                //r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                r6.setChecked(false);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                r2.setChecked(false);
               // r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
                r6.setChecked(false);
            }
        });
        r4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                //r4.setChecked(false);
                r5.setChecked(false);
                r6.setChecked(false);
            }
        });
        r5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
               // r5.setChecked(false);
                r6.setChecked(false);
            }
        });
        r6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r1.setChecked(false);
                r2.setChecked(false);
                r3.setChecked(false);
                r4.setChecked(false);
                r5.setChecked(false);
               // r6.setChecked(false);
            }
        });
        ImageButton savelang= langPopupView.findViewById(R.id.SaveLangBtn);
        dialogBuilder.setView(langPopupView);
        dialog =dialogBuilder.create();
        dialog.show();
        savelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(r1.isChecked()) {
                    setLocale("mr");
                    Toast.makeText(MainActivity.this, "भाषा मराठीत बदलली", Toast.LENGTH_SHORT).show();
                }
                else if(r2.isChecked()) {
                    setLocale("hi");
                    Toast.makeText(MainActivity.this, "भाषा बदली हिंदी में", Toast.LENGTH_SHORT).show();
                }
                else if(r3.isChecked()) {
                    setLocale("en");
                    Toast.makeText(MainActivity.this, "Language Changed to English", Toast.LENGTH_SHORT).show();
                }
                else if(r4.isChecked()) {
                    setLocale("gu");
                    Toast.makeText(MainActivity.this, "Language Changed to Krio", Toast.LENGTH_SHORT).show();
                }
                else if(r5.isChecked()) {
                    setLocale("pa");
                    Toast.makeText(MainActivity.this, "Iyiero dholuo", Toast.LENGTH_SHORT).show();
                }
                else if(r6.isChecked()) {
                    setLocale("ta");
                    Toast.makeText(MainActivity.this, "Language Changed to Luganda", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
                restartActivity(MainActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLocale();
        FirebaseUser user = Auth.getCurrentUser();
        if(user!=null){
            sendToHome();
        }
    }
    private void sendToHome() {
        Intent intent = new Intent(MainActivity.this,HomePage.class);
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
}