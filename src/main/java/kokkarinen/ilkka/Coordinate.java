package kokkarinen.ilkka;

import java.util.*;

public class Coordinate extends Node {
    public int getArity() { return 0; }
    protected boolean useX;
    public Coordinate() { }
    public Coordinate(Random rng) {
        useX = rng.nextBoolean();
    }
    public double evaluate(double x, double y) {
        return boundsCheck(useX? x: y);
    }
}