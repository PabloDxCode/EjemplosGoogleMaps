package com.pablo.pgutierrezd.ejemplogooglemaps;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.kml.KmlLayer;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleApiClient.OnConnectionFailedListener{

    private FirstMapFragment mFirstMapFragment;
    private static final int LOCATION_REQUEST_CODE = 1;
    private GoogleMap mMap;

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));

    private Button btnIr;
    EditText lnglat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAutocompleteView = (AutoCompleteTextView) findViewById(R.id.autocomplete_places);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        mFirstMapFragment = FirstMapFragment.newInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.map_container, mFirstMapFragment)
                .commit();

        mFirstMapFragment.getMapAsync(this);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        mAdapter = new PlaceAutocompleteAdapter(this, mGoogleApiClient, BOUNDS_GREATER_SYDNEY,
                null);
        mAutocompleteView.setAdapter(mAdapter);

        btnIr = (Button) findViewById(R.id.btnIr);

        lnglat = (EditText) findViewById(R.id.lnglat);

        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SecondActivity.class));
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.moveCamera(CameraUpdateFactory.zoomBy(100));

        LatLng queretaro = new LatLng(20.5872194, -100.387161);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(queretaro, 15));

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
                        LOCATION_REQUEST_CODE);
            }
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        Log.d("AA","Posicion de camara: "+mMap.getCameraPosition().target.longitude + ", "+mMap.getCameraPosition().target.latitude);

        /*try {
            KmlLayer layer = new KmlLayer(mMap, R.raw.doc, getApplicationContext());
            layer.addLayerToMap();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (permissions.length > 0 &&
                    permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                Toast.makeText(this, "Error de permisos", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        lnglat.setText("Latitud: "+marker.getPosition().latitude +", Longitud: "+marker.getPosition().longitude);
        Log.d("AA","Latitud: "+marker.getPosition().latitude +", Longitud: "+marker.getPosition().longitude+":"
                            +marker.getPosition().latitude +","+marker.getPosition().longitude);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            /*Toast.makeText(getApplicationContext(), "Clicked: " + primaryText + ", colonia ",
                    Toast.LENGTH_SHORT).show();*/
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                places.release();
                return;
            }
            final Place place = places.get(0);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            int radius = 80;

            mMap.clear();

            LatLng[] candiles = new LatLng[]{
                    new LatLng(20.54829214641088,-100.40625415742397),
                    new LatLng(20.547797682634307,-100.40606774389744),
                    new LatLng(20.54781055441004,-100.4035836830735),
                    new LatLng(20.54622700391372,-100.40274146944286),
                    new LatLng(20.54640909421866,-100.40223520249128),
                    new LatLng(20.546103621911403,-100.40194083005191),
                    new LatLng(20.546968549626197,-100.40103960782288),
                    new LatLng(20.547798624471596,-100.3896341845393),
                    new LatLng(20.54010550398346,-100.38742437958717),
                    new LatLng(20.538607900015485,-100.38911081850527),
                    new LatLng(20.53707511699442,-100.39580561220646),
                    new LatLng(20.53636711801179,-100.39669811725616),
                    new LatLng(20.537210437047747,-100.39735525846481),
                    new LatLng(20.537007927874736,-100.39828900247814),
                    new LatLng(20.538353902416194,-100.39824608713388),
                    new LatLng(20.538350762764615,-100.39964888244867),
                    new LatLng(20.537242461729612,-100.39966061711311),
                    new LatLng(20.536785324458922,-100.40388878434896),
                    new LatLng(20.543864205566166,-100.4071007296443),
                    new LatLng(20.544098414952046,-100.40672287344933),
                    new LatLng(20.546205655381,-100.40630880743265),
                    new LatLng(20.547565362593332,-100.40733240544796)
            };

            Polygon cubaPolygon = mMap.addPolygon(new PolygonOptions()
                    .add(candiles)
                    .strokeColor(getResources().getColor(R.color.rojo))
                    .fillColor(getResources().getColor(R.color.rojo_transparente))
                    .strokeWidth(3));

            /*Polygon cubaPolygon = mMap.addPolygon(new PolygonOptions()
                    .add(place.getLatLng(),place.getViewport().southwest, place.getViewport().northeast)
                    .strokeColor(Color.parseColor("#AB47BC"))
                    .fillColor(Color.parseColor("#7B1FA2")));*/

            Toast.makeText(getApplicationContext(), "Colonia " + place.getAddress(),
                    Toast.LENGTH_SHORT).show();


            places.release();
        }
    };



    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        //Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }
}
