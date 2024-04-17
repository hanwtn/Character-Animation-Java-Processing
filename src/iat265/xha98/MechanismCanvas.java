package iat265.xha98;

import controlP5.*;
import processing.core.*;

import java.util.ArrayList;

public class MechanismCanvas {
    Mechanism mechanism;
    ControlP5 cp5;
    Wheel selectedWheel;
    PApplet p; // The PApplet instance to be used for drawing
    Scrubbable rootWheel;

    // Constructor to accept a PApplet instance
    public MechanismCanvas(PApplet p) {
        this.p = p;
        setup();

    }

    public PApplet getPApplet() {
        return p;
    }

    public void setup() {
        cp5 = new ControlP5(p);

        mechanism = new Mechanism(this);
        selectedWheel = null;

        cp5.addButton("clear")
                .setPosition(p.width - 90, 10)
                .setSize(80, 30)
                .setCaptionLabel("Clear");

        cp5.addButton("saveImage")
                .setPosition(p.width - 200, 10)
                .setSize(100, 30)
                .setCaptionLabel("Save Image");

        rootWheel = mechanism.root;
    }

    public void draw() {
        p.background(255);
        mechanism.display();

        // Display instructions for user interaction on screen
        p.fill(0);
        p.textAlign(PApplet.LEFT, PApplet.TOP);
        p.textSize(12);
        p.text("Click on a wheel to select it.", 10, p.height - 105);
        p.text("Press '+' to increase the selected wheel's radius.", 10, p.height - 90);
        p.text("Press '-' to decrease the selected wheel's radius.", 10, p.height - 75);
        p.text("Press '1' to change the selected wheel's color to red.", 10, p.height - 60);
        p.text("Press '2' to change the selected wheel's color to green.", 10, p.height - 45);
        p.text("Press '3' to change the selected wheel's color to blue.", 10, p.height - 30);
    }

    public void keyPressed() {
        if (selectedWheel != null) {
            float currentRadius = selectedWheel.getParameter("radius");
            if (p.key == '+') {
                selectedWheel.setParameter("radius", currentRadius + 5);
            } else if (p.key == '-') {
                selectedWheel.setParameter("radius", Math.max(5, currentRadius - 5));
            } else if (p.key == '1') {
                selectedWheel.setParameter("color", p.color(255, 0, 0));
            } else if (p.key == '2') {
                selectedWheel.setParameter("color", p.color(0, 255, 0));
            } else if (p.key == '3') {
                selectedWheel.setParameter("color", p.color(0, 0, 255));
            }
        }
    }

    public void mousePressed() {

    }

    public void clear() {
        mechanism.clear();
    }

    public void saveImage() {
        p.save("mechanism.png");
    }
}
