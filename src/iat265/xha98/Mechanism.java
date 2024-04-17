package iat265.xha98;

import java.util.ArrayList;
import processing.core.*;

public class Mechanism extends PApplet {

    ArrayList<Scrubbable> wheels;
    ArrayList<Belt> belts;
    MechanismCanvas parent;


    Wheel root;



    Mechanism(MechanismCanvas parent) {
        this.parent = parent;
        wheels = new ArrayList<Scrubbable>();
        belts = new ArrayList<Belt>();
        createMechanism();
    }

    void createMechanism() {
        float rootRadius = 100;
        float rootSpeed = PConstants.TWO_PI / 300.0f;
        // Center the root wheel based on the dimensions of the PApplet
        float centerX = parent.p.width / 2;
        float centerY = parent.p.height / 2;

        root = createWheel(parent.p);
        wheels.add(root);

    }

    Wheel createWheel(PApplet parent) {
        // Define parameters for the root wheel
        float rootX = parent.width / 2;
        float rootY = 100;
        float rootRadius = 50;
        int rootColor = parent.color(255, 0, 0);
        float rootRotationSpeed = 0.05f;

        // Create the root wheel
        Wheel rootWheel = new Wheel(parent, rootX, rootY, rootRadius, rootColor, rootRotationSpeed, null, "Root");
        rootWheel.addChildWheel(rootWheel); // Add root wheel to itself
        wheels.add(rootWheel); // Add root wheel to the list of wheels

        // Define parameters for child wheels
        int numChildWheels = 5;
        float childRadius = 20;
        float childXOffset = 100;
        float childYOffset = 150;
        float scaleFactor = 0.75f;

        // Create and add child wheels
        for (int i = 0; i < numChildWheels; i++) {
            float childX = rootX + (i - (numChildWheels - 1) / 2.0f) * childXOffset;
            float childY = rootY + childYOffset;
            int colorDepth = (rootColor + i * 30) % 255;
            float childRotationSpeed = rootRotationSpeed * (100 / rootRadius);

            // Create child wheel
            Wheel childWheel = new Wheel(parent, childX, childY, childRadius * scaleFactor, colorDepth, childRotationSpeed, rootWheel, "Child_" + i);

            // Add child wheel to its parent
            rootWheel.addChildWheel(childWheel);

            // Add child wheel to the list of wheels
            wheels.add(childWheel);

            // Create belt between root wheel and child wheel
            Belt belt = new Belt(parent, rootWheel, childWheel);
            belts.add(belt); // Add belt to the list of belts
            rootWheel.addBelt(belt); // Add belt to the root wheel
            childWheel.setParentWheel(rootWheel); // Set parent wheel for the child wheel
        }

        return rootWheel;
    }


    void display() {
        for (Scrubbable belt : belts) {
            belt.draw();
        }
        for (Scrubbable wheel : wheels) {
            wheel.draw();
        }
    }

    void clearMechanism() {
        wheels.clear();
        belts.clear();
    }

    public Wheel[] getAllWheels() {
        if (wheels != null) {
            System.out.println("Number of wheels: " + wheels.size());
            for (Scrubbable wheel : wheels) {
                System.out.println("Wheel: " + wheel.getName());
            }
            return wheels.toArray(new Wheel[0]);
        } else {
            System.out.println("No wheels found.");
            return new Wheel[0]; // Return an empty array if the list is null
        }
    }
}
