package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MyCropActivity extends AppCompatActivity {
    Button Fertilizer_btn,Crop_btn;
    Button[] cropButton = new Button[6];
    ImageButton[] closeButton = new ImageButton[6];
    TextView[] cropNameText = new TextView[6];
    ImageButton backButton;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String cropNum;
    String[] crop = new String[6];
    String[] cropCultivationUrl = new String[6];
    int[] pairer = new int[]{-1,-1,-1,-1,-1,-1};
    String userID;
    String userName;
    int cropNumber=0;
    String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_crop);
        loadLocale();
        Fertilizer_btn = findViewById(R.id.Fertilizer_btn);
        Crop_btn = findViewById(R.id.CropSuggestBtn);
        backButton = findViewById(R.id.BackBtnCropSuggest);
        cropNameText[0]=findViewById(R.id.cropname1);
        cropButton[0]=findViewById(R.id.crop1);
        closeButton[0]=(ImageButton)findViewById(R.id.close1);
        cropNameText[1]=findViewById(R.id.cropname2);
        cropButton[1]=findViewById(R.id.crop2);
        closeButton[1]=findViewById(R.id.close2);
        cropNameText[2]=findViewById(R.id.cropname3);
        cropButton[2]=findViewById(R.id.crop3);
        closeButton[2]=findViewById(R.id.close3);
        cropNameText[3]=findViewById(R.id.cropname4);
        cropButton[3]=findViewById(R.id.crop4);
        closeButton[3]=findViewById(R.id.close4);
        cropNameText[4]=findViewById(R.id.cropname5);
        cropButton[4]=findViewById(R.id.crop5);
        closeButton[4]=findViewById(R.id.close5);
        cropNameText[5]=findViewById(R.id.cropname6);
        cropButton[5]=findViewById(R.id.crop6);
        closeButton[5]=findViewById(R.id.close6);
        for(int q=0;q<6;q++)
        {
            cropNameText[q].setVisibility(View.INVISIBLE);
            closeButton[q].setVisibility(View.INVISIBLE);
            cropButton[q].setVisibility(View.INVISIBLE);
        }
        closeButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[0]);
            }
        });
        closeButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[1]);
            }
        });
        closeButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[2]);
            }
        });
        closeButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[3]);
            }
        });
        closeButton[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[4]);
            }
        });
        closeButton[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseDeleteRequested(pairer[5]);
            }
        });
        cropButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(0);
            }
        });
        cropButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(1);
            }
        });
        cropButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(2);
            }
        });
        cropButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(3);
            }
        });
        cropButton[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(4);
            }
        });
        cropButton[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb(5);
            }
        });

        Crop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCropActivity.this, Select_crop.class);
                startActivity(intent);
                finish();
            }
        });

        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(MyCropActivity.this, new EventListener<DocumentSnapshot>() {
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
                    try {
                        //Toast.makeText(SuggestCropActivity.this, "problem in catch", Toast.LENGTH_SHORT).show();
                        userName = value.get("userName").toString();
                    }
                    catch (Exception e)
                    {
                        userName="";
                        //Toast.makeText(SuggestCropActivity.this, "No problem in catch", Toast.LENGTH_SHORT).show();
                    }
                    cropNumber=0;
                    String tempCropNum = cropNum;
                    while(true)
                    {
                        int index = tempCropNum.indexOf("1");
                        if(index!=-1)
                        {
                            tempCropNum = tempCropNum.substring(0, index)
                                    + "0"
                                    + tempCropNum.substring(index + 1);

                            DocumentReference subDoc = fstore.collection("plan").document(crop[index]);
                            subDoc.addSnapshotListener(MyCropActivity.this, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                                    if (value.exists()) {
                                        String cropName;
                                        if(lang.contains("m"))
                                            cropName = value.get("cropmarathi").toString();
                                        else if(lang.contains("h"))
                                            cropName = value.get("crophindi").toString();
                                        else
                                            cropName = value.get("cropname").toString();
                                        cropCultivationUrl[cropNumber] = value.get("cultivation").toString();
                                        cropNameText[cropNumber].setVisibility(View.VISIBLE);
                                        closeButton[cropNumber].setVisibility(View.VISIBLE);
                                        cropButton[cropNumber].setVisibility(View.VISIBLE);
                                        cropNameText[cropNumber].setText(cropName);
                                        pairer[cropNumber]=index;
                                        Log.d("niranjanPairer",Integer.toString(cropNumber)+"-"+Integer.toString(index));
                                        cropNumber++;
                                        Log.d("niranjanSapadla", cropName);
                                    }
                                }

                            });
                        }
                        else
                            break;
                    }

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(MyCropActivity.this, HomePage.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });

        Fertilizer_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCropActivity.this, SuggestCropActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }
    @Override
    public void onBackPressed() {
        Intent openHomeActivity = new Intent(MyCropActivity.this, HomePage.class);
        openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openHomeActivity, 0);
        finish();
    }
    private void firebaseDeleteRequested(int index)
    {
        if(index<0)
            return;
        cropNum = cropNum.substring(0, index)
                + "0"
                + cropNum.substring(index + 1);
        crop[index]="";
        Map<String, Object> user = new HashMap<>();
        user.put("cropNum", cropNum);
        user.put("crop0", crop[0]);
        user.put("crop1", crop[1]);
        user.put("crop2", crop[2]);
        user.put("crop3", crop[3]);
        user.put("crop4", crop[4]);
        user.put("crop5", crop[5]);
        DocumentReference delDoc = fstore.collection("users").document(userID);
        delDoc.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MyCropActivity.this, getString(R.string.crop_deleted), Toast.LENGTH_SHORT).show();
                cropButton[cropNumber].setVisibility(View.INVISIBLE);
                closeButton[cropNumber].setVisibility(View.INVISIBLE);
                cropNameText[cropNumber].setVisibility(View.INVISIBLE);
            }
        });

    }
    private void openWeb(int index)
    {
        Intent openWebview = new Intent(MyCropActivity.this, WebViewActivity.class);
        openWebview.putExtra("pageTitle",cropNameText[index].getText().toString()+getString(R.string.cultivation_guide));
        openWebview.putExtra("url",cropCultivationUrl[index]);
        startActivity(openWebview);
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
        lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}