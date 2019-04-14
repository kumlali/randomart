package kokkarinen.ilkka;

import java.util.*;

public class RadialTile extends Node {
    private static final double TILESCALE = 0.3;
    public int getArity() { return 1; }
    protected double tileD, cx, cy, strength, stretchA, freqA, stretchD, freqD;
    protected boolean manhattan, perspective;
    public RadialTile() { }
    public RadialTile(Random rng) {
        tileD = rng.nextDouble() * TILESCALE + 0.3;
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
        manhattan = rng.nextBoolean();
        perspective = rng.nextBoolean();
        strength = rng.nextDouble() * 0.4;
        stretchA = rng.nextDouble() * 0.4;
        freqA = rng.nextDouble() * 5;
        stretchD = rng.nextDouble() * 0.4;
        freqD = rng.nextDouble() * 5;
    }
    public double evaluate(double x, double y) {
        if(Math.abs(x - cx) < 0.003 && Math.abs(y - cy) < 0.003) {
            return children[0].evaluate(cx, cy);
        }
        double d, angle;
        d = distance(x, y, cx, cy);
        angle = (2 * Math.acos((x - cx) / d) - Math.PI) / Math.PI;
        
        if(manhattan) { d = manhattanD(x, y, cx, cy); }
        if(perspective) { d = Math.sqrt(1.0 / d); }
        d = d * (1 + stretchD * Math.cos(freqD * angle));
        angle = angle + strength * d - stretchA * Math.cos(freqA * d);
        if(angle > 1) { angle = 2 - angle; }
        else if(angle < -1) { angle = -2 - angle; }
        double td = Math.floor(d / tileD);
        d = d - td * tileD;
        if((int)td % 2 == 0) {
            d = tileD - d;
        }    
        d = d / tileD * 2 - 1;
        if(d < -1 || d > 1) {
            System.out.println("Error in radialtile: d is " + d);
        }
        return boundsCheck(children[0].evaluate(d, angle));
    }
}