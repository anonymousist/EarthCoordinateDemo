package com.example.administrator.earthcoordinatedemo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements SensorEventListener{
    private SensorManager mySensor;
    private Sensor magneticSensor,accSensor,gravitySensor;
    private float[] gravityValues = null;
    private float[] magneticValues = null;
    private TextView text1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text1 = (TextView)findViewById(R.id.text1);
        mySensor=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mySensor.registerListener(this,mySensor.getDefaultSensor(Sensor.TYPE_GRAVITY),10000);
        mySensor.registerListener(this,mySensor.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),10000);
        mySensor.registerListener(this,mySensor.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),10000);
    }

    @Override
    protected void onStop(){
        mySensor.unregisterListener(this);
        super.onStop();
    }
    @Override protected void onPause(){
        mySensor.unregisterListener(this);
        super.onPause();
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if ((gravityValues != null) && (magneticValues != null)
                && (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {

            float[] deviceRelativeAcceleration = new float[4];
            deviceRelativeAcceleration[0] = event.values[0];
            deviceRelativeAcceleration[1] = event.values[1];
            deviceRelativeAcceleration[2] = event.values[2];
            deviceRelativeAcceleration[3] = 0;

            // Change the device relative acceleration values to earth relative values
            // X axis -> East
            // Y axis -> North Pole
            // Z axis -> Sky

            float[] R = new float[16], I = new float[16], earthAcc = new float[16];

            SensorManager.getRotationMatrix(R, I, gravityValues, magneticValues);

            float[] inv = new float[16];

            android.opengl.Matrix.invertM(inv, 0, R, 0);
            android.opengl.Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);
            Log.d("Acceleration", "Values: (" + earthAcc[0] + ", " + earthAcc[1] + ", " + earthAcc[2] + ")");
            text1.setText(earthAcc[0]+"\n"+earthAcc[1]+"\n"+earthAcc[2]);

        } else if ( event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            gravityValues = event.values;
        } else if ( event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magneticValues = event.values;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
