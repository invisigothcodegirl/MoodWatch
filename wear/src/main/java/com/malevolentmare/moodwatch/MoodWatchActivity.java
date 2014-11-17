package com.malevolentmare.moodwatch;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

public class MoodWatchActivity extends Activity implements SensorEventListener {

    //The sensor manager
    private SensorManager sensorManager;
    private BackgroundAnimationView backgroundView;

    /** On Activity Create Method ************************************************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //savedInstanceState will be the bundle that was stored in
        //onSaveInstanceState, unless it has not been restored
        super.onCreate(savedInstanceState);
        //Set the content view in here
        setContentView(R.layout.activity_mood_watch);

        backgroundView = (BackgroundAnimationView)findViewById(R.id.background_view);
        backgroundView.startAnimating();

        //Instantiate the sensorManger
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //Create the heartRateSensor
        Sensor heartRateSensor = sensorManager.getDefaultSensor(65562);

        //Create the heartRateSensor a different way less accuracy
        //Sensor heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

        //Tell this activity to listen for sensor events with normal delay
        sensorManager.registerListener(this,
                heartRateSensor,
                SensorManager.SENSOR_DELAY_NORMAL);



    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //event.values[0] seems to contain BPM
        float currentBPM = event.values[0];
        //Discard BPM when it is zero
        if(currentBPM>0) {
            //Set the BPM in the view
            backgroundView.setCurrentBPM(currentBPM);



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        //Turn off animations
        backgroundView.stopAnimating();
        //Turn off the sensor by unregistering for events
        sensorManager.unregisterListener(this);
    }
}
