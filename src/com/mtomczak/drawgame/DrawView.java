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

import com.larvalabs.svgandroid.SVGParser;
import com.larvalabs.svgandroid.SVG;

import java.lang.StringBuilder;
import java.util.Date;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.Random;
import java.util.Vector;

public class DrawView extends View
  implements View.OnTouchListener {

  private Canvas painting_canvas_;
  private Bitmap painting_bitmap_;
  private Path active_path_;
  private RandomSound squeakSounds_ = null;
  private RandomSound shakeSounds_ = null;
  private OscillationSensor oscillatorX_ = null;
  private OscillationSensor oscillatorY_ = null;
  private FaceDownSensor facedown_ = null;
  private Random randomSource_ = null;

  private long lastShakeTimestamp_ = 0;

  private static final int CRAYON_IMAGE_TOP_OFFSET = 100;
  private static final int CRAYON_UNSELECTED_INDENT = 15;
  private static final int CRAYON_COLORS[] = {
    Color.rgb(255, 0, 0),
    Color.rgb(0, 255, 0),
    Color.rgb(0, 0, 255)
  };
  private int selected_crayon_ = 0;

  private Picture crayon_images_[];
  private float crayon_gutter_ = 0;
  private float crayon_height_ = 0;

  public DrawView(Context context, AttributeSet attrs) {
    super(context, attrs);
    crayon_images_ = new Picture[CRAYON_COLORS.length];
    for (int i = 0; i < CRAYON_COLORS.length; i++) {
      crayon_images_[i] = SVGParser.getSVGFromResource(
	getResources(),
	R.raw.crayon,
	Color.rgb(0,0,0),
	CRAYON_COLORS[i]).getPicture();
    }
    crayon_gutter_ = crayon_images_[0].getWidth();
    crayon_height_ = crayon_images_[0].getHeight();
  }

  public void setSqueakSounds(RandomSound sound_source) {
    squeakSounds_ = sound_source;
  }

  public void setShakeSounds(RandomSound sound_source) {
    shakeSounds_ = sound_source;
  }

  public void setShakeSensors(
    OscillationSensor oscillatorX,
    OscillationSensor oscillatorY) {
    oscillatorX_ = oscillatorX;
    oscillatorY_ = oscillatorY;
  }

  public void setFaceDownSensor(FaceDownSensor facedown) {
    facedown_ = facedown;
  }

  public void setRandomSource(Random random) {
    randomSource_ = random;
  }

  @Override
    protected void onDraw (Canvas canvas) {
    super.onDraw(canvas);

    canvas.drawBitmap(painting_bitmap_, new Matrix(), null);
    Paint gutterPaint = new Paint();
    gutterPaint.setColor(Color.BLACK);
    gutterPaint.setStyle(Paint.Style.STROKE);
    gutterPaint.setStrokeWidth(2.0f);
    canvas.drawLine(
      crayon_gutter_, 0,
      crayon_gutter_, canvas.getHeight(),
      gutterPaint);

    // Check status of shake.
    if (oscillatorX_ != null) {
      Date d = new Date();

      if (facedown_.isFacedown() &&
	  (d.getTime() - oscillatorX_.getLastOscillationTimestamp() <= 500 ||
	   d.getTime() - oscillatorY_.getLastOscillationTimestamp() <= 500)) {
	shakeSounds_.play();
	eraseOneBlot();
      } else {
	shakeSounds_.pause();
      }
    }
    drawCrayons(canvas);
  }

  /** @brief Erases one blot of the image
   */
  void eraseOneBlot() {
    int x = randomSource_.nextInt(painting_canvas_.getWidth());
    int y = randomSource_.nextInt(painting_canvas_.getHeight());
    int blotWidth = randomSource_.nextInt(painting_canvas_.getWidth() / 2) +
      (painting_canvas_.getWidth() / 4);

      Paint erasePaint = new Paint();
      erasePaint.setColor(Color.WHITE);
      erasePaint.setStyle(Paint.Style.STROKE);
      erasePaint.setStrokeWidth((float)blotWidth);
      painting_canvas_.drawPoint((float)x, (float)y, erasePaint);
  }

  /** @brief Draws crayons
   */
  private void drawCrayons(Canvas canvas) {
    canvas.translate(-CRAYON_UNSELECTED_INDENT, CRAYON_IMAGE_TOP_OFFSET);
    for (int i = 0; i < crayon_images_.length; i++) {
      int translate = (i == selected_crayon_) ? CRAYON_UNSELECTED_INDENT : 0;
      canvas.translate(translate, 0);
      crayon_images_[i].draw(canvas);
      canvas.translate(-translate, crayon_images_[i].getHeight());
    }
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
      if (squeakSounds_ != null) {
	squeakSounds_.pause();
      }
      break;
    case MotionEvent.ACTION_DOWN:
      if (event.getX() <= crayon_gutter_) {
	selectNewCrayon(event.getY());
      } else {
	active_path_ = new Path();
	active_path_.moveTo(Math.max(event.getX(), crayon_gutter_), event.getY());
	if (squeakSounds_ != null) {
	  squeakSounds_.play();
	}
      }
      break;
    case MotionEvent.ACTION_MOVE:
      if (active_path_ != null) {
	active_path_.lineTo(Math.max(event.getX(), crayon_gutter_), event.getY());
	Paint path_paint = new Paint();
	path_paint.setColor(CRAYON_COLORS[selected_crayon_]);
	path_paint.setStyle(Paint.Style.STROKE);
	path_paint.setStrokeWidth(8.0f);
	painting_canvas_.drawPath(active_path_, path_paint);
	invalidate();
      }
    }

    return true;
  }

  /** @brief Selects a new crayon
   * @param yCoordinate y-coordinate of the selection event.
   */

  void selectNewCrayon(float yCoordinate) {
    int selected_crayon = (int)((yCoordinate - CRAYON_IMAGE_TOP_OFFSET)
				/ crayon_height_);
    if (selected_crayon >= 0 && selected_crayon < CRAYON_COLORS.length) {
      selected_crayon_ = selected_crayon;
      invalidate();
    }
  }
}
