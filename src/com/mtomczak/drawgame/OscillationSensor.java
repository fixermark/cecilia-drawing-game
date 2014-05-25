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

/**
 * Given an accelerometer, monitors the sensor for oscillatory behavior.
 *
 * The oscillator reports the last time it detected an oscillation (defined as)
 * a transition from one direction to another where a given oscillation was over
 * a threshold value (defined at construction time).
 *
 *	   +---+   +---+
 *         |   |   |   |
 * ----+   |   |   |   |   +---
 *     |   |   |   |   |   |
 *     +---+   +---+   +---+
 */

//   public class OscillationSensor implements SensorEventListener {
//   private float oscillation_threshold_ = 0.0f;

//   public OscillationSensor(float oscillation_threshold) {
//     oscillation_threshold_ = oscillation_threshold;
//     // TODO(mtomczak): Connect to accelerometer.
//   }


// }