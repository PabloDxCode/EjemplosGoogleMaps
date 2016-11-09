package com.pablo.pgutierrezd.ejemplogooglemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class SecondActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener{

    private FirstMapFragment mFirstMapFragment;
    private GoogleMap mMap;
    private Spinner address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        address = (Spinner) findViewById(R.id.address);

        String addresses[] = new String[]{"Selecciona","Candiles","Lomas de casa blanca", "Reforma Agraria"};

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,addresses);
        address.setAdapter(adapter);

        mFirstMapFragment = FirstMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mFirstMapFragment)
                .commit();

        mFirstMapFragment.getMapAsync(this);

        address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(mMap != null){
                    mMap.clear();
                }
                if (position == 1) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constanst.CANDILES, 14));
                    Polygon cubaPolygon = mMap.addPolygon(new PolygonOptions()
                            .add(Constanst.CANDILES_COORDS)
                            .strokeColor(getResources().getColor(R.color.rojo))
                            .fillColor(getResources().getColor(R.color.rojo_transparente))
                            .strokeWidth(3));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //googleMap.moveCamera(CameraUpdateFactory.zoomBy(80));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constanst.QUERETARO, 14));

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constanst.LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
