package kokkarinen.ilkka;

import java.util.*;

public class SuperEllipseLeaf extends Node {
    private static double FALLOFF = 6;
    public int getArity() { return 0; }
    protected double cx, cy, f, exp, r;
    protected boolean edge;
    protected int sign;
    public SuperEllipseLeaf() { }
    public SuperEllipseLeaf(Random rng) {
        exp = rng.nextDouble() * 3 + 1.5;
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
        f = rng.nextDouble() * FALLOFF + 1;
        r = rng.nextDouble() * 0.4 + 0.1;
        edge = rng.nextBoolean();
        sign = rng.nextBoolean()? +1: -1;
    }
    public double evaluate(double x, double y) {
        double d;
        if(distanceSq(x, y, cx, cy) < 0.000001) {
            return edge? -sign: sign;
        }
        if(edge) {
            d = Math.abs(distance(x, y, cx, cy, exp) - r);
        }
        else {
            d = distance(x, y, cx, cy, exp);
        }
        return sign * boundsCheck(2 * Math.exp(-d * f) - 1);
    }
}