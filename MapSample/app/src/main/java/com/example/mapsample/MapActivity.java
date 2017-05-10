package com.example.mapsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.test.espresso.core.deps.guava.reflect.TypeToken;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.mapsample.app.Config;
import com.example.mapsample.model.MyPlace;
import com.example.mapsample.model.StationPropertyResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    public static ArrayList<StationPropertyResult> allStations = new ArrayList<>();

//    int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));


//        addMarker(sydney, 1, "im here");

        mMap.getUiSettings().setMapToolbarEnabled(false);//自動產生
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }//自動產生
        mMap.setMyLocationEnabled(true);

        if (MainActivity.pickPlace != null) {
            moveMap(MainActivity.pickPlace.getLatLng());//pickPlace的位置
        } else {
            LatLng place = new LatLng(MainActivity.latitude, MainActivity.longitude);//自己GPS所在位置
            moveMap(place);
        }



        for (MyPlace place : MainActivity.myPlaces) {
            try {
                Config.LOGD("id: " + place.getPlaceId()
                        + ", latlng: " + place.getLatLng()
                        + ", name: " + place.getName()
                        + ", icon: " + place.getIcon()
                        + ", openingHours: " + place.getOpeningHours().get("open_now"));

                String openingHourStatus = "";
                if (place.getOpeningHours().get("open_now").getAsBoolean())
                    openingHourStatus = "營業中\n";
                else
                    openingHourStatus = "休息中\n";

                addMarker(place.getLatLng(), place.getName(), openingHourStatus);
            } catch (Exception e) {
//                e.printStackTrace();
                Config.LOGD("error: " + e.getMessage().toString());
            }
        }


//        for (LatLng latLng : MainActivity.collectLatLngs) {
//            addMarker(latLng, "", "");
//        }


//        //取得資料
//        GetStationData();
//
//        //將每一筆資料放到地圖上
//        for (StationPropertyResult station : allStations) {
//            LatLng stationPlace = new LatLng(station.latitude, station.longitude);
//            addMarker(stationPlace, station.stationName, station.address);
//        }

    }

    private void GetStationData() {
        //取得資料
        try {
            JSONObject jsonObject = new JSONObject(loadJSONFromAssets());
            JSONArray jsonArray = jsonObject.getJSONArray("features");
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    String jsonObjectStr = jsonArray.getJSONObject(i).get("properties").toString();
                    StationPropertyResult mResult =  new Gson().fromJson(jsonObjectStr, new TypeToken<StationPropertyResult>(){}.getType());
                    if (mResult != null) {
                        allStations.add(mResult);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String loadJSONFromAssets() {
        String json = null;
        try {
            InputStream is = getAssets().open("taiwan_rail_station");//自己建的local json file,並放置在src=>main=>assets
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(13)
                .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    private void addMarker(LatLng place, String title, String content) {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(R.drawable.mappin);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(String.valueOf(title))
                .snippet(content)
                .icon(icon);

        mMap.addMarker(markerOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//            }
//        }
    }

}
