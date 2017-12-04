package cecs453.android.csulb.edu.recipeapp;

/**
 * Created by aenah on 11/28/17.
 */
import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.app.Activity.RESULT_OK;


public class FragmentMap extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int FINE_LOCATION_PERMISSION_REQUEST = 1;
    private static final int CONNECTION_RESOLUTION_REQUEST = 2;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    public boolean permission = false;

    Button showLoc;

    public FragmentMap() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, null, false);


        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        buildGoogleAPIClient();

        showLoc = (Button) rootView.findViewById(R.id.showStore);
        showLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // hello
                if(permission == true){

                }
                else{
                    Toast.makeText(getActivity(), "App does not have access to location.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    permission = true;
                    Toast.makeText(getActivity(), "Permission granted!", Toast.LENGTH_SHORT).show();
                    findLocation();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied.", Toast.LENGTH_SHORT).show();
                    permission= false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        findLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "Connection suspended", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), getActivity(), 1);
            dialog.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        buildGoogleAPIClient();
    }

    private void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONNECTION_RESOLUTION_REQUEST && resultCode == RESULT_OK) {
            mGoogleApiClient.connect();
        }
    }

    private void findLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST);

        } else { // permission granted already
            permission = true;
            Toast.makeText(getActivity(), "Permission already granted.", Toast.LENGTH_SHORT).show();
            double latitude = -34; //filler number - should not be displayed
            double longitude = 151; // filler number - should not be displayed

            GPSTracker tracker = new GPSTracker(getActivity());
            if (!tracker.canGetLocation()) {
                tracker.showSettingsAlert();
            } else {
                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();
            }

            LatLng myLat = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(myLat).title("Current Location"));
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myLat, 16);
            mMap.animateCamera(yourLocation);
        }
    }
}