package com.ngsolutions.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.ChatAdapter;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.MyQueryAdapter;
import com.ngsolutions.myapplication.Model.MessageModel;
import com.ngsolutions.myapplication.Model.MyQueryModel;

import java.util.ArrayList;
import java.util.Locale;

public class MyQueryActivity extends AppCompatActivity {

    ImageButton backButton;
    RecyclerView myQueriesRecyclerView;
    String lang;
    MyQueryAdapter myQueryAdapter;
    ArrayList<MyQueryModel> myQueryModels = new ArrayList<>();
    String userID;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    ArrayList<String> replyArr = new ArrayList<>();
    ArrayList<String> cropName = new ArrayList<>();
    ArrayList<String> Message = new ArrayList<>();
    int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_query);
        loadLocale();
        backButton = findViewById(R.id.BackBtnMyQueryScreen);
        myQueriesRecyclerView = findViewById(R.id.MyQueryRecycleview);

        Bundle b = getIntent().getExtras();
        if(b!=null) {
            userID = (String) b.get("userID");
            LinearLayoutManager layoutManager = new LinearLayoutManager(MyQueryActivity.this);
            layoutManager.setReverseLayout(true);
            layoutManager.setStackFromEnd(true);
            myQueriesRecyclerView.setLayoutManager(layoutManager);
            myQueryAdapter = new MyQueryAdapter(myQueryModels, MyQueryActivity.this,userID);
            myQueriesRecyclerView.setAdapter(myQueryAdapter);

            Auth = FirebaseAuth.getInstance();
            fstore =FirebaseFirestore.getInstance();
            DocumentReference documentReference = fstore.collection("replies").document(userID);
            documentReference.addSnapshotListener(MyQueryActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value.exists())
                            {
                                int replyNum = Integer.parseInt(value.get("replyNum").toString());
                                for(int i=0; i<replyNum; i++)
                                {
                                    replyArr.add(value.get(Integer.toString(i)).toString());
                                }
                                counter=0;
                                fillView();
                            }
                        }
                    }
            );

        }
        else {
            finish();
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openChatMainActivity = new Intent(MyQueryActivity.this, ChatMainScreenActivity.class);
                openChatMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openChatMainActivity, 0);
                finish();
            }
        });

    }
    private void fillView()
    {
        //fstore =FirebaseFirestore.getInstance();
        for(int i=0; i<replyArr.size(); i++) {
            String cropCode = replyArr.get(i);
            int temp = cropCode.indexOf('~');
            cropCode = cropCode.substring(1,temp);
            if(!cropCode.equals("0")) {
                DocumentReference cropnameRef = fstore.collection("plan").document(cropCode);
                cropnameRef.addSnapshotListener(MyQueryActivity.this, new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (lang.contains("mr"))
                                    cropName.add(value.get("cropmarathi").toString());
                                else if (lang.contains("hi"))
                                    cropName.add(value.get("crophindi").toString());
                                else
                                    cropName.add(value.get("cropname").toString());
                            }
                        }
                );
            }
            else
            {
                cropName.add(getString(R.string.common_forum));
            }
            DocumentReference messageRef = fstore.collection("forums").document(cropCode);
            messageRef.addSnapshotListener(MyQueryActivity.this, new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(value.exists()) {
                                try {
                                    String t = replyArr.get(counter);
                                    counter++;
                                    int temp = t.indexOf('~');
                                    t = "text" + t.substring(temp+1);
                                    int temp2 = value.get(t).toString().indexOf("||");
                                    Message.add(value.get(t).toString().substring(temp2+2));
                                    if (replyArr.size() == Message.size()) {
                                        for (int j = 0; j < replyArr.size(); j++) {
                                            MyQueryModel myQueryModel = new MyQueryModel(cropName.get(j), Message.get(j),replyArr.get(j));
                                            myQueryModels.add(myQueryModel);
                                        }
                                        fillChats(myQueryModels);
                                    }
                                }
                                catch (Exception e)
                                {
                                    Toast.makeText(MyQueryActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
            );
        }
    }

    @Override
    public void onBackPressed() {
        Intent openChatMainActivity = new Intent(MyQueryActivity.this, ChatMainScreenActivity.class);
        openChatMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openChatMainActivity, 0);
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadLocale();
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
    private void fillChats(ArrayList<MyQueryModel> myQueryModels) {
        myQueriesRecyclerView.smoothScrollToPosition(myQueryModels.size());
        myQueryAdapter.notifyDataSetChanged();
    }
}