/*
Copyright 2012 Mark T. Tomczak

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

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.mtomczak.drawgame.DrawView;
import com.mtomczak.drawgame.OscillationSensor;
import com.mtomczak.drawgame.FaceDownSensor;
import com.mtomczak.drawgame.RandomSound;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class Drawgame extends Activity {
  public static final int SQUEAK_SOUNDS[] = {
    R.raw.squeak1,
    R.raw.squeak2,
    R.raw.squeak3,
    R.raw.squeak4,
    R.raw.squeak5,
    R.raw.squeak6
  };

  public static final int SHAKE_SOUNDS[] = {
    R.raw.shaka1,
    R.raw.shaka2,
    R.raw.shaka3,
    R.raw.shaka4,
    R.raw.shaka5,
    R.raw.shaka6,
    R.raw.shaka7,
    R.raw.shaka8,
    R.raw.shaka9
  };

  private OscillationSensor oscillatorX_;
  private OscillationSensor oscillatorY_;
  private FaceDownSensor facedown_;

  /** Called when the activity is first created. */
  @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RandomSound squeaks = new RandomSound(
      getApplicationContext(),
      new Random(),
      SQUEAK_SOUNDS);
    RandomSound shakes = new RandomSound(
      getApplicationContext(),
      new Random(),
      SHAKE_SOUNDS);

    setContentView(R.layout.main);
    final DrawView drawView = (DrawView)findViewById(R.id.drawview);
    oscillatorX_ = new OscillationSensor(
      (SensorManager)getSystemService(SENSOR_SERVICE),
      5.0f,
      0 /* x-axis */);
    oscillatorY_ = new OscillationSensor(
      (SensorManager)getSystemService(SENSOR_SERVICE),
      5.0f,
      1 /* y-axis */);
    facedown_ = new FaceDownSensor(
      (SensorManager)getSystemService(SENSOR_SERVICE));

    drawView.setSqueakSounds(squeaks);
    drawView.setShakeSounds(shakes);
    drawView.setShakeSensors(oscillatorX_, oscillatorY_);
    drawView.setFaceDownSensor(facedown_);
    drawView.setRandomSource(new Random());
    drawView.setOnTouchListener(drawView);

    // We need to touch the render thread at least 20 fps, to detect shake
    // events.
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    executor.scheduleAtFixedRate(
      new Runnable() {
	@Override
	  public void run() {
	  drawView.postInvalidate();
	}
      }, 0, 1000/20, TimeUnit.MILLISECONDS);
  }

  @Override
    protected void onPause() {
    oscillatorX_.onPause();
    oscillatorY_.onPause();
    facedown_.onPause();
    super.onPause();
  }

  @Override
    protected void onResume() {
    oscillatorX_.onResume();
    oscillatorY_.onResume();
    facedown_.onResume();
    super.onResume();
  }
}
