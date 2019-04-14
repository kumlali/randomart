package kokkarinen.ilkka;

import java.util.*;
import java.util.concurrent.locks.*;

public class SineSum extends Node {
    public int getArity() { return 1; }
    protected int seed;
    
    public SineSum() { }
    public SineSum(Random rng) {
        seed = rng.nextInt();
    }
    private volatile double[] fx, ox;
    private Lock mutex = new ReentrantLock();
    private int terms;
    private static final int SINEPROB = 50;
    private static final int SINEFREQ = 2;
    public double evaluate(double x, double y) {
        double result = 0.0, w = 1.0;
        double[] ffx = null;
        if(fx == null) {
            mutex.lock();
            if(fx == null) {
                Random prng = new Random(seed);
                terms = 2;
                while(terms < 10 && prng.nextInt(100) < SINEPROB) { terms++; }
                ffx = new double[terms];
                ox = new double[terms];
                for(int i = 0; i < terms; i++) {
                    ffx[i] = prng.nextDouble() + 1;
                    ox[i] = prng.nextDouble();
                }
            }
            fx = ffx;
            mutex.unlock();
        }
        double v = children[0].evaluate(x, y);
        for(int i = 0; i < terms; i++) {
            w = w / 2;
            result += w * Math.sin(fx[i] * SINEFREQ * (v + ox[i]));
        }

        return result / (1 - w);
    }
}