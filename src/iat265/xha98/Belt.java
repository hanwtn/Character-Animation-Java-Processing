package iat265.xha98;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.Iterator;

public class Belt extends PApplet implements Scrubbable {
    Wheel start, end;
    PApplet parent; // Reference to the MechanismCanvas instance
    PVector[] tangents; // Store the tangent points
    private String name;

    Belt(PApplet parent, Wheel start, Wheel end) {
        this.parent = parent;
        this.start = start;
        this.end = end;
        tangents = new PVector[4];
        calculateTangents();
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
        return new String[]{"startRadius", "endRadius"};
    }

    @Override
    public void setParameter(String parameter, float value) {
        // Implement if needed
    }

    @Override
    public float getParameter(String parameter) {
        // Implement if needed
        return 0;
    }

    @Override
    public void draw() {
        parent.strokeWeight(2);
        parent.stroke(0);
        // Make sure to check if tangents are not null before trying to draw them
        if (tangents[0] != null && tangents[1] != null && tangents[2] != null && tangents[3] != null) {
            parent.line(tangents[0].x, tangents[0].y, tangents[2].x, tangents[2].y);
            parent.line(tangents[1].x, tangents[1].y, tangents[3].x, tangents[3].y);
        }
        calculateTangents();
    }

    @Override
    public Scrubbable pick(int x, int y) {
        // Implement if needed
        return null;
    }

    @Override
    public Iterator<Scrubbable> createIterator() {
        return new Iterator<Scrubbable>() {
            private boolean hasNext = true;

            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Scrubbable next() {
                hasNext = false;
                return Belt.this;
            }
        };
    }

    // Used CHATGPT's Help for this
    void calculateTangents() {
        float startX = start.getParameter("x");
        float startY = start.getParameter("y");
        float endX = end.getParameter("x");
        float endY = end.getParameter("y");
        float startRadius = start.getParameter("radius");
        float endRadius = end.getParameter("radius");

        PVector startPos = new PVector(startX, startY);
        PVector endPos = new PVector(endX, endY);
        PVector d = PVector.sub(endPos, startPos);

        // Distance between centers
        float dist = d.mag();

        // Angles to calculate
        float angleBetweenCenters = PVector.angleBetween(new PVector(1, 0), d);
        float angleOffset = PApplet.asin((endRadius - startRadius) / dist);

        // Outer tangents
        float angleA = angleBetweenCenters + PApplet.PI / 2;
        float angleB = angleBetweenCenters - PApplet.PI / 2;

        // Inner tangents
        float angleC = angleBetweenCenters + PApplet.PI / 2;
        float angleD = angleBetweenCenters - PApplet.PI / 2;

        // Outer tangent points on start wheel
        tangents[0] = new PVector(startRadius * PApplet.cos(angleA), startRadius * PApplet.sin(angleA)).add(startPos);
        tangents[1] = new PVector(startRadius * PApplet.cos(angleB), startRadius * PApplet.sin(angleB)).add(startPos);

        // Inner tangent points on end wheel
        tangents[2] = new PVector(endRadius * PApplet.cos(angleC), endRadius * PApplet.sin(angleC)).add(endPos);
        tangents[3] = new PVector(endRadius * PApplet.cos(angleD), endRadius * PApplet.sin(angleD)).add(endPos);
    }
}
