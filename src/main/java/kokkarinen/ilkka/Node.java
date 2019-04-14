package kokkarinen.ilkka;

import java.util.*;
import java.lang.reflect.*;
import java.text.*;

public abstract class Node {
    
    // Each subclass of Node must implement these two methods and the default
    // constructor, and optionally, the constructor that takes a Random.
    public abstract double evaluate(double x, double y);
    public abstract int getArity();
    
    public final String toString() {
        try {
            return toString(0);
        }
        catch(IllegalAccessException e) {
            return "Say what?";
        }
    }
    
    private static final DecimalFormat fmt = new DecimalFormat();
    static {
        fmt.setMaximumFractionDigits(4);
    }
    private final String toString(int depth) throws IllegalAccessException {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < depth; i++) {
            result.append("   ");
        }
        Class c = this.getClass();
        result.append(c.toString().substring(6));
        for(Field field: c.getDeclaredFields()) {
            if(Modifier.isPrivate(field.getModifiers())) continue;
            if(Modifier.isStatic(field.getModifiers())) continue;
            if(field.getType() == double.class) {
                result.append(" " + field.getName() + " " + fmt.format(field.getDouble(this)));
            }
            else if(field.getType() == int.class) {
                result.append(" " + field.getName() + " " + field.getInt(this));
            }
            else if(field.getType() == boolean.class) {
                result.append(" " + field.getName() + " " + field.getBoolean(this));
            }
            else if(field.getType() == String.class && field.get(this) != null) {
                result.append(" " + field.getName() + " " + field.get(this));
            }
        }
        result.append("\n");
        if(this.getArity() > 0) {
            for(Node child: this.children) {
                result.append(child.toString(depth + 1));
            }
        }
        return result.toString();
    }
    
    public int getDepth() {
        if(this.getArity() == 0) { return 0; }
        int d = 1;
        for(Node c: this.children) {
            int cd = c.getDepth() + 1;
            if(cd > d) { d = cd; }
        }
        return d;
    }
    // The children of this node. The evaluate method of each subclass should
    // first recursively evaluate its children and then combine the results.
    // Down the tree, you can of course modify (x,y) as you wish.
    protected Node[] children;
    public final void setChildren(Node[] children) {
        this.children = children;
    }
    
    protected static double boundsCheck(double v, String src) {
        if(v < -1 || v > +1) {
            System.out.println(src + " " + v);
        }
        return boundsCheck(v);
    }
    
    // Static utility methods needed in many places.
    protected static double boundsCheck(double v) {
        if(v < -1) { v = -1; }
        else if(v > +1) { v = +1; }
        return v;
    }
    
    protected static double linearInt(double x, double y, double t) {
        return boundsCheck(t * x + (1 - t) * y);
    }
    protected static double scaleToUnity(double v) {
        return (boundsCheck(v) + 1) / 2;
    }
    protected static double scaleToAngle(double v) {
        return Math.PI * v;
    }
    
    // Methods to calculate various distance metrics.
    
    protected static double distanceSq(double x, double y, double cx, double cy) {
        return (x - cx) * (x - cx) + (y - cy) * (y - cy);
    }
    
    protected static double distance(double x, double y, double cx, double cy) {
        return Math.sqrt(distanceSq(x, y, cx, cy));
    }
    
    protected static double distance(double x, double y, double cx, double cy, double exp) {
        double dx = Math.abs(x - cx);
        double dy = Math.abs(y - cy);
        double d = Math.pow(dx, exp) + Math.pow(dy, exp);
        return Math.pow(d, 1.0 / exp);
    }
    
    protected static double manhattanD(double x, double y, double cx, double cy) {
        double dx = Math.abs(x - cx);
        double dy = Math.abs(y - cy);
        return dx + dy;
    }
    
    protected static double minimumD(double x, double y, double cx, double cy) {
        double dx = Math.abs(x - cx);
        double dy = Math.abs(y - cy);
        return dx < dy ? dx: dy;
    }

}