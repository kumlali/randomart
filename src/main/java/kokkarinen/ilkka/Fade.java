package kokkarinen.ilkka;

import java.util.*;

public class Fade extends Node {
    protected double margin;
    public int getArity() { return 2; }
    public Fade() { }
    public Fade(Random rng) {
        margin = rng.nextDouble() * 0.3 + 0.05;
    }
    public double evaluate(double x, double y) {
        if(x < -margin) {
            return children[0].evaluate(x, y);
        }
        if(x > margin) {
            return children[1].evaluate(x, y);
        }
        double v1 = children[0].evaluate(x, y);
        double v2 = children[1].evaluate(x, y);
        double t = (x + margin) / (2 * margin);
        return boundsCheck(linearInt(v2, v1, t));
    }
}