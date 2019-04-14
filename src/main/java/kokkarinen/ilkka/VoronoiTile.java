package kokkarinen.ilkka;

import java.util.*;
import java.util.concurrent.locks.*;

public class VoronoiTile extends Node {
    private static final int VOROMAX = 20;
    protected boolean smooth;
    protected int seed, type;
    protected double margin;
    private volatile double[] cx, cy;
    private int total;
    private Lock mutex = new ReentrantLock();
    
    public int getArity() { return 2; }
    public VoronoiTile() { }
    public VoronoiTile(Random rng) {
        type = rng.nextInt(3);
        smooth = rng.nextBoolean();
        seed = rng.nextInt();
        margin = rng.nextDouble() * 0.04;
    }
    public double evaluate(double x, double y) {
        double md1 = +1000, md2 = +1000, d;
        if(cx == null) {
            mutex.lock();
            double[] ccx = null;
            if(cx == null) {
                Random prng = new Random(seed); 
                total = prng.nextInt(VOROMAX) + 10;
                ccx = new double[total];
                cy = new double[total];
                for(int i = 0; i < total; i++) {
                    ccx[i] = prng.nextDouble() * 2 - 1;
                    cy[i] = prng.nextDouble() * 2 - 1;
                }
            }
            cx = ccx;
            mutex.unlock();
        }
        for(int i = 0; i < total; i++) {
            if(type == 0) {
                d = manhattanD(x, y, cx[i], cy[i]); 
            }
            else if(type == 1){
                d = distanceSq(x, y, cx[i], cy[i]);
            }
            else {
                d = minimumD(x, y, cx[i], cy[i]);
            }
            if(i % 2 == 0 && d < md1) { md1 = d; }
            if(i % 2 == 1 && d < md2) { md2 = d; }
        }
   
        if(smooth || Math.abs(md1 - md2) < margin) {
            return boundsCheck(linearInt(
                children[0].evaluate(x, y), children[1].evaluate(x,y),
                md2 / (md1 + md2)
            ));
        }
        else if(md1 < md2) {
            return boundsCheck(children[0].evaluate(x, y));
        }
        else {
            return boundsCheck(children[1].evaluate(x, y));
        }
    }
}