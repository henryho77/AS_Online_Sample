package com.example.mapsample;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.example.mapsample.model.OldPlace;
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
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static ArrayList<MyPlace> myPlaces = new ArrayList<>();
    public static ArrayList<OldPlace> oldPlaces = new ArrayList<>();
    private ArrayList<String> collectIDs = new ArrayList<>();
    public ArrayList<LatLng> collectLatLngs = new ArrayList<>();


    int PLACE_PICKER_REQUEST = 1;
    public static Place pickPlace;
    boolean isPickPlace = false;

    private Button btnMap, btnPickPlace, btnFilterByType, btnShow, btnDetail;
    private TextView tvPlaceDetail;
    private EditText etSearch;
    public static float longitude, latitude;//現在位置經緯度
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

        btnMap = (Button) findViewById(R.id.btnMap);
        btnPickPlace = (Button) findViewById(R.id.btnPickPlace);
        btnFilterByType = (Button) findViewById(R.id.btnFilterByType);
        btnShow = (Button) findViewById(R.id.btnShow);
        btnDetail = (Button) findViewById(R.id.btnDetail);
        tvPlaceDetail = (TextView) findViewById(R.id.tvPlaceDetail);
        etSearch = (EditText) findViewById(R.id.etSearch);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        rbKeyword = (RadioButton) findViewById(R.id.rbKeyword);
        rbType = (RadioButton) findViewById(R.id.rbType);
        spinner = (Spinner) findViewById(R.id.spinner);

        btnMap.setOnClickListener(onClickListener);
        btnPickPlace.setOnClickListener(onClickListener);
        btnFilterByType.setOnClickListener(onClickListener);
        btnShow.setOnClickListener(onClickListener);
        btnDetail.setOnClickListener(onClickListener);

        btnMap.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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

        //default location
        latitude = (float) 25.065117;
        longitude = (float) 121.580094;

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
                case R.id.btnMap:
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    break;
                case R.id.btnPickPlace:
                    collectIDs.clear();
                    collectLatLngs.clear();
                    oldPlaces.clear();
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
                case R.id.btnFilterByType:
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


                    //========================search by keyword=====================================
                    if (rbKeyword.isChecked()) {

                        String keyword = etSearch.getText().toString();
                        if (keyword.isEmpty()) {
                            Config.TOAST(MainActivity.this, "搜尋不得為空");
                            return;
                        }

                        Ion.with(MainActivity.this)
                                .load("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                        + pickPlace.getLatLng().latitude + "," + pickPlace.getLatLng().longitude
                                        + "&radius=" + "1000"
//                                        + "&type=" + "cafe"
                                        + "&keyword=" + keyword
                                        + "&key=" + getString(R.string.google_maps_key))
                                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

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
                                            String icon = placeInfo.getAsJsonObject().get("icon").getAsString();
                                            JsonObject openingHours = placeInfo.getAsJsonObject().getAsJsonObject("opening_hours");

                                            myPlaces.add(new MyPlace(placeId, latlng, name, icon, openingHours));
                                        }
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

                        Ion.with(MainActivity.this)
                                .load("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                                        + pickPlace.getLatLng().latitude + "," + pickPlace.getLatLng().longitude
                                        + "&radius=" + "1000"
                                        + "&type=" + type
//                                    + "&keyword=" + "starbucks"
                                        + "&key=" + getString(R.string.google_maps_key))
                                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {

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
                                            String icon = placeInfo.getAsJsonObject().get("icon").getAsString();
                                            JsonObject openingHours = placeInfo.getAsJsonObject().getAsJsonObject("opening_hours");

                                            myPlaces.add(new MyPlace(placeId, latlng, name, icon, openingHours));
                                        }
                                    }

                                } else {
                                    Config.TOAST(MainActivity.this, "no result");
                                }

                            }
                        });
                    }


