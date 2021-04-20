package com.ngsolutions.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {
    private String mTemperature;
    private String mIcon;
    private String mCity;
    private String mDescription;
    private int mWindSpeed;
    private int mWindDir;
    private int mCondition,mHumidity;

    public static WeatherData fromJson(JSONObject jsonObject)
    {
        try
        {
            WeatherData weatherData = new WeatherData();
            weatherData.mCity=jsonObject.getString("name");
            weatherData.mCondition=jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mDescription=jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            weatherData.mIcon=updateWeatherIcon(weatherData.mCondition);
            weatherData.mWindSpeed= (int) (jsonObject.getJSONObject("wind").getDouble("speed")*3.6);
            weatherData.mWindDir=jsonObject.getJSONObject("wind").getInt("deg");
            weatherData.mHumidity =  jsonObject.getJSONObject("main").getInt("humidity");
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue=(int) Math.rint(tempResult);
            weatherData.mTemperature=Integer.toString(roundedValue);
            return weatherData;
        }
         catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static String updateWeatherIcon(int condition)
    {
        if(condition>=200 && condition<300)
        {
            return "thunderstorm";
        }
        else if(condition>=300 && condition<500)
        {
            return "drizzle";
        }
        else if(condition>=600 && condition<700)
        {
            return "snow";
        }
        else if(condition>=700 && condition<800)
        {
            return "atmosphere";
        }
        else if(condition==800)
        {
            return "clear";
        }
        else if(condition>=800 && condition<900)
        {
            return "clouds";
        }
        return "clear";
    }

    public String getmTemperature() {
        return mTemperature+"°C";
    }

    public String getmIcon() {
        return mIcon;
    }

    public String getmCity() {
        return mCity;
    }

    public String getmWind() {
        if(mWindDir<112.5 && mWindDir>=67.5)
            return Integer.toString(mWindSpeed)+" E";
        else if(mWindDir<157.5 && mWindDir>=112.5)
            return Integer.toString(mWindSpeed)+" SE";
        else if(mWindDir<202.5 && mWindDir>=157.5)
            return Integer.toString(mWindSpeed)+" S";
        else if(mWindDir<247.5 && mWindDir>=202.5)
            return Integer.toString(mWindSpeed)+" SW";
        else if(mWindDir<292.5 && mWindDir>=247.5)
            return Integer.toString(mWindSpeed)+" W";
        else if(mWindDir<337.5 && mWindDir>=292.5)
            return Integer.toString(mWindSpeed)+" NW";
        else if(mWindDir>=337.5 && mWindDir<22.5)
            return Integer.toString(mWindSpeed)+" N";
        else
            return Integer.toString(mWindSpeed)+" NE";
    }
    public String getmHumidity() {
        return Integer.toString(mHumidity)+" %";
    }
    public String getmDescription() {
        return mDescription;
    }

}
