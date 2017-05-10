package com.example.mapsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mapsample.app.Config;
import com.example.mapsample.model.MyPlace;
import com.example.mapsample.view.ActionSheet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static ArrayList<MyPlace> myPlaces = new ArrayList<>();

    int PLACE_PICKER_REQUEST = 1;
    public static Place pickPlace;
    boolean isPickPlace = false;

    private Button btnPickPlace, btnSearch;
    private TextView tvPlaceDetail;
    private EditText etSearch;
    public static double longitude, latitude;//現在位置經緯度
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;// Location請求物件

    private RadioGroup radioGroup;
    private RadioButton rbKeyword;
    private RadioButton rbType;
    private Spinner spinner;

    private String showDetailStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPickPlace = (Button) findViewById(R.id.btnPickPlace);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        tvPlaceDetail = (TextView) findViewById(R.id.tvPlaceDetail);
        etSearch = (EditText) findViewById(R.id.etSearch);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rbKeyword = (RadioButton) findViewById(R.id.rbKeyword);
        rbType = (RadioButton) findViewById(R.id.rbType);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnPickPlace.setOnClickListener(onClickListener);
        btnSearch.setOnClickListener(onClickListener);

        btnSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ActionSheet.createBuilder(getApplicationContext(), getSupportFragmentManager())
                        .setCancelButtonTitle("Cancel")
                        .setOtherButtonTitles("Item0", "Item1", "Item2", "Item3")
                        .setCancelableOnTouchOutside(true)
                        .setListener(new ActionSheet.ActionSheetListener() {
                            @Override
                            public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                            }

                            @Override
                            public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                                Config.TOAST(getApplicationContext(), "Item" + index + " click");
                            }
                        }).show();
                return true;
            }
        });

        String[] searchTypes = getResources().getStringArray(R.array.search_types);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.myspinner, searchTypes);
        spinner.setAdapter(adapter);
        spinner.setSelection(14);//預設為cafe

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rbKeyword) {
                    etSearch.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                    spinner.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                } else if (checkedId == R.id.rbType) {
                    etSearch.setBackgroundColor(getResources().getColor(R.color.colorWhite));
                    spinner.setBackgroundColor(getResources().getColor(R.color.colorLightBlue));
                }
            }
        });

        //default location
        latitude = 25.065117;
        longitude = 121.580094;

        configGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnPickPlace:
                    myPlaces.clear();
                    showDetailStr = "";
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                    LatLng place = new LatLng(MainActivity.latitude, MainActivity.longitude);
//                    builder.setLatLngBounds(LatLngBounds.builder().include(place).build());

                    try {
                        startActivityForResult(builder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                    } catch (GooglePlayServicesRepairableException e) {
                        e.printStackTrace();
                    } catch (GooglePlayServicesNotAvailableException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.btnSearch:
                    myPlaces.clear();
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(getApplicationContext(), "check self permission failed", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    doSearch();

                    break;
            }
        }
    };

    private void doSearch() {
        //========================search by keyword=====================================
        if (rbKeyword.isChecked()) {

            String keyword = etSearch.getText().toString();
            if (keyword.isEmpty()) {
                Config.TOAST(MainActivity.this, "搜尋不得為空");
                return;
            }

            Config.showProgress(MainActivity.this);
            Ion.with(MainActivity.this)
                    .load("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + latitude + "," + longitude
                            + "&radius=" + "1000"
                            //+ "&type=" + "cafe"
                            + "&keyword=" + keyword
                            + "&language=zh-TW"
                            + "&key=" + getString(R.string.google_maps_key))
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    Config.dismissProgress();
                    if (result != null) {
                        Config.TOAST(MainActivity.this, "got result");
                        Config.LOGD("result.toString(): "  + result.toString());
//                                Config.LOGD("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + oldPlaces.get(i).getId() + "&key=" + getString(R.string.google_maps_key));
//                                Config.LOGD(result.getAsJsonObject("result").getAsJsonObject("opening_hours").toString());

                        JsonArray resultArray = result.getAsJsonArray("results");
                        if (resultArray != null) {
                            for (JsonElement placeInfo : resultArray) {
                                Config.LOGD(placeInfo.toString());
                                String placeId = placeInfo.getAsJsonObject().get("place_id").getAsString();
                                LatLng latlng = new LatLng(
                                        placeInfo.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble(),
                                        placeInfo.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble()
                                );
                                String name = placeInfo.getAsJsonObject().get("name").getAsString();
                                String address = placeInfo.getAsJsonObject().get("vicinity").getAsString();
                                String icon = placeInfo.getAsJsonObject().get("icon").getAsString();
                                JsonObject openingHours = placeInfo.getAsJsonObject().getAsJsonObject("opening_hours");

                                myPlaces.add(new MyPlace(placeId, latlng, name, address, icon, openingHours));
                            }

                            startActivity(new Intent(MainActivity.this, MapActivity.class));
                        }

                    } else {
                        Config.TOAST(MainActivity.this, "no result");
                    }
                }
            });

        }


        //==========================search by type======================================
        if (rbType.isChecked()) {

            String type = spinner.getSelectedItem().toString();
            if (type.isEmpty()) {
                Config.TOAST(MainActivity.this, "請選擇一個類別");
                return;
            }

            Config.showProgress(MainActivity.this);
            Ion.with(MainActivity.this)
                    .load("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + latitude + "," + longitude
                            + "&radius=" + "1000"
                            + "&type=" + type
                            //+ "&keyword=" + "starbucks"
                            + "&language=zh-TW"
                            + "&key=" + getString(R.string.google_maps_key))
                    .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                @Override
                public void onCompleted(Exception e, JsonObject result) {
                    Config.dismissProgress();
                    if (result != null) {
                        Config.TOAST(MainActivity.this, "got result");
                        Config.LOGD("result.toString(): "  + result.toString());
//                                Config.LOGD("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + oldPlaces.get(i).getId() + "&key=" + getString(R.string.google_maps_key));
//                                Config.LOGD(result.getAsJsonObject("result").getAsJsonObject("opening_hours").toString());

                        JsonArray resultArray = result.getAsJsonArray("results");
                        if (resultArray != null) {
                            for (JsonElement placeInfo : resultArray) {
                                Config.LOGD(placeInfo.toString());
                                String placeId = placeInfo.getAsJsonObject().get("place_id").getAsString();
                                LatLng latlng = new LatLng(
                                        placeInfo.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble(),
                                        placeInfo.getAsJsonObject().getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble()
                                );
                                String name = placeInfo.getAsJsonObject().get("name").getAsString();
                                String address = placeInfo.getAsJsonObject().get("vicinity").getAsString();
                                String icon = placeInfo.getAsJsonObject().get("icon").getAsString();
                                JsonObject openingHours = placeInfo.getAsJsonObject().getAsJsonObject("opening_hours");

                                myPlaces.add(new MyPlace(placeId, latlng, name, address, icon, openingHours));
                            }

                            startActivity(new Intent(MainActivity.this, MapActivity.class));
                        }

                    } else {
                        Config.TOAST(MainActivity.this, "no result");
                    }

                }
            });
        }
    }

    private synchronized void configGoogleApiClient() {

        try {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .build();
            locationRequest = new LocationRequest();
            // 設定讀取位置資訊的間隔時間為一秒（1000ms）
            locationRequest.setInterval(1000);
            // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
            locationRequest.setFastestInterval(1000);
            // 設定優先讀取高精確度的位置資訊（GPS）
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        } catch (Exception e) {
            e.printStackTrace();
            Config.TOAST(MainActivity.this, "Permission Denial");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Config.LOGD("onRequestPermissionsResult requestCode = " + requestCode);
        if(requestCode == 99) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Config.LOGD("onConnected");

        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                ActivityCompat.requestPermissions(this,
                        new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION,  android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        99
                );

                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, MainActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Config.TOAST(MainActivity.this, "Permission Denial");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Config.LOGD("onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Config.LOGD("onConnectionFailed");

        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        try {
            if (errorCode == ConnectionResult.SERVICE_MISSING) {
                Toast.makeText(this, R.string.google_play_service_missing, Toast.LENGTH_LONG).show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            Config.TOAST(MainActivity.this, "Permission Denial");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Config.LOGD("onLocationChanged");
            if (!isPickPlace) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            Config.LOGD("latitude = " + latitude + ", longitude = " + longitude);
        } catch (Exception e) {
            e.printStackTrace();
            Config.TOAST(MainActivity.this, "Permission Denial");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                isPickPlace = true;//這個旗標用來當onLocationChanged時,不再將經緯度給最新的位置,而是一直記著pickPlace的經緯度

                pickPlace = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", pickPlace.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                tvPlaceDetail.setText("Name: " + pickPlace.getName() + "\n"
                                    + "Rating: " + pickPlace.getRating() + "\n"
                                    + "PhoneNumber: " + pickPlace.getPhoneNumber() + "\n"
                                    + "Address: " + pickPlace.getAddress() + "\n"
                                    + "LatLng: " + pickPlace.getLatLng() + "\n"
                                    + "ID: " + pickPlace.getId()+ "\n"
                                    + "Locale: " + pickPlace.getLocale() + "\n"
                                    + "Attributions: " + pickPlace.getAttributions() + "\n"
                                    + "List PlaceTypes: " + pickPlace.getPlaceTypes().toString() + "\n"
                                    + "PriceLevel: " + pickPlace.getPriceLevel() + "\n");

                showDetailStr = tvPlaceDetail.getText().toString();

                latitude = pickPlace.getLatLng().latitude;
                longitude = pickPlace.getLatLng().longitude;
            }
        }
    }
}
