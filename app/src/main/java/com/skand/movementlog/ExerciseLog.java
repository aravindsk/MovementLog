package com.skand.movementlog;

/**
 * Created by user on 24-06-2017.
 * DAO for ExerciseLog table
 */

public class ExerciseLog {



    //private variables
    int _id;
    int _exercise_id;
    int _activity_id;
    String _activity_ts;
    String _lat_lng;
    String _creation_ts;



    float _lat_lng_accuracy;
    long _lat_lng_time;


    // Empty constructor
    public ExerciseLog(){

    }
    // constructor

    public ExerciseLog(int id,int exercise_id,int activity_id,String activity_ts , String lat_lng,float lat_lng_accuracy,long lat_lng_time, String creation_ts){
        this._id = id;
        this._exercise_id=exercise_id;
        this._activity_id = activity_id;
        this._activity_ts = activity_ts;
        this._lat_lng = lat_lng;
        this._lat_lng_accuracy = lat_lng_accuracy;
        this._lat_lng_time = lat_lng_time;
        this._creation_ts = creation_ts;
    }

    public ExerciseLog(int exercise_id,int activity_id,String activity_ts , String lat_lng,float lat_lng_accuracy,long lat_lng_time, String creation_ts){

        this._exercise_id=exercise_id;
        this._activity_id = activity_id;
        this._activity_ts = activity_ts;
        this._lat_lng = lat_lng;
        this._lat_lng_accuracy = lat_lng_accuracy;
        this._lat_lng_time = lat_lng_time;
        this._creation_ts = creation_ts;
    }

    // constructor
    public ExerciseLog(String activity_ts){
        this._activity_ts = activity_ts;

    }

    public ExerciseLog(String activity_ts, String lat_lng){
        this._activity_ts = activity_ts;
        this._lat_lng = lat_lng;
    }
    public ExerciseLog(int activity_id,String activity_ts, String lat_lng,String creation_ts){
        this._activity_id= activity_id;
        this._activity_ts = activity_ts;
        this._lat_lng = lat_lng;
        this._creation_ts = creation_ts;
    }

    public ExerciseLog(String activity_ts, String lat_lng,String creation_ts){
        this._activity_ts = activity_ts;
        this._lat_lng = lat_lng;
        this._creation_ts = creation_ts;
    }
    // getting ID
    public int getActivity_id(){
        return this._activity_id;
    }

    // setting id
    public void setActivity_id(int activity_id){
        this._activity_id = activity_id;
    }

    // getting ID
    public int getExercise_id(){
        return this._exercise_id;
    }

    // setting id
    public void setExercise_id(int exercise_id){
        this._exercise_id = exercise_id;
    }

    public int getID(){
        return this._id;
    }

    // setting id
    public void setID(int id){
        this._id = id;
    }
    // getting activity_ts
    public String getActivity_ts(){
        return this._activity_ts;
    }

    // setting activity_ts
    public void setActivity_ts(String activity_ts){
        this._activity_ts = activity_ts;
    }

    // getting phone number
    public String getLatLng(){
        return this._lat_lng;
    }

    // setting phone number
    public void setLatLng(String lat_lng){
        this._lat_lng = lat_lng;
    }

    // getting phone number
    public String getCreation_Ts(){
        return this._creation_ts;
    }

    // setting phone number
    public void setCreation_Ts(String creation_ts){
        this._creation_ts = creation_ts;
    }

    public float getLatLngAccuracy() {
        return _lat_lng_accuracy;
    }

    public void setLatLngAccuracy(float _lat_lng_accuracy) {
        this._lat_lng_accuracy = _lat_lng_accuracy;
    }

    public long getLatLngTime() {
        return _lat_lng_time;
    }

    public void setLatLngTime(long _lat_lng_time) {
        this._lat_lng_time = _lat_lng_time;
    }


}
