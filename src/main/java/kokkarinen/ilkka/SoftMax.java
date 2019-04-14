package kokkarinen.ilkka;

import java.util.*;

public class SoftMax extends Node {
    public int getArity() { return 2; }
    protected double t;
    public SoftMax() { }
    public SoftMax(Random rng) {
        t = 0.80 + rng.nextDouble() * 0.20;
    }
    public double evaluate(double x, double y) {
        double v0 = children[0].evaluate(x, y);
        double v1 = children[1].evaluate(x, y);
        if(v0 > v1) {
            return boundsCheck(linearInt(v0, v1, t));
        }
        else {
            return boundsCheck(linearInt(v1, v0, t));
        }
    }
}