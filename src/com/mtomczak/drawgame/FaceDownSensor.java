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
 * Monitors for the device being oriented face-down.
 */
public class FaceDownSensor implements SensorEventListener {
  private final SensorManager sensorManager_;
  private final Sensor gravity_;
  private boolean facedown_ = false;

  /** @brief Constructor.
   *
   * @param manager Sensor manager that controls the sensor of interest.
   * @param oscillationThreshold Threshold value, in m/s^2, that can trigger an
   * oscillation event.
   * @param axisOfInterest Axis to monitor with this oscillation sensor.
   */

  public FaceDownSensor(SensorManager manager) {
    sensorManager_ = manager;
    gravity_ = sensorManager_.getDefaultSensor(Sensor.TYPE_GRAVITY);
    if (gravity_ == null) {
      throw new Error("Game requires gravity sensor, but no gravity sensor is " +
		      "present on this hardware.");
    }
  }

  public void onPause() {
    sensorManager_.unregisterListener(this);
  }

  public void onResume() {
    sensorManager_.registerListener(
      this, gravity_,
      SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
    public void onSensorChanged(SensorEvent event) {
    facedown_ = event.values[2] < 0.0f;
  }

  @Override
    public void onAccuracyChanged(Sensor sensor, int accuary) {
    /* ignored */
  }


  /** Get facedown status
   *
   * @return True if we are facedown.
   */
  boolean isFacedown() {
    return facedown_;
  }
}