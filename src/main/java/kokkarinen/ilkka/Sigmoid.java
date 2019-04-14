package kokkarinen.ilkka;

import java.util.*;

public class Sigmoid extends Node {
    private static final double SIGSCALE = 7;
    public int getArity() { return 1; }
    protected double scale;
    public Sigmoid() { }
    public Sigmoid(Random rng) {
        scale = rng.nextDouble() * SIGSCALE + 3;
    }
    public double evaluate(double x, double y) {
        double v = children[0].evaluate(x, y);
        return boundsCheck(2 * (1.0 / (1.0 + Math.exp(-scale * v))) - 1.0);
    }
}