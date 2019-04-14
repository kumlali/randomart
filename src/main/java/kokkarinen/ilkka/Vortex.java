package kokkarinen.ilkka;

import java.util.*;

public class Vortex extends Node {
    
    public int getArity() { return 1; }
    protected double cos, sin;
    public Vortex() { }
    public Vortex(Random rng) {
        double angle = rng.nextDouble() * Math.PI / 4;
        cos = Math.cos(angle);
        sin = Math.sin(angle);
    }
    public double evaluate(double x, double y) {
        double d2 = distanceSq(x, y, 0, 0);
        if(d2 < 0.00001) { return children[0].evaluate(x, y); }
        if(d2 > 1) {
            x = x / d2;
            y = y / d2;
        }
        double newX = cos * x - sin * y;
        double newY = sin * x + cos * y;
        return boundsCheck(children[0].evaluate(newX, newY));
    }
}