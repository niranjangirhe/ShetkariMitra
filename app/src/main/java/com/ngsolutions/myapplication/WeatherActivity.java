package com.ngsolutions.myapplication;


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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.FeaturedAdapter;
import com.ngsolutions.myapplication.HelperClasses.HomeAddapter.FeaturedHelperClass;
import com.ngsolutions.myapplication.Model.WeatherForecast;

import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


import cz.msebera.android.httpclient.Header;

public class WeatherActivity extends AppCompatActivity {

    RecyclerView featureRecycler;
    RecyclerView.Adapter adapter;
    ImageButton backButton;
    ImageView weatherStandby;
    final String APP_ID = "444e31951a39e90687fe2e9bf224ab4d";
    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/forecast";
    private ImageView mIcon;

    String[] days = new String[5];
    String[] MinMaxDays = new String[5];
    String[] ConditionDays = new String[5];
    String Latitude="";
    String Longitude="";
    TextView temperature, windSpeed, humidity, description,min_max_temp;
    LottieAnimationView pbWeather;
    LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        loadLocale();

        //hooks
        featureRecycler = findViewById(R.id.Wrather_Recycler);
        backButton = findViewById(R.id.BackBtnWeather);
        temperature = findViewById(R.id.TempNowText);
        windSpeed = findViewById(R.id.WindSpeedText);
        humidity = findViewById(R.id.HumidityNowText);
        description = findViewById(R.id.DescriptionText);
        min_max_temp = findViewById(R.id.TempMinMaxText);
        mIcon = findViewById(R.id.TodayWeatherIcon);
        pbWeather = findViewById(R.id.Pbweather);
        lineChart = findViewById(R.id.LineChart);
        weatherStandby = findViewById(R.id.weather_standybyImage);

