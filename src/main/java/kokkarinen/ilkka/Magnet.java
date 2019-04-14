package kokkarinen.ilkka;

import java.util.*;

public class Magnet extends Node {
    private static final int MAGMAX = 5;
    private static final double MAGSTRENGTH = 0.1;
    
    protected double cx, cy, s;
    
    public int getArity() { return 1; }
    public Magnet() { }
    public Magnet(Random rng) {
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
        s = rng.nextDouble() * MAGSTRENGTH + 0.01;
    }
    
    public double evaluate(double x, double y) {     
        double d = distanceSq(x, y, cx, cy);
        if(d >= 0.0001) {
            x += s * (x - cx) / (d * Math.sqrt(d));
            y += s * (y - cy) / (d * Math.sqrt(d));
            x = boundsCheck(x);
            y = boundsCheck(y);
        }
        return children[0].evaluate(x, y);
    }
}