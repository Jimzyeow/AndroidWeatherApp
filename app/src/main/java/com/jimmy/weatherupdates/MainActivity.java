package com.jimmy.weatherupdates;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity
{
    EditText city, country;
    TextView weatherResult;
    private final String url = "https://api.openweathermap.org/data/2.5/weather"; //open weather current weather data
    private final String appId = "af4f49272ab708303e634ae97573def0"; //own api key
    DecimalFormat df = new DecimalFormat("#.##"); //this will format the temperature to 2 decimal places

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //match the layout with variables
        city = findViewById(R.id.city);
        country = findViewById(R.id.country);
        weatherResult = findViewById(R.id.result);
    }

    public void getWeatherDetails(View view) {
        //define a temp variable to hold complete url
        String tempURL = "";
        String tempCity = city.getText().toString().trim();
        String tempCountry = country.getText().toString().trim();

        //city cannot be null
        if (tempCity.equals("")) {
            weatherResult.setText("Please enter the City field");
        } else {
            //if user input optional field country, add it as part of the URL
            if (!tempCountry.equals("")) {
                tempURL = url + "?q=" + tempCity + "," + tempCountry + "&appid=" + appId;
            } else {
                tempURL = url + "?q=" + tempCity + "&appid=" + appId;
            }
            //instantiate string object as HTTP request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, tempURL, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
//                    Log.d("response", response);

                    String output ="";

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                        String description = jsonObjectWeather.getString("description");

                        JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                        double temp = jsonObjectMain.getDouble("temp") - 273.15; //to show in degree celscius
                        double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                        float pressure = jsonObjectMain.getInt("pressure");
                        int humidity = jsonObjectMain.getInt("humidity");

                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind = jsonObjectWind.getString("speed");

                        JSONObject jsonObjectCloud = jsonResponse.getJSONObject("clouds");
                        String clouds = jsonObjectCloud.getString("all");

                        JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                        String countryName = jsonObjectSys.getString("country");

                        String cityName = jsonResponse.getString("name");
                        weatherResult.setTextColor(Color.rgb(0,0,0));

                        output += "Current weather of " + cityName + " (" + countryName + ")"
                                + "\n Temp: " + df.format(temp) + " degree Celscius"
                                + "\n Feels like: " + df.format(feelsLike) + " degree Celscius"
                                + "\n Humidity: " + humidity + "%"
                                + "\n Description: " + description
                                + "\n Wind Speed: " + wind + "m/s"
                                + "\n Cloudiness: " + clouds + "%"
                                + "\n Pressure: " + pressure + " hpa";

                        weatherResult.setText(output);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
                }
            });

            //instantiate request queue
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }
    }
}




