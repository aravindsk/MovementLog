package com.skand.movementlog;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private ProgressBar progressBarCircleMove;
    private CountDownTimer CountDownTimerMove;
    private Button startButton,stopButton;

    TextView mTextFieldRemTime,mTextFieldMoveType;

    //Initialization for Countdowntimer variables
    long remainingSecs, startingSecs ;
    int acitivityIterator = 0, flgResetTimerStart = 1, flgNewExercise=1, newActivityId=0,newExerciseId=0;
    int[] intArray = new int[]{3600000, 1800000, 900000}; //long time for TESTING
//    int[] intArray = new int[]{15000, 10000, 5000}; //short time for DEV
    String exerActivityArray[] = {"Walk", "Jog", "Walk"};
    String currExerActivity,timeForDisplay,timeForDB;


    //Initialization for Location variables
    double currLat,currLng;
    float currLocationAccuracy;
    String currLocation;


    Location mLocation;
    TextView latLng;
    GoogleApiClient mGoogleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 5000;  /* 15 secs */
    private long FASTEST_INTERVAL = 5000; /* 5 secs */

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        // enable transitions
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);




        progressBarCircleMove = (ProgressBar) findViewById(R.id.progressBarMove);

        //StartButton rename logic
        startButton = (Button) findViewById(R.id.buttonStartMove);
        startButton.setOnClickListener(this);

        //StartButton rename logic
        stopButton = (Button) findViewById(R.id.buttonStopMove);
        stopButton.setOnClickListener(this);

        final TextView mTextFieldRemTime = (TextView) findViewById(R.id.textViewRemTime);
        final TextView mTextFieldMoveType = (TextView) findViewById(R.id.textViewMovementType);

        mTextFieldRemTime.setText("Press START");
        mTextFieldMoveType.setText("");

        latLng = (TextView) findViewById(R.id.textViewLatLng);

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }



        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    //Countdowntimer starter function
    public void startTimer() {

        final DatabaseHandler db = new DatabaseHandler(this);

        final TextView mTextFieldRemTime = (TextView) findViewById(R.id.textViewRemTime);
        final TextView mTextFieldMoveType = (TextView) findViewById(R.id.textViewMovementType);

        mTextFieldRemTime.setText("startTimer() : startingSecs : " + startingSecs);
        mTextFieldMoveType.setText(currExerActivity);

        //reset progress bar only on StartPress or NEW ACTIVITY START(to be done!)
        if (flgResetTimerStart == 1) {
            progressBarCircleMove.setMax((int) startingSecs);
            flgResetTimerStart =0;
        }


        CountDownTimerMove = new CountDownTimer(startingSecs, 1000) {


            public void onTick(long millisUntilFinished) {



                remainingSecs = millisUntilFinished;
                progressBarCircleMove.setProgress((int) (millisUntilFinished));
                timeForDisplay="Time remaining: " +
                        String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1));

                timeForDB=String.format("%02d:%02d:%02d.%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % TimeUnit.MINUTES.toSeconds(1),
                        millisUntilFinished/100);
                mTextFieldRemTime.setText(timeForDisplay);

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                //yyyy.MM.dd.HH.mm.ss//new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
                String formattedDate = sdf.format(date);
                //sqlite time format :: YYYY-MM-DD HH:MM:SS.SSS
//                Log.d("TIME", "formattedDate" + formattedDate);
                Log.d("TIME", "timeForDB" + timeForDB);
                currLocation = String.valueOf(currLat)+","+String.valueOf(currLng);


                //write to db
                db.addExerciseLog(new ExerciseLog(newExerciseId,newActivityId, timeForDB,currLocation, formattedDate));
                //db.addExerciseLog(new ExerciseLog(currLocation, formattedDate));




            }

            public void onFinish() {

                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds
                v.vibrate(400);
                flgResetTimerStart=1;
                counterExecuteManager();



            }
        }.start();

    }

    //Countdowntimer manager function: to handle list of exercise activities
    public void counterExecuteManager() {
        final DatabaseHandler db = new DatabaseHandler(this);
        newActivityId=db.getNewActivityID();

        final TextView mTextFieldMoveType = (TextView) findViewById(R.id.textViewMovementType);


        if(acitivityIterator < intArray.length )
        {
            //reset timer if previous timer is done or if its new exercise
            if(flgResetTimerStart==1) {

                startingSecs = intArray[acitivityIterator];
                currExerActivity = exerActivityArray[acitivityIterator];
                acitivityIterator++;

            }
            startTimer();
        } else {
            mTextFieldMoveType.setText("Workout completed");
            CountDownTimerMove.cancel();


        }

    }

    //Override for implements View.OnClickListener
    //Handle registered button clicks
    @Override
    public void onClick(View v) {
        Log.d("button", (String) startButton.getText());


        switch (v.getId()) {
            //Start button : pause logic and initialize timer variables
            case R.id.buttonStartMove:
                // do your code


                if (((String) startButton.getText()).equals("Start")) {
                    //If new exercise get new exercise ID from db
                    if(flgNewExercise==1) {
                        final DatabaseHandler db = new DatabaseHandler(this);
                        newExerciseId = db.getNewExerciseID();
                         db.close();
                        Log.d("DB","flgNewExercise==1 : newExerciseId = "+ newExerciseId);
                    }
                    startButton.setText("Pause");
                    flgNewExercise = 0;
                    counterExecuteManager();
                } else if ((String) startButton.getText() == "Pause") {
                    flgResetTimerStart = 0;
                    startingSecs = remainingSecs;
                    CountDownTimerMove.cancel();

                    startButton.setText("Start");
                }
                break;
            //Stop button : restart logic and reset timer variables
            case R.id.buttonStopMove:
                Log.d("StopBtn","clicked : flgNewExercise :"+flgNewExercise);
                startButton.setText("Start");
                flgNewExercise = 1;
                flgResetTimerStart=1;
                acitivityIterator=0;
                final TextView mTextFieldRemTime = (TextView) findViewById(R.id.textViewRemTime);
                final TextView mTextFieldMoveType = (TextView) findViewById(R.id.textViewMovementType);

                mTextFieldRemTime.setText("Press START");
                mTextFieldMoveType.setText("");
                if (CountDownTimerMove != null) {
                    CountDownTimerMove.cancel();
                }


                // do your code
                break;

//            case R.id.threeButton:
//                // do your code
//                break;

            default:
                break;
        }

    }

    //Location handling code follows


    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    @Override
    protected void onStart() {
        Log.d("Location", "onStart:");
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Location", "onResume:");

        if (!checkPlayServices()) {
            latLng.setText("Please install Google Play services.");
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Location", "onConnected:");
        Log.d("LOCATION","ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION : "+ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION));
        Log.d("LOCATION","ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) : "+ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION));

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

         ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d("LOCATION","inside IF :ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION : "+ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION));
            Log.d("LOCATION","inside IF :ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) : "+ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION));
            return;
        }
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d("Location", "before :if(mLocation!=null)");
        if(mLocation==null){
            Log.d("Location", "mLocation==null");

        }

        if(mLocation!=null)
        {
            // mLocation.getLatitude();
            latLng.setText("Latitude : "+mLocation.getLatitude()+" , Longitude : "+mLocation.getLongitude());
            currLat= mLocation.getLatitude();
            currLng=mLocation.getLongitude() ;
            currLocationAccuracy = mLocation.getAccuracy();
            Log.d("LOCATION","mLocation.getAccuracy() : "+mLocation.getAccuracy());
        }

        startLocationUpdates();



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Location", "onLocationChanged:");

        if(location!=null) {
            latLng.setText("Latitude : " + location.getLatitude() + " , Longitude : " + location.getLongitude());
            currLat= location.getLatitude();
            currLng=location.getLongitude() ;
            currLocationAccuracy = location.getAccuracy();
            Log.d("LOCATION","mLocation.getAccuracy() : "+mLocation.getAccuracy()+"@"+location.getTime());
        }



    }

    private boolean checkPlayServices() {
        Log.d("Location", "checkPlayServices:");
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else
                finish();

            return false;
        }
        return true;
    }

    protected void startLocationUpdates() {
        Log.d("Location", "startLocationUpdates:");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(), "Enable Permissions", Toast.LENGTH_LONG).show();
            Log.d("Location", "startLocationUpdates: IF TOAST");
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);


    }

    private boolean hasPermission(String permission) {
        Log.d("Location", "hasPermission:");
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        Log.d("Location", "canMakeSmores:");
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("Location", "onRequestPermissionsResult:");

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        Log.d("Location", "showMessageOKCancel:");
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        Log.d("Location", "onDestroy:");
        super.onDestroy();
        stopLocationUpdates();
    }


    public void stopLocationUpdates()
    {Log.d("Location", "stopLocationUpdates:");
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }


    public void viewExerciseDetails(View view) {
        //CountDownTimerMove.cancel();
        //Intent to display a message

//        getWindow().setExitTransition(new Explode());
        Intent intent = new Intent(this, MovementDetailsActivity.class);
        startActivity(intent
//                , ActivityOptions
//                .makeSceneTransitionAnimation(this).toBundle()
        );
    }


}
