package kokkarinen.ilkka;

import java.util.*;

public class HarmonicSum extends Node {
    
    public int getArity() { return 1; }
    protected int bits;
    protected double cx, cy;
    public HarmonicSum() { }
    public HarmonicSum(Random rng) {
        bits = rng.nextInt(15) + 1;
        cx = rng.nextDouble() * 2 - 1;
        cy = rng.nextDouble() * 2 - 1;
    }
    public double evaluate(double x, double y) {
        double result = 0;
        double weight = 1.0;
        int count = 1;
        int b = bits;
        while(b > 0) {
            if(b % 2 == 1) {
                weight = weight / 2;
                result += weight * children[0].evaluate(
                    linearInt(x, cx, 1.0 / count),
                    linearInt(y, cy, 1.0 / count)
                );
            }
            b = b / 2;
            count++;
        }
        return boundsCheck(result / (1 - weight));
    }
}