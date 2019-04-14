package kokkarinen.ilkka;

import java.util.*;

public class PointLight extends Node {
    private static double FALLOFF = 2;
    public int getArity() { return 0; }
    protected double cx, cy, f;
    protected int type;
    public PointLight() { }
    public PointLight(Random rng) {
        type = rng.nextInt(5);
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
        f = rng.nextDouble() * FALLOFF + 0.2;
    }
    public double evaluate(double x, double y) {
        double d;
        if(type == 0) {
            d = manhattanD(x, y, cx, cy);
        }
        else if(type == 1) {
            d = distance(x, y, cx, cy);
        }
        else {
            d = minimumD(x, y, cx, cy);
        }
        return boundsCheck(2 * Math.exp(-d * f) - 1);
    }
}