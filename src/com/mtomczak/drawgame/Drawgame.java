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
import android.os.Bundle;

import com.mtomczak.drawgame.DrawView;
import com.mtomczak.drawgame.RandomSound;

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


  /** Called when the activity is first created. */
  @Override
    public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    RandomSound squeaks = new RandomSound(
					  getApplicationContext(),
					  new Random(),
					  SQUEAK_SOUNDS);
    setContentView(R.layout.main);
    DrawView drawView = (DrawView)findViewById(R.id.drawview);
    drawView.setSqueakSounds(squeaks);
    drawView.setOnTouchListener(drawView);
  }
}
