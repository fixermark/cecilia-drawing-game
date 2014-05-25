/*
Copyright 2014 Mark T. Tomczak

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.mtomczak.drawgame;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import java.util.Date;

/**
 * Given an accelerometer, monitors the sensor for oscillatory behavior.
 *
 * The oscillator reports the last time it detected an oscillation (defined as
 * a transition from one direction to another where a given oscillation was over
 * a threshold value). The threshold value is defined at construction time.
 *
 *	   A---+   A---+
 *         |   |   |   |
 * ----+   |   |   |   |   A---
 *     |   |   |   |   |   |
 *     A---+   A---+   A---+
 *
 * ... Each point marked 'A' in the above graph indicates an oscillation event
 * report.
 */
public class OscillationSensor implements SensorEventListener {
  private float oscillationThreshold_ = 0.0f;
  private final SensorManager sensorManager_;
  private final Sensor accelerometer_;
  private final int axisOfInterest_;

  /** Either 1, -1, or 0. */
  private int lastOscillationDirection_ = 0;

  /** Last timestamp of an oscillation event in milliseconds. */
  private long lastTimestampMillis_ = 0;

  /** @brief Constructor.
   *
   * @param manager Sensor manager that controls the sensor of interest.
   * @param oscillationThreshold Threshold value, in m/s^2, that can trigger an
   * oscillation event.
   * @param axisOfInterest Axis to monitor with this oscillation sensor.
   */

  public OscillationSensor(
    SensorManager manager,
    float oscillationThreshold,
    int axisOfInterest) {
    sensorManager_ = manager;
    oscillationThreshold_ = oscillationThreshold;
    axisOfInterest_ = axisOfInterest;

    accelerometer_ = sensorManager_.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    if (accelerometer_ == null) {
      throw new Error("Game requires accelerometer, but no accelerometer is " +
		      "present on this hardware.");
    }
  }

  public void onPause() {
    sensorManager_.unregisterListener(this);
  }

  public void onResume() {
    sensorManager_.registerListener(this, accelerometer_,
				     SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
    public void onSensorChanged(SensorEvent event) {

    float value = event.values[axisOfInterest_];

    if (Math.abs(value) > oscillationThreshold_ && (
	  (value < 0.0f && lastOscillationDirection_ >= 0) ||
	  (value > 0.0f && lastOscillationDirection_ <= 0))) {
      lastTimestampMillis_ = (new Date()).getTime();
      if (value > 0.0f) {
	lastOscillationDirection_ = 1;
      } else {
	lastOscillationDirection_ = -1;
      }
    }
  }

  @Override
    public void onAccuracyChanged(Sensor sensor, int accuary) {
    /* ignored */
  }

  /** Get the last oscillation timestamp
   *
   * @return The last oscillation timestamp (in milliseconds), or 0 if none have
   *     been detected.
   */
  long getLastOscillationTimestamp() {
    return lastTimestampMillis_;
  }
}