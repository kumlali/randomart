package kokkarinen.ilkka;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class PointListTurtle extends AbstractTurtle {
    
    private short[] xs;
    private short[] ys;
    private int idx = 0;
    
    public PointListTurtle(int size) {
        xs = new short[size];
        ys = new short[size];
    }
    public int getSize() { return idx; }
    public short getX(int i) {
        if(i >= idx) { return xs[idx - 1]; }
        return xs[i];
    }
    public short getY(int i) {
        if(i >= idx) { return ys[idx - 1]; }
        return ys[i];
    }
    
    @Override public void move(double dist) {
        
        xs[idx] = (short)(Math.round(this.getX()));
        ys[idx] = (short)(Math.round(this.getY()));
        idx++;
        double a = Math.toRadians(heading);
        double x2 = x + dist * Math.cos(a);
        double y2 = y + dist * Math.sin(a);
        x = x2; y = y2;
    }

}