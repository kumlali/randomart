package kokkarinen.ilkka;

import java.util.*;

public class SuperEllipse extends Node {
    public int getArity() { return 1; }
    protected double exp;
    protected double sign;
    public SuperEllipse() { }
    public SuperEllipse(Random rng) {
        exp = rng.nextDouble() * 1.5 + 3.0;
        sign = rng.nextBoolean()? +1: -1;
    }
    public double evaluate(double x, double y) {
        double v = children[0].evaluate(x, y);
        if(v >= 0) {
            return boundsCheck(sign * Math.pow(v, exp));
        }
        else {
            return boundsCheck(sign * Math.pow(-v, exp));
        }
    }
}