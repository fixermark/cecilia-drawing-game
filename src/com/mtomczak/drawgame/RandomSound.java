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

/**
 * Generates random sound from a pool of resources until told to stop.
 */

package com.mtomczak.drawgame;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Random;

public class RandomSound implements MediaPlayer.OnCompletionListener {
  private MediaPlayer[] sounds_ = null;
  private int[] sound_resources_ = null;
  private boolean playing_ = false;
  private Random rng_ = null;

  public RandomSound(Context context, Random rng, int[] sound_resources) {
    rng_ = rng;
    sounds_ = new MediaPlayer[sound_resources.length];
    for (int i = 0; i < sound_resources.length; i++) {
      sounds_[i] = MediaPlayer.create(context, sound_resources[i]);
      sounds_[i].setOnCompletionListener(this);
    }
  }

  public void play() {
    if(!playing_) {
      playing_ = true;
      chooseNextSound();
    }
  }

  public void pause() {
    playing_ = false;
  }

  @Override
    public void onCompletion(MediaPlayer mp) {
    if (playing_) {
      chooseNextSound();
    }
  }

  private void chooseNextSound() {
    sounds_[rng_.nextInt(sounds_.length)].start();
  }

}