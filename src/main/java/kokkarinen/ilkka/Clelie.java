package kokkarinen.ilkka;

import java.util.*;

public class Clelie extends Node {
    private static final double CLELIESCALE = 5;
    protected double m;
    public int getArity() { return 2; }
    public Clelie() { }
    public Clelie(Random rng) {
        m = rng.nextDouble() * CLELIESCALE + 1.0;
    }
    public double evaluate(double x, double y) {
        double theta = children[0].evaluate(x, y);
        double newX = Math.sin(m * theta) * Math.cos(theta);
        double newY = Math.sin(m * theta) * Math.sin(theta);
        return boundsCheck(children[1].evaluate(newX, newY));
    }
}