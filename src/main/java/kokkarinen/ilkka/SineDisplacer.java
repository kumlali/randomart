package kokkarinen.ilkka;

import java.util.*;

public class SineDisplacer extends Node {
    private static final double SINEFREQ = 30;
    private static final double DISPLACEMENT = 0.01;
    public int getArity() { return 1; }
    protected double fx, fy;
    public SineDisplacer() { }
    public SineDisplacer(Random rng) {
        fx = rng.nextDouble() * SINEFREQ + 20;
        fy = rng.nextDouble() * SINEFREQ + 20;
    }
    public double evaluate(double x, double y) {
        double disp = DISPLACEMENT;
        if(1 - x < disp) { disp = 1 - x; }
        if(x + 1 < disp) { disp = x + 1; }
        if(1 - y < disp) { disp = 1 - y; }
        if(y + 1 < disp) { disp = y + 1; }
        double newX = boundsCheck(x + Math.sin(fx * x) * disp);
        double newY = boundsCheck(y + Math.cos(fy * x) * disp);
        return boundsCheck(children[0].evaluate(newX, newY));
    }
}