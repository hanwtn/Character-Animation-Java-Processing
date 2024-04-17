package iat265.xha98;

import processing.core.PApplet;
import java.util.ArrayList;
import java.util.Iterator;

public class Wheel extends PApplet implements Scrubbable {
    float x, y, radius, angle;
    int color;
    float rotationSpeed;
    PApplet parent;
    ArrayList<Belt> connectedBelts;
    Wheel parentWheel;
    ArrayList<Wheel> childWheels;
    private String name;

    Wheel(PApplet parent, float x, float y, float radius, int color, float rotationSpeed, Wheel parentWheel, String name) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.rotationSpeed = rotationSpeed;
        this.parentWheel = parentWheel;
        this.name = name;
        this.connectedBelts = new ArrayList<>();
        this.childWheels = new ArrayList<>();
    }

    public void addBelt(Belt belt) {
        connectedBelts.add(belt);
    }

    public void addChildWheel(Wheel wheel) {
        childWheels.add(wheel);
    }

    public void setParentWheel(Wheel parentWheel) {
        this.parentWheel = parentWheel;
    }


    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String[] getProperties() {
        return new String[]{"radius", "color", "rotationSpeed"};
    }

    @Override
    public void setParameter(String parameter, float value) {
        switch (parameter) {
            case "radius":
                radius = value;
                break;
            case "color":
                color = (int) value;
                break;
            case "rotationSpeed":
                rotationSpeed = value;
                break;
        }
    }

    @Override
    public float getParameter(String parameter) {
        switch (parameter) {
            case "radius":
                return radius;
            case "color":
                return color;
            case "rotationSpeed":
                return rotationSpeed;
            case "x":
                return x;
            case "y":
                return y;
            default:
                return 0;
        }
    }


    @Override
    public void draw() {
        parent.pushMatrix();
        parent.translate(x, y);
        parent.rotate(angle);
        parent.fill(color);
        parent.ellipse(0, 0, 2 * radius, 2 * radius);

        // Draw spokes
        int numSpokes = 8; // Change this value to adjust the number of spokes
        float angleIncrement = TWO_PI / numSpokes;
        for (int i = 0; i < numSpokes; i++) {
            float spokeAngle = i * angleIncrement;
            float spokeX = radius * cos(spokeAngle);
            float spokeY = radius * sin(spokeAngle);
            parent.line(0, 0, spokeX, spokeY);
        }

        parent.popMatrix();
        angle += rotationSpeed;
    }


    @Override
    public Scrubbable pick(int x, int y) {
        System.out.println("Checking wheel: " + name + " at (" + this.x + ", " + this.y + ") with radius " + radius);

        float distance = PApplet.dist(x, y, this.x, this.y);
        if (distance < radius) {
            System.out.println("Picked wheel: " + name);
            return this;  // The click is within the current wheel
        }
        // Recursively check child wheels
        for (Wheel child : childWheels) {
            if (child != null && child != this) {  // Ensure child is not null and not this wheel
                Scrubbable picked = child.pick(x, y);
                if (picked != null) {
                    return picked;  // Return the first non-null picked child
                }
            }
        }
        return null;  // No child and not this wheel were picked
    }



    @Override
    public Iterator<Scrubbable> createIterator() {
        return new Iterator<Scrubbable>() {
            private int currentIndex = 0;
            private ArrayList<Scrubbable> combinedList = new ArrayList<>();

            {
                // Add child wheels to the combined list
                combinedList.addAll(childWheels);
                // Add connected belts to the combined list
                combinedList.addAll(connectedBelts);
            }

            @Override
            public boolean hasNext() {
                return currentIndex < combinedList.size();
            }

            @Override
            public Scrubbable next() {
                return combinedList.get(currentIndex++);
            }
        };
    }



}
