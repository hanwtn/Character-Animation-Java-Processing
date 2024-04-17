package tweeneditor;

import iat265.xha98.Scrubbable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import processing.core.PApplet;

/**
 *
 * @author aga53
 */
public class ScrubberChannel extends PApplet {

  private final Scrubbable target;
  private final String property;
  private float x, y, scrubberW, scrubberH, duration;
  private final List<KeyFrame> kFrames;

  public float getDuration() {
    return duration;
  }

  public void setDuration(float duration) {
    this.duration = duration;
  }

  public ScrubberChannel(Scrubbable target, String property, float x, float y, float w, float h, long duration) {
    this.target = target;
    this.property = property;
    this.x = x;
    this.y = y;
    this.scrubberW = w;
    this.scrubberH = h;
    this.duration = duration;
    this.kFrames = new ArrayList<>();
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getW() {
    return scrubberW;
  }

  public void setW(float w) {
    this.scrubberW = w;
  }

  public float getH() {
    return scrubberH;
  }

  public void setH(float h) {
    this.scrubberH = h;
  }

  public List<KeyFrame> getkFrames() {
    return kFrames;
  }

  public void addkFrame(KeyFrame f) {
    kFrames.add(f);
    kFrames.sort(new Comparator<KeyFrame>() {
      @Override
      public int compare(KeyFrame o1, KeyFrame o2) {
        return (int) (o1.getT() - o2.getT());
      }
    });
  }

  public void removekFrame(KeyFrame f) {
    kFrames.remove(f);
  }

  @Override
  public void draw() {
    pushStyle();
    noStroke();
    fill(128);
    text(target.getName() + ":" + property, 10, y + 10);
    rect(x, y, scrubberW, scrubberH);

    /*
         Draw key frames
     */
    for (int i = 0; i < kFrames.size(); i++) {
      KeyFrame f = kFrames.get(i);
      float anchorX = x + (scrubberW / duration) * f.getT();
      fill(0, 0, 255);
      rect(anchorX, y, ANCHOR_WIDTH, scrubberH);
    }

    popStyle();
  }

  public void update(long currentT) {
    for (int i = 0; i < kFrames.size() - 1; i++) {
      KeyFrame keyFrame = kFrames.get(i);
      KeyFrame nextKeyFrame = kFrames.get(i + 1);
      if (currentT >= keyFrame.getT() && currentT <= nextKeyFrame.getT()) {
        float delta = (nextKeyFrame.getValue() - keyFrame.getValue())
                / (nextKeyFrame.getT() - keyFrame.getT());
        float newValue = keyFrame.getValue() + delta * (currentT - keyFrame.getT());
        target.setParameter(property, newValue);
      }
    }
  }

  /*
     When the animation stops, this method is called to reset the state of 
     properties to the first keyFrame if present.
   */
  public void reset() {
    if (kFrames.size() > 0) {
      target.setParameter(property, kFrames.get(0).getValue());
    }
  }

  private static final int ANCHOR_WIDTH = 5;

  public boolean pick(Scrubbable selected, int mouseX, int mouseY) {
    if (mouseX > x && mouseX < x + scrubberW && mouseY > y && mouseY < y + scrubberH) {
      /*
             If the user clicks on existing key frame, remove it and return this
       */
      for (int i = 0; i < kFrames.size(); i++) {
        KeyFrame f = kFrames.get(i);
        float anchorX = x + (scrubberW / duration) * f.getT();
        //if clicking on existing keyframe remove it
        if (mouseX > anchorX && mouseX < anchorX + ANCHOR_WIDTH) {
          removekFrame(f);
          return true;
        }
      }

      /*
             If the user clicks on a new space
       */
      long t = (long) ((mouseX - x) * (duration / scrubberW));
      //add a new key frame if the target is the currently selected scrubbable
      final KeyFrame f = new KeyFrame(t, target.getParameter(this.property));
      if (selected == this.target) {
        addkFrame(f);
      }

      //in any case -- whether or not a key frame is added, return this.
      return true;
    }
    return false;
  }

  public Scrubbable getTarget() {
    return target;
  }

  public String getProperty() {
    return property;
  }

  public void write() {
  }
}
