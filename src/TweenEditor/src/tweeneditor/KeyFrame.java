package tweeneditor;

/**
 *
 * @author aga53
 */
public class KeyFrame {

    private final long t;
    private float value;

    public KeyFrame(long t, float value) {
        this.t = t;
        this.value = value;
    }

    public void setValue(float v) {
        value = v;
    }

    public long getT() {
        return t;
    }

    public float getValue() {
        return value;
    }
}
