package kokkarinen.ilkka;

import java.util.*;

public class Tiles extends Node {
    private static final double TILESCALE = 0.3;
    public int getArity() { return 1; }
    protected double tileX, tileY;
    public Tiles() { }
    public Tiles(Random rng) {
        tileX = rng.nextDouble() * TILESCALE + 0.1;
        tileY = rng.nextDouble() * TILESCALE + 0.1;
    }
    public double evaluate(double x, double y) {
        x = scaleToUnity(x);
        y = scaleToUnity(y);
        double tx = Math.floor(x / tileX);
        double ty = Math.floor(y / tileY);
        x = x - tx * tileX;
        y = y - ty * tileY;
        if((int)tx % 2 == 0) {
            x = tileX - x;
        }
        if((int)ty % 2 == 0) {
            y = tileY - y;
        }
        x = x / tileX * 2 - 1;
        y = y / tileY * 2 - 1;
        return boundsCheck(children[0].evaluate(x, y));
    }
}