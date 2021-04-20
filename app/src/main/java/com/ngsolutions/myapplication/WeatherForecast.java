package com.ngsolutions.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherForecast {
    private String mTemperature;
    private String mTemperatureMinMax;
    private String mIcon;
    private String mCity;
    private String mDescription;
    private String[] DateNTime = new String[40];
    private String[] MinMaxDay = new String[6];
    private String[] DayDay = new String[6];
    private String[] ConditionDay = new String[6];
    private int mWindSpeed;
    private int mWindDir;
    private int mConditionNow;
    private int mHumidity;
    private float[] tempArr = new float[40];
    public static WeatherForecast fromJson(JSONObject jsonObject)
    {
        try
        {
            WeatherForecast weatherData = new WeatherForecast();

            weatherData.mCity=jsonObject.getJSONObject("city").getString("name");
            weatherData.mConditionNow=jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherData.mDescription=jsonObject.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");

            weatherData.mIcon=updateWeatherIcon(weatherData.mConditionNow);
            weatherData.mWindSpeed= (int) (jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getDouble("speed")*3.6);
            weatherData.mWindDir=jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getInt("deg");
            weatherData.mHumidity =  jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getInt("humidity");
            double tempResult = jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp")-273.15;
            int roundedValue=(int) Math.rint(tempResult);
            weatherData.mTemperature=Integer.toString(roundedValue);
            double tempResultMin = jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_min")-273.15;
            int roundedValueMin=(int) Math.rint(tempResultMin);
            double tempResultMax = jsonObject.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_max")-273.15;
            int roundedValueMax=(int) Math.rint(tempResultMax);
            weatherData.mTemperatureMinMax=Integer.toString(roundedValueMin)+"/"+Integer.toString(roundedValueMax);
            double minT, maxT;
            double minTF=250;
            double maxTF=-250;
            int condition=0;
            int dayCounter=0;
            int  counter=1;
            int[] dayNo = new int[40];
            for(int i = 0; i<40; i++)
            {
                double tempArr = jsonObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp")-273.15;
                weatherData.tempArr[i]= (float) tempArr;
                weatherData.DateNTime[i] =jsonObject.getJSONArray("list").getJSONObject(i).getString("dt_txt");
                dayNo[i]=Integer.parseInt(weatherData.DateNTime[i].substring(8,10));

                if(i!=0)
                {
                    if(dayNo[i]==dayNo[i-1])
                    {
                        condition += jsonObject.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id");
                        minT = jsonObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_min")-273.15;
                        maxT = jsonObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_max")-273.15;
                        if(minT < minTF)
                            minTF=minT;
                        if(maxT>maxTF)
                            maxTF=maxT;
                        counter++;
                    }
                    else
                    {
                        weatherData.ConditionDay[dayCounter] = updateWeatherIcon((int) (condition/counter));
                        weatherData.MinMaxDay[dayCounter] = Integer.toString((int) Math.rint(minTF))+"/"+Integer.toString((int) Math.rint(maxTF)) + " °C";
                        weatherData.DayDay[dayCounter]=weatherData.DateNTime[i].substring(0,10);
                        dayCounter++;
                        counter=1;
                        minTF=250;
                        maxTF=-250;
                        condition += jsonObject.getJSONArray("list").getJSONObject(i).getJSONArray("weather").getJSONObject(0).getInt("id");
                        minT = jsonObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_min")-273.15;
                        maxT = jsonObject.getJSONArray("list").getJSONObject(i).getJSONObject("main").getDouble("temp_max")-273.15;
                    }
                }
            }
            weatherData.ConditionDay[dayCounter] = updateWeatherIcon((int) (condition/counter));
            weatherData.MinMaxDay[dayCounter] = Integer.toString((int) Math.rint(minTF))+"/"+Integer.toString((int) Math.rint(maxTF)) + " °C";
            weatherData.DayDay[dayCounter]=weatherData.DateNTime[39].substring(0,10);
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
    public String getmTemperatureMinMax() {
        return mTemperatureMinMax+"°C";
    }
    public String getmIcon() {
        return mIcon;
    }

    public float[] getTempArr() {
        return tempArr;
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

    public String[] getMinMaxDay() {
        return MinMaxDay;
    }

    public String[] getDateNTime() {
        return DateNTime;
    }

    public String[] getDayDay() {
        return DayDay;
    }

    public String[] getConditionDay() {
        return ConditionDay;
    }
}

