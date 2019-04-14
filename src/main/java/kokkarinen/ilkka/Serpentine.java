package kokkarinen.ilkka;

import java.util.*;

public class Serpentine extends Node {
    public int getArity() { return 1; }
    protected double a;
    public Serpentine() { }
    public Serpentine(Random rng) {
        a = rng.nextDouble() * 0.1 + 0.9;
    }
    public double evaluate(double x, double y) {
        double v = children[0].evaluate(x, y);
        double result = (2 * v * a) / (v*v + a*a);
        if(v < -1 || v > 1) {
            System.out.println("Serpentine error! result = " + result);
        }
        return boundsCheck(result);
    }
}