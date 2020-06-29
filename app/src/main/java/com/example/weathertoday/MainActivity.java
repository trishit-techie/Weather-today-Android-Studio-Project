package com.example.weathertoday;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText cityEditText;
    TextView reportTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityEditText = (EditText)findViewById(R.id.cityEditText);
        reportTextView = (TextView)findViewById(R.id.reportTextView);
    }
    public void getWeather(View view){
        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + cityEditText.getText().toString() + "&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(), 0);
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Sorry! Weather could not be found.",Toast.LENGTH_LONG).show();
        }
    }
    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection =null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection)url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data!=-1)
                {
                    char current = (char)data;
                    result+=current;
                    data = reader.read();
                }
                return result;

            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject obj = new JSONObject(s);
                String weatherInfo = obj.getString("weather");
                String tempInfo = obj.getString("main");
                String windInfo = obj.getString("wind");
                Log.i("weather info",weatherInfo);
                Log.i("temperature info",tempInfo);
                JSONArray array = new JSONArray(weatherInfo);
                String message="";
                for(int i=0;i<array.length();i++)
                {
                    JSONObject jsonPart = array.getJSONObject(i);
                    String main = jsonPart.getString("main");
                    String description = jsonPart.getString("description");
                    if(!main.equals("") && !description.equals("")){
                        message = main+" , "+description;
                    }
                }

                    JSONObject tempPart = new JSONObject(tempInfo);
                    String temp =  tempPart.getString("temp");
                    String feels_like =  tempPart.getString("feels_like");
                    String temp_min =  tempPart.getString("temp_min");
                    String temp_max =  tempPart.getString("temp_max");
                    String pressure = tempPart.getString("pressure");
                    String humidity = tempPart.getString("humidity");

                    JSONObject windPart = new JSONObject(windInfo);
                    String speed = windPart.getString("speed");

                if(!message.equals(""))
                    reportTextView.setText("Sky: "+message +"\r\n"+ "Temperature: "+temp+"\r\n"+"Feels like: "+ feels_like+"\r\n"+"Max. temperature: "+temp_max+"\r\n"+"Min. temperature: "+temp_min+"\r\n"+"Pressure: "+pressure+" mm Hg"+"\r\n"+"Humidity: "+humidity+"%"+"\r\n"+"Wind velocity: "+speed+" m/s");
                else {
                    Toast.makeText(getApplicationContext(),"Sorry! Weather could not be found.",Toast.LENGTH_LONG).show();
                }
                }

            catch (Exception e){
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Sorry! Weather could not be found.",Toast.LENGTH_LONG).show();
            }
        }
    }

}
