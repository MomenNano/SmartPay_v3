package com.android.nfc.smartpay_v3.Fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;

import com.android.nfc.smartpay_v3.Classes.Companey;
import com.android.nfc.smartpay_v3.DBA.Configuration;
import com.android.nfc.smartpay_v3.DBA.MySingleton;
import com.android.nfc.smartpay_v3.R;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.location.LocationListener;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fifty on 5/7/2018.
 */

public class MapFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    MapView mMapView;
    ArrayList<Companey> companeyArrayList = new ArrayList<Companey>();
    private GoogleMap googleMap;
    View collapseView;

    public int [] companeyImage= {
            R.mipmap.cafe_icon,
            R.mipmap.resturant_icon,
            R.mipmap.game_icon,
            R.mipmap.supermarket
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
        LinearLayout collapsingToolbarContent = (LinearLayout) getActivity().findViewById(R.id.collapsing_toolbar_content);
        collapsingToolbarContent.setVisibility(View.GONE);
        collapsingToolbarContent.removeAllViewsInLayout();
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Map");
        collapsingToolbarLayout.setTitle("Map");
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap mMap) {
                googleMap = mMap;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
                getStoresFromServer();
                // For dropping a marker at a point on the Map

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title("Latitude:"+latLng.latitude+" Longitude:"+latLng.longitude);
                        mMap.addMarker(markerOptions);
                    }
                });


            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity().getBaseContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        Bitmap currentLocationMarker = Bitmap.createScaledBitmap(((BitmapDrawable) getActivity().getResources().getDrawable(R.mipmap.person_icon)).getBitmap(), 100, 100, false);
        markerOptions = markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currentLocationMarker));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }


    }
    public void setStoresLocationsMarkers(){
        if (companeyArrayList != null){
            for (int i = 0 ;i<companeyArrayList.size() ;i++){
                Companey companey = companeyArrayList.get(i);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(companey.getCompaneyLocation());
                markerOptions.title(companey.getName());
                Bitmap currentLocationMarker = Bitmap.createScaledBitmap(((BitmapDrawable) getActivity()
                        .getResources().getDrawable(companeyImage[companey.getType()])).getBitmap(), 100, 100, false);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(currentLocationMarker));
                googleMap.addMarker(markerOptions);
            }
        }
    }
    public void getStoresFromServer(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, Configuration.GET_STORES_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        while (count<response.length()){
                            try {
                                JSONObject jsonObject = response.getJSONObject(count);
                                Companey companey = new Companey();
                                companey.setName(jsonObject.getString(Configuration.KEY_COMPANY_NAME));
                                companey.setType(jsonObject.getInt(Configuration.KEY_COMPANY_TYPE));
                                companey.setCompaneyLocation(new LatLng(
                                        jsonObject.getDouble(Configuration.KEY_COMPANY_LATITUDE),
                                        jsonObject.getDouble(Configuration.KEY_COMPANY_LONGITUDE))
                                );
                                companeyArrayList.add(companey);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity().getBaseContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            count++;
                        }
                        setStoresLocationsMarkers();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity().getBaseContext(), "Check Your Internet Connection And Open The Activity Again", Toast.LENGTH_SHORT).show();
                    }
                });
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(jsonArrayRequest);

    }


}