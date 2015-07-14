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

import android.view.MotionEvent;
import java.util.HashMap;

/**
 * Tracks multiple touch events.
 *
 * Override the abstract methods to handle the touch event.
 */
public abstract class MultiTouchTracker {
  private HashMap<Integer, MotionEvent.PointerCoords> touches_;

  public MultiTouchTracker() {
    touches_ = new HashMap<Integer, MotionEvent.PointerCoords>();
  }

  /**
   * Handle an incoming touch event.
   * @param event The event that happens.
   * @return True if we handled the event.
   */
  public boolean onTouchEvent(MotionEvent event) {
    int idx = event.getActionIndex();
    int id = -1;
    MotionEvent.PointerCoords coords;

    switch(event.getActionMasked()) {
    case MotionEvent.ACTION_DOWN:
    case MotionEvent.ACTION_POINTER_DOWN:
      coords = new MotionEvent.PointerCoords();
      id = event.getPointerId(idx);
      event.getPointerCoords(idx, coords);
      if (touches_.isEmpty()) {
	onInteractionStart();
      }
      onTouchStart(id, coords);
      doDrag(id, coords);
      return true;
    case MotionEvent.ACTION_UP:
    case MotionEvent.ACTION_POINTER_UP:
      coords = new MotionEvent.PointerCoords();
      id = event.getPointerId(idx);
      event.getPointerCoords(idx, coords);
      doDrag(id, coords);
      onTouchStop(id);
      touches_.remove(id);
      if (touches_.isEmpty()) {
	onInteractionStop();
      }
      return true;
    case MotionEvent.ACTION_MOVE:
      for (int i = 0; i < event.getPointerCount(); i++) {
	coords = new MotionEvent.PointerCoords();
	id = event.getPointerId(i);
	event.getPointerCoords(i, coords);
	doDrag(id, coords);
      }
      return true;
    case MotionEvent.ACTION_CANCEL:
      if (!touches_.isEmpty()) {
	// TODO(mtomczak): Should probably onTouchStop all the outstanding
	// events here.
	onInteractionStop();
	touches_.clear();
      }
      return true;
    default:
      return false;
    }
  }

  /**
   * Handle a drag operation.
   * @param id Unique ID of the pointer being dragged.
   * @param currentEvent PointerCoords event we are handling.
   */
  private void doDrag(int id, MotionEvent.PointerCoords currentEvent) {
    if (touches_.containsKey(id)) {
      MotionEvent.PointerCoords prevEvent = touches_.get(id);
      onDrag(id, currentEvent, prevEvent);
      prevEvent.copyFrom(currentEvent);
    } else {
      touches_.put(id, currentEvent);
    }
  }

  /**
   * Called when the first touch event starts.
   */
  public abstract void onInteractionStart();

  /**
   * Called when the last touch event stops.
   */
  public abstract void onInteractionStop();

  /**
   * Called when a new touch event starts.
   * @param id Unique id of the new touch event.
   * @param event The first touch point.
   */
  public abstract void onTouchStart(int id, MotionEvent.PointerCoords event);

  /**
   * Called when a touch event stops.
   * @param id Unique id of the new touch event.
   */
  public abstract void onTouchStop(int id);

  /**
   * Called when a drag event occurs.
   * @param pointerId Unique ID of the pointer.
   * @param currentEvent The pointer coords of the current event.
   * @param prevEvent The pointer coords of the previous event.
   */
  public abstract void onDrag(
    int pointerId,
    MotionEvent.PointerCoords currentEvent,
    MotionEvent.PointerCoords prevEvent);
}
