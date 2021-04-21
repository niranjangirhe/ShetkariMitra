package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Setting_Activity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    Button logOutButton;
    ImageButton backButton;
    ProgressBar progressBar;
    Button editNameButton, changelang;
    ImageButton pencilButton;
    EditText yourName;
    String userID,userName;
    boolean datafound=false;
    boolean loggedin=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_);
        yourName = findViewById(R.id.YourName);
        editNameButton = findViewById(R.id.EditNameBtn);
        pencilButton = findViewById(R.id.Pencil);
        yourName.setText("");
        yourName.setHint("Enter You Name here");
        yourName.setFocusable(true);
        yourName.setFocusableInTouchMode(true);
        yourName.setInputType(1);
        yourName.setBackgroundTintList(Setting_Activity.this.getResources().getColorStateList(R.color.green));
        editNameButton.setVisibility(View.VISIBLE);
        pencilButton.setVisibility(View.INVISIBLE);
        changelang = findViewById(R.id.ChangeLangBtn);
        ConstraintLayout cl = findViewById(R.id.SettingPage);


        logOutButton = findViewById(R.id.LogOutBtn);
        progressBar = findViewById(R.id.ProgressBar3);
        backButton = findViewById(R.id.BackBtn4);
        Auth = FirebaseAuth.getInstance();

        progressBar.setVisibility(View.GONE);
       // Auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(Setting_Activity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(!loggedin) {
                    return;
                }
                if (value.exists()) {
                    try {
                        datafound=true;
                        userName = value.get("userName").toString();
                        yourName.setText(userName);
                        yourName.setFocusable(false);
                        yourName.setFocusableInTouchMode(false);
                        yourName.setInputType(0);
                        pencilButton.setVisibility(View.VISIBLE);
                        editNameButton.setVisibility(View.INVISIBLE);
                        yourName.setBackgroundTintList(Setting_Activity.this.getResources().getColorStateList(R.color.transparent));
                    } catch (Exception e) {
                        yourName.setText("");
                        yourName.setHint("Enter You Name here");
                        yourName.setFocusable(true);
                        yourName.setFocusableInTouchMode(true);
                        yourName.setInputType(1);
                        yourName.setBackgroundTintList(Setting_Activity.this.getResources().getColorStateList(R.color.green));
                        editNameButton.setVisibility(View.VISIBLE);
                        pencilButton.setVisibility(View.INVISIBLE);
                    }
                }
                else
                {
                    datafound=false;
                }
            }
        });
        editNameButton.setMovementMethod(null);
        editNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentReference.addSnapshotListener(Setting_Activity.this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(!loggedin)
                            return;
                        Map<String, Object> user = new HashMap<>();
                        if (value.exists()) {
                            try {
                                userName = value.get("userName").toString();
                            } catch (Exception e) {
                                userName = "";

                            }
                        }
                        else
                        {
                            userName = "";
                        }
                        if(userName.isEmpty() && yourName.getText().toString().isEmpty()) {
                            Toast.makeText(Setting_Activity.this, "Enter Valid Name", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if(!yourName.getText().toString().isEmpty()) {
                            userName = yourName.getText().toString();
                        }
                        user.put("userName",userName);
                        user.put("token", FirebaseInstanceId.getInstance().getToken());
                        DocumentReference addname = fstore.collection("users").document(userID);
                        if(datafound) {
                            addname.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Setting_Activity.this, getString(R.string.nameSavedToYourProfile), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else
                        {
                            user.put("cropNum", "000000");
                            user.put("crop0", "");
                            user.put("crop1", "");
                            user.put("crop2", "");
                            user.put("crop3", "");
                            user.put("crop4", "");
                            user.put("crop5", "");
                            user.put("token", FirebaseInstanceId.getInstance().getToken());
                            addname.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Setting_Activity.this, getString(R.string.nameSavedToYourProfile), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        editNameButton.setVisibility(View.INVISIBLE);
                        pencilButton.setVisibility(View.VISIBLE);

                    }
                });

            }
        });
        changelang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                langDialog();
            }
        });

        pencilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourName.setText("");
                yourName.setHint(userName);
                yourName.setFocusable(true);
                yourName.setFocusableInTouchMode(true);
                yourName.setInputType(1);
                yourName.setBackgroundTintList(Setting_Activity.this.getResources().getColorStateList(R.color.green));
                editNameButton.setVisibility(View.VISIBLE);
                pencilButton.setVisibility(View.INVISIBLE);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(Setting_Activity.this, HomePage.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                logOutButton.setVisibility(View.INVISIBLE);
                loggedin=false;
                cl.removeView(yourName);
                Auth.signOut();
                sendToWelcome();
            }
        });
    }
    private void sendToWelcome() {
        Intent intent = new Intent(Setting_Activity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        logOutButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        finish();
    }
    private void langDialog()
    {
        dialogBuilder = new AlertDialog.Builder(this);
        final View langPopupView = getLayoutInflater().inflate(R.layout.popuplang,null);
        RadioButton r1=langPopupView.findViewById(R.id.radioButton);
        RadioButton r2=langPopupView.findViewById(R.id.radioButton2);
        RadioButton r3=langPopupView.findViewById(R.id.radioButton3);
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_lang","en");
        if(lang.contains("m"))
        {
            r1.setChecked(true);

        }
        else if(lang.contains("h"))
        {
            r2.setChecked(true);
        }
        else
        {
            r3.setChecked(true);
        }
        r1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r2.setChecked(false);
                r3.setChecked(false);
            }
        });
        r2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r3.setChecked(false);
                r1.setChecked(false);
            }
        });
        r3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                r2.setChecked(false);
                r1.setChecked(false);
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
                    Toast.makeText(Setting_Activity.this, "भाषा मराठीत बदलली", Toast.LENGTH_SHORT).show();
                }
                else if(r2.isChecked()) {
                    setLocale("hi");
                    Toast.makeText(Setting_Activity.this, "भाषा बदली हिंदी में", Toast.LENGTH_SHORT).show();
                }
                else if(r3.isChecked()) {
                    setLocale("en");
                    Toast.makeText(Setting_Activity.this, "Language Changed to English", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent openHomeActivity = new Intent(Setting_Activity.this, HomePage.class);
        openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openHomeActivity, 0);
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
        restartActivity(Setting_Activity.this);
    }
    public static void restartActivity(Activity activity){
        if (Build.VERSION.SDK_INT >= 11) {
            activity.recreate();
        } else {
            activity.finish();
            activity.startActivity(activity.getIntent());
        }
    }
}