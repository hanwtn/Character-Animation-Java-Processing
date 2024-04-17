package iat265.xha98;

import controlP5.ControlP5;
import processing.core.PApplet;

public class MechanismFactoryClass implements MechanismFactory {
    private ControlP5 cp5;
    private Mechanism mechanism;
    private Scrubbable rootWheel;

    @Override
    public Scrubbable getMechanism(PApplet p) {
        cp5 = new ControlP5(p);

        // Assuming MechanismCanvas has been adjusted to not extend PApplet and can accept PApplet in its constructor
        MechanismCanvas editor = new MechanismCanvas(p);


        return editor.rootWheel;
    }
}
