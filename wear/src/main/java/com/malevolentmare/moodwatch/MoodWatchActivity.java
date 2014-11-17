package com.malevolentmare.moodwatch;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class MoodWatchActivity extends Activity implements SensorEventListener {

    //The sensor manager
    private SensorManager sensorManager;
    private BackgroundAnimationView backgroundView;

    //Dismiss Overlay piece so that it can close with long press
    //instead of swipe
    private DismissOverlayView dismissOverlay;
    private GestureDetector detector;


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



        // Obtain the DismissOverlayView element
        dismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        dismissOverlay.setIntroText(R.string.long_press_intro);
        dismissOverlay.showIntroIfNecessary();

        // Configure a gesture detector
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            public void onLongPress(MotionEvent ev) {
                dismissOverlay.show();
            }
        });




    }

    // Capture long presses
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return detector.onTouchEvent(ev) || super.onTouchEvent(ev);
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