////                    List<Integer> filters = new ArrayList<>();
////                    filters.add(Place.TYPE_FOOD);
////                    AutocompleteFilter autocompleteFilter = AutocompleteFilter.create(filters);
//                    AutocompleteFilter.Builder acfBilder = new AutocompleteFilter.Builder();
//                    acfBilder.setTypeFilter(Place.TYPE_HOSPITAL);
//                    AutocompleteFilter autocompleteFilter = acfBilder.build();
//
////                    double pickPlaceLatLngSW = pickPlace.getLatLng().latitude;
////                    double pickPlaceLngNE = pickPlace.getLatLng().longitude;
//
//                    LatLng pickPlaceLatLngSW = new LatLng(pickPlace.getLatLng().latitude - 0.01, pickPlace.getLatLng().longitude - 0.02);
//                    LatLng pickPlaceLatLngNE = new LatLng(pickPlace.getLatLng().latitude + 0.01, pickPlace.getLatLng().longitude + 0.02);
//                    Config.LOGD("pickPlace.getLatLng(): " + pickPlace.getLatLng().toString());
//                    Config.LOGD("pickPlaceLatLngSW: " + pickPlaceLatLngSW.toString());
//                    Config.LOGD("pickPlaceLatLngNE: " + pickPlaceLatLngNE.toString());
//
////                    LatLngBounds bounds = new LatLngBounds(new LatLng(25.042173799999997, 121.5082927), new LatLng(25.045024500000004, 121.5231057));
//                    LatLngBounds bounds = new LatLngBounds(pickPlaceLatLngSW, pickPlaceLatLngNE);
//                    final PendingResult<AutocompletePredictionBuffer> pendingResult = Places.GeoDataApi.getAutocompletePredictions(googleApiClient, search, bounds, autocompleteFilter);
//                    //rectangleLyon is LatLngBounds, to remove filters put autocompletefilter as null
//                    // Second parameter(as String "delhi") is your search query
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            AutocompletePredictionBuffer autocompletePredictionBuffer = pendingResult.await(10, TimeUnit.SECONDS);
//                            Status status = autocompletePredictionBuffer.getStatus();
//                            Iterator<AutocompletePrediction> iterator = autocompletePredictionBuffer.iterator();
//
//                            while (iterator.hasNext()) {
//                                AutocompletePrediction autocompletePrediction = iterator.next();
//                                // do something
//                                if (autocompletePrediction != null) {
//                                    Log.d("debug", "id: " + autocompletePrediction.getId());
//                                    showDetailStr += "id: " + autocompletePrediction.getId() + "\n";
////                                    oldPlaces.add(new OldPlace(autocompletePrediction.getId(), null, null));
//                                    collectIDs.add(autocompletePrediction.getId());
//                                }
//                            }
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    tvPlaceDetail.setText(showDetailStr);
//                                }
//                            });
//
//                        }
//                    }).start();

                    break;
                case R.id.btnShow:

//                    ArrayList<String> collectIDs = new ArrayList<>();
//                    for (OldPlace place : oldPlaces) {
//                        collectIDs.add(place.getId());
//                    }

                    Places.GeoDataApi.getPlaceById(googleApiClient, collectIDs.toArray(new String[0]))
                            .setResultCallback(new ResolvingResultCallbacks<PlaceBuffer>(MainActivity.this, 0) {
                                @Override
                                public void onSuccess(@NonNull PlaceBuffer places) {
                                    Log.d("debug", "onSuccess Result");
                                    if (places.getCount() <= 0) {
                                        Toast.makeText(MainActivity.this, "No place found", Toast.LENGTH_SHORT).show();
                                    }

                                    for (int i = 0; i < places.getCount(); i++) {
                                        Log.d("debug", String.format("Place '%s', LatLng: '%s', Viewport %s, PlaceTypes: %s",
                                                places.get(i).getName(),
                                                places.get(i).getLatLng(),
                                                places.get(i).getViewport(),
                                                places.get(i).getPlaceTypes().toString()));
//                                        oldPlaces.get(i).setLatLng(places.get(i).getLatLng());
                                        showDetailStr += "Place: " + places.get(i).getName() +  "LatLng: " + places.get(i).getLatLng() + "\n";
                                    }
                                    places.release();

//                                    for (Place place : places) {
//                                        Log.d("debug", String.format("Place '%s', LatLng: '%s', Viewport %s, PlaceTypes: %s",
//                                                place.getName(),
//                                                place.getLatLng(),
//                                                place.getViewport(),
//                                                place.getPlaceTypes().toString()));
//                                        collectLatLngs.add(place.getLatLng());
//                                        showDetailStr += "Place: " + place.getName() +  "LatLng: " + place.getLatLng() + "\n";
//                                    }
//                                    places.release();

                                    tvPlaceDetail.setText(showDetailStr);
                                }

                                @Override
                                public void onUnresolvableFailure(@NonNull Status status) {
                                    Log.d("debug", "onUnresolvableFailure");
                                }
                            });
                    break;
                case R.id.btnDetail:
