package kokkarinen.ilkka;

import java.util.*;

public class SineTile extends Node {
    private static final double TILESCALE = 3;
    public int getArity() { return 1; }
    protected double fx, fy, ox, oy, fdx, fdy;
    public SineTile() { }
    public SineTile(Random rng) {
        fx = rng.nextDouble() * TILESCALE + 1.0;
        fy = rng.nextDouble() * TILESCALE + 1.0;
        fdx = rng.nextDouble() * rng.nextDouble() * 0.3;
        fdy = rng.nextDouble() * rng.nextDouble() * 0.3;
        ox = rng.nextDouble() * 0.5;
        oy = rng.nextDouble() * 0.5;
    }
    public double evaluate(double x, double y) {
        return boundsCheck(children[0].evaluate(
            Math.sin((x + fdx * Math.sin(x)) * fx + ox),
            Math.sin((y + fdy * Math.cos(y)) * fy + oy)
        ));
    }
}