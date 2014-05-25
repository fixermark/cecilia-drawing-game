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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.StringBuilder;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Vector;

public class DrawView extends View
  implements View.OnTouchListener {

  private Canvas painting_canvas_;
  private Bitmap painting_bitmap_;
  private Path active_path_;
  private RandomSound squeak_sounds_ = null;

  public DrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void setSqueakSounds(RandomSound sound_source) {
    squeak_sounds_ = sound_source;
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawBitmap(painting_bitmap_, new Matrix(), null);
  }

  @Override
    protected void onMeasure(int width, int height) {
    int w = MeasureSpec.getSize(width);
    int h = MeasureSpec.getSize(height);

    painting_bitmap_ = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
    painting_canvas_ = new Canvas(painting_bitmap_);

    super.onMeasure(width, height);
  }

  @Override
  public boolean onTouch(View v, MotionEvent event) {
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    // We handle multiple touch pointers here, because it turns out
    // that it's asking a lot of tiny hands to keep their other
    // fingers, palm, etc. off the screen and only touch with one
    // fingertip at a time. :)
    // TODO(mtomczak): Definitely going to want this back, so will
    // need to learn multi-touch API to track multiple pointers.
    //for (int i=0; i < event.getPointerCount(); i++) {
    //}
    switch(event.getAction()) {
    case MotionEvent.ACTION_UP:
      active_path_ = null;
      if (squeak_sounds_ != null) {
	squeak_sounds_.pause();
      }
      break;
    case MotionEvent.ACTION_DOWN:
      active_path_ = new Path();
      active_path_.moveTo(event.getX(), event.getY());
      if (squeak_sounds_ != null) {
	squeak_sounds_.play();
      }
      break;
    case MotionEvent.ACTION_MOVE:
      active_path_.lineTo(event.getX(), event.getY());
      Paint path_paint = new Paint();
      path_paint.setColor(Color.BLUE);
      path_paint.setStyle(Paint.Style.STROKE);
      path_paint.setStrokeWidth(8.0f);
      painting_canvas_.drawPath(active_path_, path_paint);
      invalidate();
    }

    return true;
  }
}
