package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather?lat=12.91891&lon=77.58551&appid=06d97305963de8d65fe7a02e623e5b97";
    // App ID to use OpenWeather data
    final String APP_ID = "06d97305963de8d65fe7a02e623e5b97";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    String LOCATION_PROVIDER = LocationManager.NETWORK_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    LocationManager mLocationManager;
    LocationListener mLocationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);

        // Linking the elements in the layout to Java code
        mCityLabel = findViewById(R.id.locationTV);
        mWeatherImage = findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = findViewById(R.id.tempTV);
        ImageButton changeCityButton = findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherController.this, ChangeCityController.class);
                startActivity(intent);
            }
        });

    }


    // TODO: Add onResume() here:
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Clima App", "onResume() method called");

        Intent nIntent = getIntent();
        String cityName = nIntent.getStringExtra("city");

        if (cityName != null) {
            getWeatherForNewCity(cityName);
        } else {
            Log.d("Clima App", "Getting Weather for current location");
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
    private void getWeatherForNewCity(String cityName) {
        RequestParams params1 = new RequestParams();
        params1.put("q", cityName);
        params1.put("appid", APP_ID);
        letsDoSomeNetworking(params1);
    }

    // TODO: Add getWeatherForCurrentLocation() here:
    private void getWeatherForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Clima App", "onLocationChanged: is called");
                String longitude = String.valueOf(location.getLongitude());
                String latitude = String.valueOf(location.getLatitude());
                Log.d("Clima App", "The Latitude is" + latitude);
                Log.d("Clima App", "The Longitude is" + longitude);

                RequestParams params = new RequestParams();
                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking(params);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("Clima App", "onProviderDisabled is called");
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Clima App", "onRequestPermissionsResult() : Permission Granted");
                getWeatherForCurrentLocation();
            } else {
                Log.d("Clima App", "Permission has been Denied");
            }
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:
    private void letsDoSomeNetworking(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Clima App", "Success : Json Response" + response.toString());
                WeatherDataModel weatherData = WeatherDataModel.fromJson(response);
                if (weatherData != null) {
                    updateUI(weatherData);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {
                Log.e("Clima App", "Failure" + e.toString());
                Log.d("Clima App", "Status Code" + statusCode);
                Toast.makeText(WeatherController.this, "Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weather) {
        mTemperatureLabel.setText(weather.getmTemperature());
        mCityLabel.setText(weather.getmCity());

        int resourceID = getResources().getIdentifier(weather.getmIconName(), "drawable", getPackageName());
        mWeatherImage.setImageResource(resourceID);
    }


    // TODO: Add onPause() here:
    @Override
    protected void onPause() {
        super.onPause();

        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }
}