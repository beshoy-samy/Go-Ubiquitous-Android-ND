package com.example.android.sunshine;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by bisho on 11-Mar-17.
 */

public class GetWeatherDataService extends WearableListenerService {

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent dataEvent : dataEventBuffer){
            if(dataEvent.getType() == DataEvent.TYPE_CHANGED){
                String path = dataEvent.getDataItem().getUri().getPath();
                if(path.equals("/today-weather")){
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(dataEvent.getDataItem());
                    DataMap dataMap = dataMapItem.getDataMap();
                    String maxTemp = dataMap.getString("max-temp");
                    String minTemp = dataMap.getString("min-temp");
                    int imageID    = dataMap.getInt("weather-image");
                    Log.d("besho","in onDataChanged:"+maxTemp+","+minTemp+","+imageID);
                }

            }
        }


    }




}
