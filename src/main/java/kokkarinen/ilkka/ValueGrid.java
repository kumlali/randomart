package kokkarinen.ilkka;

import java.util.concurrent.locks.*;
import java.util.*;

public class ValueGrid extends Node {
    protected double tile;
    protected int seed;
    private volatile double[][] mag;
    private Lock mutex = new ReentrantLock();
    
    public int getArity() { return 1; }
    public ValueGrid() { }
    public ValueGrid(Random rng) { 
        seed = rng.nextInt();
        tile = rng.nextDouble() * 0.1 + 0.05;
    }
    
    public double evaluate(double x, double y) {
        if(mag == null) {
            mutex.lock();
            double[][] magg = null;
            if(mag == null) {
                Random prng = new Random(seed);
                int size = (int)Math.ceil(1.0 / tile);
                magg = new double[size+1][size+1];
                for(int tx = 0; tx < size; tx++) {
                    for(int ty = 0; ty < size; ty++) {
                        magg[tx][ty] = prng.nextDouble() * 0.3 - 0.15;
                    }
                }
            }
            mag = magg;
            mutex.unlock();
        }
        double v = children[0].evaluate(x, y);
        x = scaleToUnity(x);
        y = scaleToUnity(y);
        int tx = (int)Math.floor(x / tile);
        int ty = (int)Math.floor(y / tile);
        double ox = (x - tx * tile) / tile;
        double oy = (y - ty * tile) / tile;
        
        v = v + (2 * tile - manhattanD(x, y, tx * tile, ty * tile)) * mag[tx][ty];
        v = v + (2 * tile - manhattanD(x, y, tx * tile, (ty+1) * tile)) * mag[tx][ty+1]; 
        v = v + (2 * tile - manhattanD(x, y, (tx+1) * tile, ty * tile)) * mag[tx+1][ty];
        v = v + (2 * tile - manhattanD(x, y, (tx+1) * tile, (ty+1) * tile)) * mag[tx+1][ty+1];
        v = boundsCheck(v);
        
        return v;
    }
    
}
