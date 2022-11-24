package com.ngsolutions.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ngsolutions.myapplication.Model.WeatherData;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class HomePage extends AppCompatActivity {

    ImageButton settingButton, backButton;
    Button myCropButton,projectButton,ChatButton,govPolicy,kisancall;
    LottieAnimationView pb1;
    final String APP_ID = "444e31951a39e90687fe2e9bf224ab4d";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";
    private ImageView mIcon;
    ImageView weatherImage;

    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;
    String Latitude = "";
    String Longitude = "";

    String Location_Provider = LocationManager.GPS_PROVIDER;
    TextView temperature, windSpeed, humidity, description, greetCity;
    LocationManager mLocationManager;
    LocationListener mLocationListener;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth Auth;
    FirebaseFirestore fstore;
    String userID,userName="";
    Button soil_test;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        loadLocale();
        settingButton = findViewById(R.id.SettingBtn);
        temperature = findViewById(R.id.TempTextView);
        humidity = findViewById(R.id.HumidityTextView);
        windSpeed = findViewById(R.id.WindSpeedTextView);
        description = findViewById(R.id.DescriptionTextView);
        greetCity = findViewById(R.id.GreetCityTextView);
        kisancall = findViewById(R.id.kisanCallCEnterBtn);
        mIcon = findViewById(R.id.IconImageView);
        weatherImage = findViewById(R.id.WeatherImg);
        pb1 = findViewById(R.id.PB1);
        myCropButton = findViewById(R.id.MyCropsBtn);
        projectButton = findViewById(R.id.ProjectBtn);
        ChatButton = findViewById(R.id.ChatBtn);
        govPolicy = findViewById(R.id.GovSchemesBtn);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        temperature.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        windSpeed.setVisibility(View.INVISIBLE);
        pb1.setVisibility(View.VISIBLE);

        Auth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userID = Auth.getCurrentUser().getUid();
        updateToken();

        DocumentReference documentReference = fstore.collection("users").document(userID);
        documentReference.addSnapshotListener(HomePage.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.exists()) {
                    try {
                        userName = value.get("userName").toString();
                        greetCity.setText(getString(R.string.Greetings)+userName);
                    } catch (Exception e) {
                        userName="";
                    }
                }
                else
                {
                    userName="";
                }
            }
        });

        soil_test = findViewById(R.id.soil_img_btn);

        kisancall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18001801551"));
                startActivity(intent);
            }
        });
        govPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openWebview = new Intent(HomePage.this, WebViewActivity.class);
                openWebview.putExtra("pageTitle",getString(R.string.gov_policies));
                //openWebview.putExtra("url","https://krishijagran.com/agripedia/best-government-schemes-and-programmes-in-agriculture-for-farmers/");
                openWebview.putExtra("url","https://agricoop.nic.in/en/ministry-major-schemes");
                startActivity(openWebview);
            }
        });
        ChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, ChatMainScreenActivity.class);
                startActivity(intent);
            }
        });
        myCropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, MyCropActivity.class);
                startActivity(intent);

//                Intent i = new Intent(HomePage.this,CameraActivity.class);
//                i.putExtra("Mode",0);
//                startActivity(i);
            }
        });
        projectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, CameraActivity.class);
                startActivity(intent);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(HomePage.this, Setting_Activity.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
                finish();
            }
        });

        weatherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Latitude.isEmpty() || Longitude.isEmpty())
                    Toast.makeText(HomePage.this, getString(R.string.please_wait), Toast.LENGTH_SHORT).show();
                else {
                    Intent openWeatherActivity = new Intent(HomePage.this, WeatherActivity.class);
                    openWeatherActivity.putExtra("Latitude", Latitude);
                    openWeatherActivity.putExtra("Longitude", Longitude);
                    startActivity(openWeatherActivity);
                }
            }
        });
        greetCity.setText(getString(R.string.Greetings)+userName);

        soil_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomePage.this,ProjectActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        greetCity.setText(getString(R.string.Greetings)+userName);
        getLocation();
        SharedPreferences sp = getSharedPreferences("SP_USER",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("Current_USERID",userID);
        editor.apply();
        //Toast.makeText(this, userID, Toast.LENGTH_SHORT).show();
    }

    private void getLocation() {
        Log.d("niranjanState","in getLo");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(HomePage.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE);
            return;
        }
        if (ActivityCompat.checkSelfPermission(HomePage.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        Latitude = String.valueOf(location.getLatitude());
                        Longitude = String.valueOf(location.getLongitude());
                        //Toast.makeText(HomePage.this, Latitude + "   " + Longitude, Toast.LENGTH_SHORT).show();
                        RequestParams params = new RequestParams();
                        params.put("lat", Latitude);
                        params.put("lon", Longitude);
                        params.put("appid", APP_ID);
                        letsdoSomeNetworking(params);
                    }
                }
            });
        }
    }
    private void updateToken()
    {
        DocumentReference tokenUpdater = fstore.collection("users").document(userID);
        Map<String,Object> tk = new HashMap<>();
        tk.put("token",FirebaseInstanceId.getInstance().getToken());
        tokenUpdater.update(tk).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(HomePage.this, "Token Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getLocation();
            }
            else
            {
                Toast.makeText(HomePage.this, getString(R.string.Please_give), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                WeatherData weatherData = WeatherData.fromJson(response);
                updateUI(weatherData);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(HomePage.this, getString(R.string.weather_unSuccess), Toast.LENGTH_SHORT).show();

            }
        });
    }
    private  void updateUI(WeatherData weatherData)
    {
        temperature.setVisibility(View.VISIBLE);
        humidity.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        windSpeed.setVisibility(View.VISIBLE);
        pb1.setVisibility(View.GONE);
        greetCity.setText(getString(R.string.Greetings)+userName);
        temperature.setText(weatherData.getmTemperature());
        windSpeed.setText(weatherData.getmWind());
        humidity.setText(weatherData.getmHumidity());
        description.setText(weatherData.getmDescription());
        int resourceID = getResources().getIdentifier(weatherData.getmIcon(),"drawable",getPackageName());
        mIcon.setImageResource(resourceID);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
    @Override
    public void onBackPressed() {
        finishAffinity();
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