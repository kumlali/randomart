package kokkarinen.ilkka;

import java.util.*;

public class WeightedSum extends Node {
    protected double t;
    public int getArity() { return 2; }
    public WeightedSum() { }
    public WeightedSum(Random rng) {
        t = rng.nextDouble() * 0.8 + 0.1;
    }
    public double evaluate(double x, double y) {
        double v0 = boundsCheck(children[0].evaluate(x, y));
        double v1 = boundsCheck(children[1].evaluate(x, y));
        return boundsCheck(linearInt(v0, v1, t));
    }
}