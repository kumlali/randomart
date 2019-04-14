package kokkarinen.ilkka;

import java.util.*;
import java.util.concurrent.locks.*;

public class Stripes extends Node {
    
    public int getArity() { return 0; }
    protected int seed;
    protected boolean vertical;
    protected double disp;
    protected double a, b, f;
    private volatile double[] c;
    private Lock mutex = new ReentrantLock();
    public Stripes() { }
    public Stripes(Random rng) {
        seed = rng.nextInt();
        vertical = rng.nextBoolean();
        disp = rng.nextDouble() * 0.1;
        a = rng.nextDouble() * 10 - 5;
        b = rng.nextDouble() * 10 - 5;
        f = rng.nextDouble() * FALLOFF + 7;
    }
    
    private static final double FALLOFF = 5;
    public double evaluate(double x, double y) {
        if(c == null) {
            mutex.lock();
            if(c == null) {
                Random prng = new Random(seed);
                c = new double[5 + prng.nextInt(10)];
                for(int i = 0; i < c.length; i++) {
                    c[i] = prng.nextDouble() * 2 - 1;
                }
            }
            mutex.unlock();
        }
        x = x + Math.cos(a*y*y + b*y) * disp;
        y = y + Math.sin(a*x*x + b*x) * disp;
        double d = +1000, d2;
        for(int i = 0; i < c.length; i++) {
            if(vertical) { d2 = Math.abs(x - c[i]); }
            else { d2 = Math.abs(y - c[i]); }
            if(d2 < d) { d = d2; }
        }
        return boundsCheck(2 * Math.exp(-d * f) - 1);
    }
}
