package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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

public class ChatMainScreenActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText userNameEditText;
    private Button saveName;
    private ImageButton MyQueryButton;
    ImageView[] cropButton = new ImageView[6];
    TextView[] cropNameText = new TextView[6];
    ImageView commonForum;
    ImageView askDev;
    String userName="";
    boolean gotName=false;
    ImageButton backButton;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String cropNum;
    String[] crop = new String[6];
    int[] pairer = new int[]{-1,-1,-1,-1,-1,-1};
    String userID;
    int cropNumber=0;
    boolean datafound=false;
    String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main_screen);
        loadLocale();
        backButton = findViewById(R.id.BackBtnChatMainScreen);
        cropNameText[0]=findViewById(R.id.ChatCropName1);
        cropButton[0]=findViewById(R.id.Chat1Tile);
        cropNameText[1]=findViewById(R.id.ChatCropName2);
        cropButton[1]=findViewById(R.id.Chat2Tile);
        cropNameText[2]=findViewById(R.id.ChatCropName3);
        cropButton[2]=findViewById(R.id.Chat3Tile);
        cropNameText[3]=findViewById(R.id.ChatCropName4);
        cropButton[3]=findViewById(R.id.Chat4Tile);
        cropNameText[4]=findViewById(R.id.ChatCropName5);
        cropButton[4]=findViewById(R.id.Chat5Tile);
        cropNameText[5]=findViewById(R.id.ChatCropName6);
        cropButton[5]=findViewById(R.id.Chat6Tile);
        commonForum = findViewById(R.id.Chat8Tile);
        askDev=findViewById(R.id.Chat7Tile);
        MyQueryButton = findViewById(R.id.MyQueryBtn);



        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(ChatMainScreenActivity.this, HomePage.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });

        MyQueryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatMainScreenActivity.this, MyQueryActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
        commonForum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openChat = new Intent(ChatMainScreenActivity.this, ChatActivity.class);
                openChat.putExtra("cropCode","0");
                openChat.putExtra("chatTitle",getString(R.string.common));
                openChat.putExtra("userID",userID);
                startActivity(openChat);
                finish();
            }
        });
        askDev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail="niranjangirheindia@gmail.com";
                String subject = "Querry in Shetkari Mitra App";
                String usernameReplacer = userName;
                if(usernameReplacer.isEmpty()||usernameReplacer==null)
                {
                    usernameReplacer="Your Name";
                }
                String body = "Hey, Developer!\nI am "+ usernameReplacer+"\n\nMy UserID is "+userID+"\n"+getString(R.string.bodyforemail)+"\n\n";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"+mail+"?subject=" + Uri.encode(subject) + "&body=" + Uri.encode(body));
                intent.setData(data);
                startActivity(intent);
            }
        });
        cropButton[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(0);
            }
        });
        cropButton[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(1);
            }
        });
        cropButton[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(2);
            }
        });
        cropButton[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(3);
            }
        });
        cropButton[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(4);
            }
        });
        cropButton[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openChatWindow(5);
            }
        });
        for(int q=0;q<6;q++)
        {
            cropNameText[q].setVisibility(View.GONE);
            cropButton[q].setVisibility(View.GONE);
        }
        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(ChatMainScreenActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                cropNum="000000";
                if(value.exists()) {
                    datafound=true;
                    cropNum = value.get("cropNum").toString();
                    crop[0]=value.get("crop0").toString();
                    crop[1]=value.get("crop1").toString();
                    crop[2]=value.get("crop2").toString();
                    crop[3]=value.get("crop3").toString();
                    crop[4]=value.get("crop4").toString();
                    crop[5]=value.get("crop5").toString();
                    cropNumber=0;
                    try
                    {
                        userName = value.get("userName").toString();
                        gotName = true;
                    }
                    catch (Exception e)
                    {
                        nameDialog();
                    }
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
                            subDoc.addSnapshotListener(ChatMainScreenActivity.this, new EventListener<DocumentSnapshot>() {
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
                                        cropNameText[cropNumber].setVisibility(View.VISIBLE);
                                        cropButton[cropNumber].setVisibility(View.VISIBLE);
                                        cropNameText[cropNumber].setText(cropName);
                                        pairer[cropNumber]=index;
                                        cropNumber++;
                                    }
                                }

                            });
                        }
                        else
                            break;
                    }

                }
                else
                {
                    datafound=false;
                    Map<String, Object> data = new HashMap<>();
                    data.put("replyNum",0);
                    DocumentReference addReplies = fstore.collection("replies").document(userID);
                    addReplies.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
                if(cropNum=="000000")
                {
                    for(int i=0;i<6;i++)
                    {
                        crop[i]="";
                    }
                    nameDialog();
                }
            }

        });

    }

    private void openChatWindow(int index) {
        Intent openChat = new Intent(ChatMainScreenActivity.this, ChatActivity.class);
        openChat.putExtra("cropCode",crop[pairer[index]]);
        openChat.putExtra("chatTitle",cropNameText[index].getText().toString());
        openChat.putExtra("userID",userID);
        startActivity(openChat);
        finish();

        //Toast.makeText(this, crop[pairer[index]], Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
    }
    @Override
    public void onBackPressed() {
        Intent openHomeActivity = new Intent(ChatMainScreenActivity.this, HomePage.class);
        openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openHomeActivity, 0);
        finish();
    }
    private void nameDialog()
    {
        if(gotName)
            return;
        dialogBuilder = new AlertDialog.Builder(this);
        final View namePopupView = getLayoutInflater().inflate(R.layout.popupname,null);
        userNameEditText = namePopupView.findViewById(R.id.UserNameText);
        saveName = namePopupView.findViewById(R.id.SaveLangBtn);
        dialogBuilder.setView(namePopupView);
        dialog =dialogBuilder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        saveName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userNameEditText.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(ChatMainScreenActivity.this, "Please enter your name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    userName = userNameEditText.getText().toString();
                    Map<String, Object> user = new HashMap<>();
                    user.put("userName",userName);
                    DocumentReference addname = fstore.collection("users").document(userID);
                    if(datafound) {
                        addname.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ChatMainScreenActivity.this, "Name saved to your profile", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(ChatMainScreenActivity.this, "Name saved to your profile", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    gotName =true;
                    dialog.dismiss();
                }
            }
        });
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
        lang = prefs.getString("My_lang","en");
        setLocale(lang);
    }
}