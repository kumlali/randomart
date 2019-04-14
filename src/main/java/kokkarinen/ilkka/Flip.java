package kokkarinen.ilkka;

import java.util.*;

public class Flip extends Node {
    protected boolean vertical;
    public Flip() { }
    public Flip(Random rng) {
        vertical = rng.nextBoolean();
    }
    public int getArity() { return 1; }
    public double evaluate(double x, double y) {
        if(vertical) {
            return boundsCheck(children[0].evaluate(x, -y));
        }
        else {
            return boundsCheck(children[0].evaluate(-x, y));
        }
    }
}
