package kokkarinen.ilkka;

import java.util.*;

public class SuperFormula extends Node {
    private static final int FALLOFF = 10;
    public int getArity() { return 0; }
    protected double m, n1, n2, n3;
    protected double cx, cy, a, b, f;
    protected int type;
    protected int sign;
    public SuperFormula() { }
    public SuperFormula(Random rng) {
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
        a = rng.nextDouble() * 0.6 + 0.1;
        b = rng.nextDouble() * 0.6 + 0.1;
        m = rng.nextDouble() * 5 + 0.6;
        n1 = rng.nextDouble() * 10 + 2;
        n2 = n1 + rng.nextDouble() -0.5;
        n3 = n1 + rng.nextDouble() -0.5;
        f = rng.nextDouble() * FALLOFF + 2;
        type = rng.nextInt(3);
        sign = rng.nextBoolean() ? +1: -1;
    } 
    public double evaluate(double x, double y) {
        if(Math.abs(x - cx) < 0.003 && Math.abs(y - cy) < 0.003) {
            return -sign;
        }
        double d, phi;
        d = distance(x, y, cx, cy);
        phi = (2 * Math.acos((x - cx) / d) - Math.PI) / Math.PI;
        if(type == 1) { d = manhattanD(x, y, cx, cy); }
        if(type == 2) { d = minimumD(x, y, cx, cy); }
        double t1 = Math.pow(Math.abs(Math.cos(m * phi) / a), n2);
        double t2 = Math.pow(Math.abs(Math.sin(m * phi) / b), n3);
        double r = Math.pow(t1 + t2, -1.0 / n1);
        d = Math.abs(d - r);
        return sign * boundsCheck(2 * Math.exp(-d * f) - 1);
    }
}