        temperature.setVisibility(View.INVISIBLE);
        humidity.setVisibility(View.INVISIBLE);
        description.setVisibility(View.INVISIBLE);
        windSpeed.setVisibility(View.INVISIBLE);
        min_max_temp.setVisibility(View.INVISIBLE);
        mIcon.setVisibility(View.INVISIBLE);
        pbWeather.setVisibility(View.VISIBLE);
        weatherStandby.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.INVISIBLE);


        Intent intent= getIntent();
        Bundle b = intent.getExtras();
        if(b!=null)
        {
            Latitude =(String) b.get("Latitude");
            Longitude =(String) b.get("Longitude");
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openHomeActivity = new Intent(WeatherActivity.this, HomePage.class);
                openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openHomeActivity, 0);
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        RequestParams params = new RequestParams();
        params.put("lat", Latitude);
        params.put("lon", Longitude);
        params.put("appid", APP_ID);
        letsdoSomeNetworking(params);
    }
    private void letsdoSomeNetworking(RequestParams params)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL,params,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //super.onSuccess(statusCode, headers, response);
                WeatherForecast weatherData = WeatherForecast.fromJson(response);
                Toast.makeText(WeatherActivity.this, weatherData.getmCity(), Toast.LENGTH_SHORT).show();
                updateUI(weatherData);
                LineDataSet lineDataSet = new LineDataSet(lineChartDataSet(weatherData),"data set");
                ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
                lineDataSet.setValueTextSize(15);
                lineDataSet.enableDashedLine(20,10,2);
                lineDataSet.setLineWidth(2);
                lineDataSet.setValueTextColor((getResources().getColor(R.color.white)));
                iLineDataSets.add(lineDataSet);

                LineData lineData = new LineData(iLineDataSets);
                lineChart.setData(lineData);
                // style chart
                lineChart.setScaleEnabled(false);
                Description description = new Description();
                description.setText("");
                lineChart.setDescription(description);
                lineChart.setDrawGridBackground(false);
                lineChart.setDrawBorders(false);
                lineChart.setAutoScaleMinMaxEnabled(true);

                // remove axis
                YAxis leftAxis = lineChart.getAxisLeft();
                leftAxis.setEnabled(false);
                YAxis rightAxis = lineChart.getAxisRight();
                rightAxis.setEnabled(false);

                XAxis xAxis = lineChart.getXAxis();
                xAxis.setEnabled(false);

                // hide legend
                Legend legend = lineChart.getLegend();
                legend.setEnabled(false);
                lineChart.invalidate();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(WeatherActivity.this, "Got Weather unSuccessfully", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private  ArrayList<Entry> lineChartDataSet(WeatherForecast weatherData)
    {
        ArrayList<Entry> dataSet = new ArrayList<Entry>();
        float[] temp = weatherData.getTempArr();
        for(int i =0; i<9; i++)
        {
            dataSet.add(new Entry(i,temp[i]));
        }

        return dataSet;
    }
    private  void updateUI(WeatherForecast weatherData)
    {
        temperature.setVisibility(View.VISIBLE);
        humidity.setVisibility(View.VISIBLE);
        description.setVisibility(View.VISIBLE);
        windSpeed.setVisibility(View.VISIBLE);
        min_max_temp.setVisibility(View.VISIBLE);
        mIcon.setVisibility(View.VISIBLE);
        pbWeather.setVisibility(View.INVISIBLE);
        weatherStandby.setVisibility(View.INVISIBLE);
        lineChart.setVisibility(View.VISIBLE);


        temperature.setText(getString(R.string.Temp)+weatherData.getmTemperature());
        min_max_temp.setText(getString(R.string.min_max)+weatherData.getmTemperatureMinMax());
       windSpeed.setText(getString(R.string.wind)+weatherData.getmWind());
       humidity.setText(getString(R.string.humidity_)+weatherData.getmHumidity());
        description.setText(weatherData.getmDescription());
       int resourceID = getResources().getIdentifier(weatherData.getmIcon(),"drawable",getPackageName());
       mIcon.setImageResource(resourceID);

       days= weatherData.getDayDay();
       MinMaxDays = weatherData.getMinMaxDay();
       ConditionDays = weatherData.getConditionDay();
       featureRecycler();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    private void featureRecycler()  {
        featureRecycler.setHasFixedSize(true);
        featureRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,1);
        featuredLocations.add(new FeaturedHelperClass(getResources().getIdentifier(ConditionDays[1],"drawable",getPackageName()),MinMaxDays[1],ConditionDays[1],DateToday(c.get(Calendar.DAY_OF_WEEK))));
        c.add(Calendar.DATE,1);
        featuredLocations.add(new FeaturedHelperClass(getResources().getIdentifier(ConditionDays[2],"drawable",getPackageName()),MinMaxDays[2],ConditionDays[2],DateToday(c.get(Calendar.DAY_OF_WEEK))));
        c.add(Calendar.DATE,1);
        featuredLocations.add(new FeaturedHelperClass(getResources().getIdentifier(ConditionDays[3],"drawable",getPackageName()),MinMaxDays[3],ConditionDays[3],DateToday(c.get(Calendar.DAY_OF_WEEK))));
        c.add(Calendar.DATE,1);
        featuredLocations.add(new FeaturedHelperClass(getResources().getIdentifier(ConditionDays[4],"drawable",getPackageName()),MinMaxDays[4],ConditionDays[4],DateToday(c.get(Calendar.DAY_OF_WEEK))));
        //c.add(Calendar.DATE,1);
        //featuredLocations.add(new FeaturedHelperClass(getResources().getIdentifier(ConditionDays[5],"drawable",getPackageName()),MinMaxDays[5],ConditionDays[5],DateToday(c.get(Calendar.DAY_OF_WEEK))));



        adapter = new FeaturedAdapter(featuredLocations);
        featureRecycler.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        Intent openHomeActivity = new Intent(WeatherActivity.this, HomePage.class);
        openHomeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(openHomeActivity, 0);
    }
    private String DateToday(int i)
    {
        String day;
        switch (i){
            case 1: day="SUN";
            break;
            case 2:day="MON";
            break;
            case 3:day="TUE";
            break;
            case 4:day="WEN";
            break;
            case 5:day="THU";
            break;
            case 6:day="FRI";
            break;
            case 7:day="SAT";
            break;
            default:day="SUN";
            break;
        }
        return day;
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