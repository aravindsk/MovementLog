package com.skand.movementlog;

import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import static android.R.id.list;

public class MovementDetailsActivity extends AppCompatActivity {
    int entered_exercise_id;
    String[] markerLatLngList, markerTimeStampList,polylineLatLngList, exerciseDetails;

    float distance = 0, distanceInExercise = 0;

    java.sql.Timestamp start_ts, exer_start_ts, end_ts = java.sql.Timestamp.valueOf("2007-09-23 10:10:10.0");

    long milliseconds1, milliseconds2, diff, diffSeconds, diffMinutes, diffHours;
    String exerciseTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_details);


        final TextView mTextFieldDbActDets = (TextView) findViewById(R.id.textViewExerList);
        mTextFieldDbActDets.setMovementMethod(new ScrollingMovementMethod());


        //get details from DB
        final DatabaseHandler db = new DatabaseHandler(this);

        String exerciseDbList = "";
        //get two dimensional array max(column_name1),column_name2
        String[][] dbActSumm = db.getActivitySummary();
//        dbActSumm.length


        for (int i = 0; i < dbActSumm.length; i++) {
            if (dbActSumm[i][0] != null)
                exerciseDbList = exerciseDbList + dbActSumm[i][0] + " : " + dbActSumm[i][1] + System.lineSeparator();


        }


        mTextFieldDbActDets.setText(exerciseDbList);
        db.close();
    }

    public void viewMap(View view) {


        populateExerciseDetails(null);

        Log.d("LocTS", "exer_start_ts.getTime() : " + exer_start_ts.getTime());
        Log.d("LocTS", "exer_start_ts.toString() : " + exer_start_ts.toString());


        Log.d("LocTS", "diff : " + exer_start_ts.toString());
        Log.d("LocTS", "exerciseTime : " + exerciseTime);

        exerciseDetails = new String[3];
        exerciseDetails[0] = exer_start_ts.toString();
        exerciseDetails[1] = end_ts.toString();
        exerciseDetails[2] = exerciseTime;
        Log.d("MARKERDEBUG", "markerLatLngList.length() : " + markerLatLngList.length);

        Bundle bundleMarkerLatLng = new Bundle();
        bundleMarkerLatLng.putStringArray("MARKER_LATLNG_LIST", markerLatLngList);

        Bundle bundleTimeStampList = new Bundle();
        bundleTimeStampList.putStringArray("MARKER_TIMESTAMP_LIST", markerTimeStampList);

        Bundle bundlePolyLineLatLng = new Bundle();
        bundlePolyLineLatLng.putStringArray("POLYLINE_LATLNG_LIST", polylineLatLngList);

        Bundle bundleExerciseDetails = new Bundle();
        bundleTimeStampList.putStringArray("EXERCISE_DETAILS", exerciseDetails);

        Bundle bundleDistanceInExercise = new Bundle();
        bundleTimeStampList.putFloat("DISTANCE_IN_EXERCISE", distanceInExercise);


        Intent intent = new Intent(this, MapDisplayActivity.class);
        intent.putExtras(bundleMarkerLatLng);
        intent.putExtras(bundleTimeStampList);
        intent.putExtras(bundlePolyLineLatLng);
        intent.putExtras(bundleExerciseDetails);
        intent.putExtras(bundleDistanceInExercise);
        startActivity(intent);
    }

    public void populateExerciseDetails(View view) {
        distance = 0;
        distanceInExercise = 0;
        String exerciseDbList = "";
//        Timestamp start_ts = Timestamp.valueOf("00");
//        Timestamp end_ts= Timestamp.valueOf("00");


        final TextView mTextFieldDbActDets = (TextView) findViewById(R.id.textViewExerDetails);
        mTextFieldDbActDets.setMovementMethod(new ScrollingMovementMethod());

        EditText editTextExerciseID = (EditText) findViewById(R.id.editTextExerciseId);

        entered_exercise_id = Integer.parseInt(editTextExerciseID.getText().toString());

        Log.d("UI", "populateExerciseDetails: entered_exercise_id=" + entered_exercise_id);
//get details from DB
        final DatabaseHandler db = new DatabaseHandler(this);

        Location locationStartPoint = new Location("point A");

//        locationStartPoint.setLatitude(latA);
//        locationStartPoint.setLongitude(lngA);

        Location locationEndPoint = new Location("point B");
//
//        locationEndPoint.setLatitude(latB);
//        locationEndPoint.setLongitude(lngB);


        int exerciseId = 0, activityId = 0;

        List<ExerciseLog> exerciseLogList = db.getExerciseDetails(entered_exercise_id);

        List<String> latLngList;
        Log.d("List", "MovementDetailsActivity.onCreate: size =" + exerciseLogList.size());

        //initialize start and end point locations
        latLngList = Arrays.asList(exerciseLogList.get(0)._lat_lng.split(","));
        locationStartPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
        locationStartPoint.setLongitude(Double.parseDouble(latLngList.get(1)));

        locationEndPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
        locationEndPoint.setLongitude(Double.parseDouble(latLngList.get(1)));

        //test github what is this


        int recordGrainSplitter = 1;//time interval for which logs to be generated. In secs.

        int markerPointsCount = (int) (exerciseLogList.size()*0.05);//x% of total points
        if(markerPointsCount<0)markerPointsCount=1;
        int markerArrayIndex = 0, markerArraySize = 0;
        Log.d("ArrSize", "exerciseLogList.size :" + exerciseLogList.size());
//        Log.d("ArrSize", "markerPointsCount :" + markerPointsCount);

//        Log.d("ArrSize", "exerciseLogList.size()%markerPointsCount+1 :" + exerciseLogList.size() % markerPointsCount );
//

        Log.d("ArrSize", "exerciseLogList.size() : "+String.valueOf(exerciseLogList.size())) ;
        markerArraySize =markerPointsCount;
        Log.d("ArrSize", "(markerArraySize:)" +markerArraySize);
        markerLatLngList = new String[markerArraySize];

        markerTimeStampList = new String[markerArraySize];
        polylineLatLngList = new String[exerciseLogList.size()-1];

//Initialize start point - sample git test
        latLngList = Arrays.asList(exerciseLogList.get(0)._lat_lng.split(","));
        locationStartPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
        locationStartPoint.setLongitude(Double.parseDouble(latLngList.get(1)));
        start_ts = Timestamp.valueOf(exerciseLogList.get(0)._creation_ts);

        //get start time of exercise. End time will be in end_ts. start_ts gets updated for each activity, hence this var.
        exer_start_ts = start_ts;
//        Log.d("ArrSize", "markerLatLngList.length :" + markerLatLngList.length);
//        Log.d("ArrSize", "markerArraySize :" + markerArraySize);
        for (int i = 1; i < exerciseLogList.size(); i++) {
            //Values to be passed as intents


            latLngList = Arrays.asList(exerciseLogList.get(i)._lat_lng.split(","));
//                    Log.d("Location","latLngList[0]"+ latLngList.get(0));
//                    Log.d("Location","latLngList[1]"+ latLngList.get(1));
//            if (i == 0) {
//                        exerciseId= exerciseLogList.get(i)._exercise_id;
//
//                                activityId=exerciseLogList.get(i)._activity_id;


//                        Log.d("Location","i==0 : A->Lat[0]"+ latLngList.get(0));
//                        Log.d("Location","i==0 : A->Lng[1]"+ latLngList.get(1));
//                locationStartPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
//                locationStartPoint.setLongitude(Double.parseDouble(latLngList.get(1)));
//                start_ts = Timestamp.valueOf(exerciseLogList.get(i)._creation_ts);
//
//                //get start time of exercise. End time will be in end_ts. start_ts gets updated for each activity, hence this var.
//                exer_start_ts = start_ts;
//                                                Log.d("Location","StartTime : "+ start_ts);

//            }
//            if (i % recordGrainSplitter == 0 || i == exerciseLogList.size() - 1) {
//                Log.d("Location", "in loop :i: " + i);
//                        Log.d("LOCATIONDEBUG","loop begin :Start point : " + locationStartPoint.getLatitude()+","+locationStartPoint.getLongitude() );

//                        Log.d("Location","i==0 : B->Lng[1]"+ latLngList.get(1));

                end_ts = Timestamp.valueOf(exerciseLogList.get(i)._creation_ts);


                locationEndPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
                locationEndPoint.setLongitude(Double.parseDouble(latLngList.get(1)));
//                        Log.d("LOCATIONDEBUG","befor distanceTo :Start point : " + locationStartPoint.getLatitude()+","+locationStartPoint.getLongitude() );
//                        Log.d("LOCATIONDEBUG","befor distanceTo :End point : " + locationEndPoint.getLatitude()+","+locationEndPoint.getLongitude());
                distance = locationStartPoint.distanceTo(locationEndPoint);
                distanceInExercise = distanceInExercise + distance;

//                            Log.d("Location", "StartTime : " + start_ts);
//                            Log.d("Location", "EndTime : " + end_ts);
//                        Log.d("Location","distance in km : "+ distance/1000);
//                        if (distance != 0) {
//
//
//                        Log.d("Location", "StartTime : " + start_ts);
//                        Log.d("Location", "EndTime : " + end_ts);
//                            Log.d("Location", "distance : " + distance);
//                            Log.d("Location", "distanceInExercise : " + distanceInExercise);
//                    }
                //reset start point to current point
                locationStartPoint.setLatitude(Double.parseDouble(latLngList.get(0)));
                locationStartPoint.setLongitude(Double.parseDouble(latLngList.get(1)));
                start_ts = Timestamp.valueOf(exerciseLogList.get(i)._creation_ts);
            polylineLatLngList[i-1]=exerciseLogList.get(i)._lat_lng;


                //set markers for map only for limited points
                if (i% (exerciseLogList.size()/ markerPointsCount)==0  &&markerArrayIndex<=markerArraySize) {

                    Log.d("LOCATIONDEBUG", "Set marker point : " + i + "  markerArrayIndex:" +markerArrayIndex+" : "+ exerciseLogList.get(i)._lat_lng);
//                    Log.d("LOCATIONDEBUG", "markerArrayIndex : " + markerArrayIndex);
                    markerLatLngList[markerArrayIndex] = exerciseLogList.get(i)._lat_lng;
                    markerTimeStampList[markerArrayIndex] = exerciseLogList.get(i)._creation_ts;
                    markerArrayIndex++;
                }


//            }

        }
//        exerciseDbList =  "Exercise id:"+ exerciseId + System.lineSeparator()+"Activity id: " +activityId + System.lineSeparator()+"Total distance:"+distanceInExercise+ System.lineSeparator()+exerciseDbList ;
//        mTextFieldDbActDets.setText(exerciseDbList);

        milliseconds1 = exer_start_ts.getTime();
        milliseconds2 = end_ts.getTime();

        diff = milliseconds2 - milliseconds1;
        diffSeconds = diff / 1000;
        diffMinutes = diff / (60 * 1000);
        diffHours = diff / (60 * 60 * 1000);
        exerciseTime = diffHours + ":" + diffMinutes + ":" + diffSeconds;

        //long diffDays = diff / (24 * 60 * 60 * 1000);
    }
}
