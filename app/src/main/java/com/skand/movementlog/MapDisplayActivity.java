package com.skand.movementlog;

import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Boolean.TRUE;


public class MapDisplayActivity extends FragmentActivity implements OnMapReadyCallback {
    private String[] markerLatlngLists;
    private String[] polylineLatlngList;
    private String[] markerTimeStampList;
    private String[] exerciseDetails;
    float distanceInExercise;

    ArrayList<LatLng> polylineLatLngListMap = new ArrayList<LatLng>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_display);

        String exerciseSummaryDisplay="";

        Bundle b = this.getIntent().getExtras();

        markerLatlngLists = b.getStringArray("MARKER_LATLNG_LIST");
        markerTimeStampList = b.getStringArray("MARKER_TIMESTAMP_LIST");
        polylineLatlngList =b.getStringArray("POLYLINE_LATLNG_LIST");
        Log.d("MAPDISP","markerLatlngLists.length :"+markerLatlngLists.length);
        Log.d("MAPDISP","polylineLatlngList.length :"+polylineLatlngList.length);

        exerciseDetails = b.getStringArray("EXERCISE_DETAILS");
        distanceInExercise = b.getFloat("DISTANCE_IN_EXERCISE");
        for(int i=0;i<exerciseDetails.length;i++){

            if(i==0)
            exerciseSummaryDisplay=exerciseSummaryDisplay+"Start time :"+exerciseDetails[i]+System.lineSeparator() ;
            if(i==1)
                exerciseSummaryDisplay=exerciseSummaryDisplay+"End time :"+exerciseDetails[i]+System.lineSeparator() ;
            if(i==2)
                exerciseSummaryDisplay=exerciseSummaryDisplay+"Duration :"+exerciseDetails[i]+System.lineSeparator() ;

        }
        exerciseSummaryDisplay=exerciseSummaryDisplay+"Distance :"+distanceInExercise ;

        final TextView mTextFieldExerciseDetails = (TextView) findViewById(R.id.textViewExerciseDetails);

        mTextFieldExerciseDetails.setText(exerciseSummaryDisplay);



        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we
     * just add a marker near Africa.
     */
    @Override
    public void onMapReady(GoogleMap map) {


        //Draw the markers
        Log.d("MarkerINMAP","markerLatlngLists.length :"+ markerLatlngLists.length);
        for (int i = 0; i < markerLatlngLists.length; i++) {

//            Log.d("MarkerINMAP","markerLatlngLists LOOP :markerLatlngLists[i] :"+markerLatlngLists[i]);


            List<String> latLngList = Arrays.asList(markerLatlngLists[i].split(","));
            //initialize camera to center at start poing and set default zoom
            if (i == 0) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(latLngList.get(0)), Double.parseDouble(latLngList.get(1))), 17));
            }

            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latLngList.get(0)), Double.parseDouble(latLngList.get(1)))).title(String.valueOf(i)).snippet(markerTimeStampList[i]));

//            polylineLatLngList.add(new LatLng(Double.parseDouble(latLngList.get(0)), Double.parseDouble(latLngList.get(1))));

        }

        for (int i = 0; i < polylineLatlngList.length; i++) {

//            Log.d("MarkerINMAP","polylineLatlngList LOOP:polylineLatlngList[i] :"+polylineLatlngList[i]);
            List<String> latLngList = Arrays.asList(polylineLatlngList[i].split(","));
            polylineLatLngListMap.add(new LatLng(Double.parseDouble(latLngList.get(0)), Double.parseDouble(latLngList.get(1))));


        }

        Polyline polyline1 = map.addPolyline(new PolylineOptions()
                .clickable(true)
                .addAll(polylineLatLngListMap)
        );
        // Position the map's camera near HOME. Update this as start point
        map.getUiSettings().setZoomControlsEnabled(true);


    }
}
