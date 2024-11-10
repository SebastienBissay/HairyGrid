import processing.core.PApplet;
import processing.core.PVector;

import static processing.core.PConstants.TWO_PI;

public class Node {
    public PVector pos;
    public PVector speed;
    PApplet pApplet;

    Node(float x, float y, PApplet pApplet) {
        this.pApplet = pApplet;
        pos = new PVector(x, y);
        speed = PVector.fromAngle(pApplet.random(0, TWO_PI)).mult(pApplet.random(0, 5));
    }
}