//                  .load("https://maps.googleapis.com/maps/api/place/details/json?placeid=ChIJN1t_tDeuEmsRUsoyG83frY4&key=YOUR_API_KEY")
//                  .load("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + collectIDs.get(0) + "&key=" + getString(R.string.google_maps_key))
                    for (int i = 0; i < collectIDs.size(); i++) {
                        Ion.with(MainActivity.this)
                                .load("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + collectIDs.get(i) + "&key=" + getString(R.string.google_maps_key))
                                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (result != null) {
                                    Config.TOAST(MainActivity.this, "got result");
                                    Config.LOGD("result.toString(): "  + result.toString());
//                                    Config.LOGD("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + oldPlaces.get(i).getId() + "&key=" + "AIzaSyBhV1wYbX4tisfAIeEhzrmzLQTgUQVA4vE");
//                                    Config.LOGD(result.getAsJsonObject("result").getAsJsonObject("opening_hours").toString());

                                    String id = result.getAsJsonObject("result").get("id").getAsString();
                                    LatLng latlng = new LatLng(
                                            result.getAsJsonObject("result").getAsJsonObject("geometry").getAsJsonObject("location").get("lat").getAsDouble(),
                                            result.getAsJsonObject("result").getAsJsonObject("geometry").getAsJsonObject("location").get("lng").getAsDouble()
                                    );
                                    JsonObject openingHours =  result.getAsJsonObject("result").getAsJsonObject("opening_hours");
//                                    boolean flag = result.getAsJsonObject("result").getAsJsonObject("opening_hours").get("open_now").getAsBoolean();
//                                    if (flag)
//                                        Config.LOGD("true");
//                                    else
//                                        Config.LOGD("false");

                                    oldPlaces.add(new OldPlace(id, latlng, openingHours));
                                } else {
                                    Config.TOAST(MainActivity.this, "no result");
                                }

                            }
                        });
                    }

//                    Ion.with(MainActivity.this)
//                            .load("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + oldPlaces.get(0).getId() + "&key=" + getString(R.string.google_maps_key))
//                            .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
//                            @Override
//                            public void onCompleted(Exception e, JsonObject result) {
//                            if (result != null) {
//                                Config.TOAST(MainActivity.this, "got result");
//                                Config.LOGD("result.toString(): "  + result.toString());
//                                Config.LOGD("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + oldPlaces.get(0).getId() + "&key=" + "AIzaSyBhV1wYbX4tisfAIeEhzrmzLQTgUQVA4vE");
//                                Config.LOGD(result.getAsJsonObject("result").getAsJsonObject("opening_hours").toString());
//
//                                boolean flag = result.getAsJsonObject("result").getAsJsonObject("opening_hours").get("open_now").getAsBoolean();
//                                if (flag) {
//                                    Config.LOGD("true");
//                                } else {
//                                    Config.LOGD("false");
//                                }
//
//                            } else {
//                                Config.TOAST(MainActivity.this, "no result");
//                            }
//                        }
//                    });
                    break;
            }
        }
    };

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
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
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

                latitude = (float) pickPlace.getLatLng().latitude;
                longitude = (float) pickPlace.getLatLng().longitude;
            }
        }
    }
}
