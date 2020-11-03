package com.ventrux.androidtest;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ventrux.androidtest.Activity.PlaceListActivity;
import com.ventrux.androidtest.Model.StaticData;
import com.ventrux.androidtest.Sqlite.DatabaseHelper;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SearchView searchView;
    TextView searchtext;
    DatabaseHelper myDb;
   private  String API_KEY="AIzaSyCWf9xjaO1kGxaQyIUHzfKNfUatQ-V7gX0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        myDb = new DatabaseHelper(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        searchView=findViewById(R.id.sv_location);
        searchtext=findViewById(R.id.textview);
        requestMultiplePermissions();
        findViewById(R.id.myplace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PlaceListActivity.class));
            }
        });
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),API_KEY);
        }
        PlacesClient placesClient=Places.createClient(this);
        final List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        searchtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

       /* // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected( Place place) {
                // TODO: Get info about the selected place.
                Log.d("places", "Place: " + place.getName() + ", " + place.getId());
            }


            @Override
            public void onError( Status status) {
                // TODO: Handle the error.
                Log.d("places", "An error occurred: " + status);
            }
        });*/


       /* // Start the autocomplete intent.
       List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);*/


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location=searchView.getQuery().toString();

                if (location!=null || !location.equals("")){


                        adddatatomap(location);

                        //
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mapFragment.getMapAsync(this);
    }

    private void requestMultiplePermissions() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
                            requestMultiplePermissions();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (StaticData.postion==1){
            Geocoder geocoder=new Geocoder(MapsActivity.this);
            List<Address> addresses=null;
            try{
                addresses=geocoder.getFromLocationName(StaticData.location,1);
            }catch (IOException e){

            }
            Address address=addresses.get(0);
           StaticData.lat=address.getLatitude();
           StaticData.longi=address.getLongitude();

        }
    }

    void adddatatomap(String location){
        Geocoder geocoder=new Geocoder(MapsActivity.this);
        List<Address> addresses=null;
        try{
            addresses=geocoder.getFromLocationName(location,1);
        }catch (IOException e){

        }
        Address address=addresses.get(0);
        mMap.clear();
        LatLng latLng=new LatLng(address.getLatitude(),address.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in "+location));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));
        addtodatabase(location);
    }

    private void addtodatabase(String location) {
        //  String id1 = productListModels.get(0).getId();
       //id product
        String title1 =location;

        //String totalprice1=price1;

        boolean isInserted = myDb.insertData(title1);
        if (isInserted == true) {
            Toast.makeText(getApplicationContext(), "Added ", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already Added", Toast.LENGTH_LONG).show();
        }
        //addtocartfun(0);

        Cursor res = myDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            Toast.makeText(getApplicationContext(), "No Data Inserted", Toast.LENGTH_LONG).show();
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while (res.moveToNext()) {
            buffer.append("id :" + res.getString(0));
            buffer.append("title :" + res.getString(1));

        }
        // Show all data
        Log.d("alldata", buffer.toString());

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(StaticData.lat ,StaticData.longi);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Kolkata"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,10));

        // Add a marker in Sydney and move the camera

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.d("places", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.d("places", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*private void getcount() {
        getalldata();
        TextView count=findViewById(R.id.cartcount);
        Cursor ress = myDb.getAllData();


        if (ress.getCount() == 0) {
            // show message
            count.setText(String.valueOf(ress.getCount()));
        }else{
            count.setText(String.valueOf(ress.getCount()));
        }
    }*/

}
