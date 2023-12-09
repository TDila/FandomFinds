package com.vulcan.fandomfinds.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;
import com.vulcan.fandomfinds.Animations.LoadingDialog;
import com.vulcan.fandomfinds.Domain.StoreLocationDomain;
import com.vulcan.fandomfinds.R;
import com.vulcan.fandomfinds.Service.DirectionApi;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 10;
    private GoogleMap map;
    private Location currentLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Marker marker_current,marker_pin;
    public static final String TAG = MapActivity.class.getName();
    private Polyline polyline;
    private SearchView mapSearchView;
    private LatLng store;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore firestore;
    private Button storeLocationButton;
    private String location;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initComponents();

        setListener();

    }

    private void loadDirection() {
        Intent intent = getIntent();
        String latString = intent.getStringExtra("lat");
        String lngString = intent.getStringExtra("lng");
        if(latString != null & lngString != null){
            mapSearchView.setVisibility(View.GONE);
            storeLocationButton.setVisibility(View.GONE);

            LatLng end  = new LatLng(Double.parseDouble(latString),Double.parseDouble(lngString));

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(end,10));
            map.addMarker(new MarkerOptions().position(end));
            LatLng start = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
            getDirection(start,end);
        }
    }

    private void initComponents() {
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        mapSearchView = findViewById(R.id.mapSearch);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);

        storeLocationButton = findViewById(R.id.storeLocationButton);
        loadingDialog = new LoadingDialog(MapActivity.this);
    }


    private void setListener() {
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                location = mapSearchView.getQuery().toString();
                System.out.println(location);
                List<Address> addressList = null;

                if(location != null){
                    Geocoder geocoder = new Geocoder(MapActivity.this);

                    try {
                        addressList = geocoder.getFromLocationName(location,1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Address address = addressList.get(0);
                    store = new LatLng(address.getLatitude(),address.getLongitude());
                    if(marker_pin == null){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(store);
                        marker_pin = map.addMarker(markerOptions);
                    }else{
                        marker_pin.setPosition(store);
                    }
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(store,10));
                    LatLng start = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                    getDirection(start,store);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        storeLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(store != null){
                    loadingDialog.show();
                    searchLocation();
                }else{
                    Toast.makeText(MapActivity.this,"Select a Location first!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void searchLocation(){
        firestore.collection("Sellers").whereEqualTo("email",user.getEmail()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        snapshot.getReference().collection("Stores").whereEqualTo("lat", store.latitude).whereEqualTo("lng", store.longitude).get().
                                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().size() == 0) {
                                                saveLocation(snapshot);
                                            }else{
                                                loadingDialog.cancel();
                                                Toast.makeText(MapActivity.this,"Location Already Saved! Pick another Store.",Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }
                                });
                    }

                }
            }
        });
    }

    private void saveLocation(QueryDocumentSnapshot snapshot) {
        String id = "STR_"+String.format("%06d", new Random().nextInt(999999));
        StoreLocationDomain storeLocationDomain = new StoreLocationDomain(id,location,store.latitude,store.longitude);
        snapshot.getReference().collection("Stores").add(storeLocationDomain).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                loadingDialog.cancel();
                Toast.makeText(MapActivity.this,"Location Saved",Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.cancel();
                Toast.makeText(MapActivity.this,"Failed Location Saving!",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setZoomControlsEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);

        if(checkPermission()){
            getLastLocation();
        }else{
            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }

    private boolean checkPermission(){
        boolean permission = false;
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            permission = true;
        }

        return permission;
    }

    private void getLastLocation(){
        if(checkPermission()) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location != null){
                        currentLocation = location;
                        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title("My Location"));
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,30));
                        loadDirection();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else{
                Snackbar.make(findViewById(R.id.container), "Location permission denied!",Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }).show();
            }
        }
    }

    public void getDirection(LatLng start, LatLng end){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DirectionApi directionApi = retrofit.create(DirectionApi.class);

        String origin = start.latitude+","+start.longitude;
        String destination = end.latitude+","+end.longitude;
        String key = "AIzaSyBNQ4OsArtQTjhv3GRAESJ9k3jtsxmMNEE";
        
        Call<JsonObject> apiJson = directionApi.getJson(origin,destination,true,key);
        apiJson.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject body = response.body();
                JsonArray routes = body.getAsJsonArray("routes");

                JsonObject route = routes.get(0).getAsJsonObject();
                JsonObject overviewPolyline = route.getAsJsonObject("overview_polyline");

                List<LatLng> points = PolyUtil.decode(overviewPolyline.get("points").getAsString());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(polyline == null){
                            PolylineOptions polylineOptions = new PolylineOptions();
                            polylineOptions.width(20);
                            polylineOptions.color(getColor(android.R.color.holo_blue_light));
                            polylineOptions.addAll(points);
                            polyline =  map.addPolyline(polylineOptions);
                        }else{
                            polyline.setPoints(points  );
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
}