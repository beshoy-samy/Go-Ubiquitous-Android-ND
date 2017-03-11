package com.example.android.sunshine.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.sunshine.data.WeatherContract;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


/**
 * Created by bisho on 11-Mar-17.
 */

public class SunshineSyncWithWear implements GoogleApiClient.ConnectionCallbacks
                                            ,GoogleApiClient.OnConnectionFailedListener{

    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private final int INDEX_WEATHER_MAX_TEMP = 0;
    private final int INDEX_WEATHER_MIN_TEMP = 1;
    private final int INDEX_WEATHER_CONDITION_ID = 2;



    public SunshineSyncWithWear(Context context) {
        this.mContext = context;
    }

    public void initializeGoogleApiClient(){
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
            Log.d("besho","initialize");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            getWeatherData();
        Log.d("besho","connected");
    }

    private void getWeatherData() {
        ContentResolver resolver = mContext.getContentResolver();
        String[] columns = {WeatherContract.WeatherEntry.COLUMN_MAX_TEMP
                            ,WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                            ,WeatherContract.WeatherEntry.COLUMN_WEATHER_ID};
        Cursor cursor = resolver.query(WeatherContract.WeatherEntry.CONTENT_URI
                                        ,columns
                                        ,null
                                        ,null
                                        ,null);
        if(cursor!=null) {
            if(cursor.moveToPosition(0)){
                double maxTemp = cursor.getDouble(INDEX_WEATHER_MAX_TEMP);
                double minTemp = cursor.getDouble(INDEX_WEATHER_MIN_TEMP);
                int  weatherId = cursor.getInt(INDEX_WEATHER_CONDITION_ID);
                sendWeatherToWear(maxTemp,minTemp,weatherId);
            }
            cursor.close();
        }
    }

    private void sendWeatherToWear(double maxTemp, double minTemp, int weatherId) {
        String highString = SunshineWeatherUtils.formatTemperature(mContext, maxTemp);
        String lowString = SunshineWeatherUtils.formatTemperature(mContext, minTemp);

        PutDataMapRequest mapRequest = PutDataMapRequest.create("/today-weather");
        mapRequest.getDataMap().putString("max-temp",highString);
        mapRequest.getDataMap().putString("min-temp",lowString);
        mapRequest.getDataMap().putInt("weather-image",weatherId);

        PutDataRequest dataRequest = mapRequest.asPutDataRequest();
        Wearable.DataApi.putDataItem(mGoogleApiClient,dataRequest)
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                if(dataItemResult.getStatus().isSuccess())
                    Log.d("beshoy","sent successfully");
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("besho","suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("besho","failed:"+connectionResult.getErrorMessage());
    }


}
