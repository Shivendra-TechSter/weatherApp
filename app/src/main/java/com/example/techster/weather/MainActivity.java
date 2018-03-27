package com.example.techster.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    TextView resultView;

    public void getWeatherInfo(View view){
        EditText cityNameText = findViewById(R.id.cityName);
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert mgr != null;
        mgr.hideSoftInputFromWindow(cityNameText.getWindowToken(),0);

        DownloadTask task = new DownloadTask();

        try {

            task.execute("http://openweathermap.org/data/2.5/weather?q="+cityNameText.getText().toString()+"&appid=b6907d289e10d714a6e88b30761fae22").get();


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls){

            try {

                URL url;
                StringBuilder res = new StringBuilder();
                HttpURLConnection connection;
                url = new URL(urls[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data !=-1){
                    char current = (char)data;
                    res.append(current);
                    data = reader.read();
                }
                return res.toString();

            } catch (Exception e) {
                return "Failed";
            }
        }




        //Parsing json data and displaying the result to the user
        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            try {
                String msg = "";
                JSONObject jsonObject = new JSONObject(res);
                String weatherInfo= jsonObject.getString("weather");
                String currentTemp = jsonObject.getJSONObject("main").getString("temp");
                String wind = jsonObject.getJSONObject("wind").getString("speed");
                String pressure = jsonObject.getJSONObject("main").getString("pressure");
                String humidity = jsonObject.getJSONObject("main").getString("humidity");

                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0;i < arr.length();i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String desc;

                    desc = jsonPart.getString("main");

                    if (!Objects.equals(desc, "")) {
                         msg = "\nCloudiness : " +desc+ "\n" + "Temperature : " +currentTemp +"°c\n"
                         +"Wind speed : "+ wind + " m/s" +"\nPressure : "+pressure +" hpa"
                         +"\nHumidity : "+humidity+ " %";
                    }

                }

                if (!Objects.equals(msg, "")){
                    resultView.setText(msg);
                    resultView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    resultView.setTextColor(getResources().getColor(R.color.bgColor));
                }

            } catch (JSONException e) {
                resultView.setText("\n\rError occurred!\n\rPlease check the city name\n\rand your internet connection\n\ror try after some time.");
                resultView.setTextColor(getResources().getColor(R.color.failColor));
                resultView.setBackgroundColor(getResources().getColor(R.color.bgColor));

            }
        }
    }







    //Adding menu to the app
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case R.id.about :
                Toast.makeText(getApplicationContext(),"Too lazy to create about page",Toast.LENGTH_LONG).show();
                return true;
            case R.id.exit :
                finish();
                System.exit(0);
                return true;
            default: return false;
        }
    }

    //End of code for menu





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = findViewById(R.id.resultView);

    }
}