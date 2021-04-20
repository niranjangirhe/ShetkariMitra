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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.ngsolutions.myapplication.Model.cropInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Select_crop extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinnerState,spinnerDistrict,spinnerCrop;
    private RequestQueue mQueue;
    String[] str = {
            "--Select State/राज्य--",
            "Andaman And Nicobar Islands",
            "Andhra Pradesh",
            "Arunachal Pradesh",
            "Assam",
            "Bihar",
            "Chandigarh",
            "Chhattisgarh",
            "Delhi",
            "Goa",
            "Gujarat",
            "Haryana",
            "Himachal Pradesh",
            "Jammu And Kashmir",
            "Jharkhand",
            "Karnataka",
            "Kerala",
            "Ladakh",
            "Lakshadweep",
            "Madhya Pradesh",
            "Maharashtra",
            "Manipur",
            "Meghalaya",
            "Mizoram",
            "Nagaland",
            "Odisha",
            "Puducherry",
            "Punjab",
            "Rajasthan",
            "Sikkim",
            "Tamil Nadu",
            "Telangana",
            "The Dadra And Nagar Haveli And Daman And Diu",
            "Tripura",
            "Uttar Pradesh",
            "Uttarakhand",
            "West Bengal"};
    String[] strCode = {
            "0",
            "35",
            "28",
            "12",
            "18",
            "10",
            "4",
            "22",
            "7",
            "30",
            "24",
            "6",
            "2",
            "1",
            "20",
            "29",
            "32",
            "37",
            "31",
            "23",
            "27",
            "14",
            "17",
            "15",
            "13",
            "21",
            "34",
            "3",
            "8",
            "11",
            "33",
            "36",
            "38",
            "16",
            "9",
            "5",
            "19"
    };

    List<String> district=new ArrayList<String>();
    List<String> districtCode=new ArrayList<String>();
    List<String> cropList =new ArrayList<String>();
    List<String> cropCode=new ArrayList<String>();
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String cropCodeInt="0";
    String cropNum;
    String[] crop= new String[6];
    String userName;
    Button addCropButton;
    ImageButton backButton;
    ArrayList<cropInfo> c = new ArrayList<cropInfo>();
    String lang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_crop);
        loadLocale();
        spinnerState = findViewById(R.id.spinnerState);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerCrop = findViewById(R.id.spinnerCrop);
        spinnerState.setOnItemSelectedListener(this);
        spinnerDistrict.setOnItemSelectedListener(this);
        spinnerCrop.setOnItemSelectedListener(this);
        ArrayAdapter adapterState = new ArrayAdapter(this, R.layout.dropdown_item,str);
        ArrayAdapter adapterDistrict = new ArrayAdapter(this, R.layout.dropdown_item,district);
        ArrayAdapter adapterCrop = new ArrayAdapter(this, R.layout.dropdown_item, cropList);
        addCropButton = findViewById(R.id.AddCropSelectCropBtn);
        backButton = findViewById(R.id.BackBtnCropSuggest);

        Auth = FirebaseAuth.getInstance();
        fstore =FirebaseFirestore.getInstance();
        String userID = Auth.getCurrentUser().getUid();
        getData();
        spinnerDistrict.setEnabled(false);
        spinnerCrop.setEnabled(false);


        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(Select_crop.this, new EventListener<DocumentSnapshot>() {
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


        mQueue = Volley.newRequestQueue(this);
        adapterState.setDropDownViewResource(R.layout.dropdown_item);
        district.add(getString(R.string.select_district));
        districtCode.add("0");
        adapterDistrict.setDropDownViewResource(R.layout.dropdown_item);
        cropList.add(getString(R.string.select_crop));
        cropCode.add("0");
        adapterDistrict.setDropDownViewResource(R.layout.dropdown_item);
        adapterState.setNotifyOnChange(true);
        adapterDistrict.setNotifyOnChange(true);
        adapterCrop.setNotifyOnChange(true);
        spinnerState.setAdapter(adapterState);
        spinnerDistrict.setAdapter(adapterDistrict);
        spinnerCrop.setAdapter(adapterCrop);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMyCropActivity = new Intent(Select_crop.this, MyCropActivity.class);
                openMyCropActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMyCropActivity, 0);
            }
        });
        addCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cropCodeInt=="0")
                {
                    Toast.makeText(Select_crop.this, getString(R.string.crop_select), Toast.LENGTH_SHORT).show();
                    return;
                }
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
                                Toast.makeText(Select_crop.this, getString(R.string.crop_added), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(Select_crop.this, getString(R.string.Limit_reached), Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(Select_crop.this, getString(R.string.crop_already), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        for(int i=0;i<405;i++) {
            DocumentReference cropDataFectch = fstore.collection("plan").document(Integer.toString(i));
            cropDataFectch.addSnapshotListener(Select_crop.this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (value.exists()) {
                        c.add(new cropInfo(Integer.parseInt(value.getId()),value.getString("cropname"),value.getString("cropmarathi"),value.getString("crophindi")));
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent openMyCropActivity = new Intent(Select_crop.this, MyCropActivity.class);
        openMyCropActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openMyCropActivity, 0);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position==0) {
            if(parent.getId() == spinnerCrop.getId())
                cropCodeInt="0";
            return;
        }
        try {
            if (parent.getId() == spinnerState.getId()) {
                district.clear();
                districtCode.clear();
                cropList.clear();
                //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                jsonAddAll(0, "https://www.soilhealth.dac.gov.in/CommonFunction/GetDistrict?statecode=" + strCode[position] + "&VerificationToken=HxS0S_4A29z6TpERRtR6ruhHq-PD4W8LGO5nhbAZhP7LRaTC9b5uUy6AugUlItRXc1xeNmigxnLFjAePLQGwmMGlIVdGVomuzCnMDsAFJ201%2C4gct8wQtWsday0XGhczFn5nCzIDLlThGfGra0Wmb772MlYLzbarsTYYevVYHou5yLTy0hiApnEvB1xUhjuYgKJ0-bkI9H5--VPcCQp3zepg1&_=1618390071743");
            } else if (parent.getId() == spinnerDistrict.getId()) {
                cropList.clear();
                cropCode.clear();
                //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                jsonAddAll(1, "https://www.soilhealth.dac.gov.in/HealthCard/CropSet/BindCrop?VerificationToken=HxS0S_4A29z6TpERRtR6ruhHq-PD4W8LGO5nhbAZhP7LRaTC9b5uUy6AugUlItRXc1xeNmigxnLFjAePLQGwmMGlIVdGVomuzCnMDsAFJ201%2C4gct8wQtWsday0XGhczFn5nCzIDLlThGfGra0Wmb772MlYLzbarsTYYevVYHou5yLTy0hiApnEvB1xUhjuYgKJ0-bkI9H5--VPcCQp3zepg1&Irrigation_Code=&District_Code=" + districtCode.get(position) + "&_=1618390071748");
            }
            else if(parent.getId() == spinnerCrop.getId())
            {
                //Toast.makeText(this, parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                cropCodeInt=cropCode.get(position);
            }
        }
        catch (Exception e)
        {
            Toast.makeText(this, getString(R.string.wait_for_moment), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    private void jsonAddAll(int SnD, String url) {
        spinnerDistrict.setEnabled(false);
        spinnerCrop.setEnabled(false);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for(int i=0;i<response.length();i++) {
                            try {
                                String s1 = response.getJSONObject(i).getString("Text");
                                String s2 = response.getJSONObject(i).getString("Value");
                                if(SnD==0) {
                                    district.add(s1);
                                    districtCode.add(s2);
                                }
                                else {
                                    //cropList.add(s1);
                                    cropCode.add(s2);
                                    try {
                                        int l=0;
                                        int r=c.size()-1;
                                        while(l <= r)
                                        {

                                                int mid = l + (r - l)/2;
                                                if (c.get(mid).getCropCode() == Integer.parseInt(s2)) {
                                                    if(lang.contains("m"))
                                                        cropList.add(c.get(mid).getCropMarathi());
                                                    else if(lang.contains("h"))
                                                        cropList.add(c.get(mid).getCropHindi());
                                                    else
                                                        cropList.add(c.get(mid).getCropName());
                                                    break;
                                                }
                                                else if(c.get(mid).getCropCode() > Integer.parseInt(s2))
                                                {
                                                    r=mid-1;
                                                }
                                                else
                                                {
                                                    l=mid+1;
                                                }

                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        cropList.add(s1);
                                    }
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
        spinnerDistrict.setEnabled(true);
        spinnerCrop.setEnabled(true);
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