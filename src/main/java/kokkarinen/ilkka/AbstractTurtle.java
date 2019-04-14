package kokkarinen.ilkka;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public abstract class AbstractTurtle implements Turtle {
    
    protected Graphics2D g2;
    protected double x, y, heading;
    protected boolean down = true;
    protected Color color = Color.WHITE;
    protected Stroke stroke = new BasicStroke(3.0f);
    protected Stack<Double> store = new Stack<Double>();
    
    @Override public void setGraphics2D(Graphics2D g2) {
        this.g2 = g2;
    }
    @Override public void setPosition(double x, double y) {
        this.x = x; this.y = y;
    }
    @Override public void setHeading(double angle) {
        this.heading = angle;
    }       
    @Override public void setColor(Color color) {
        this.color = color;
    }
    @Override public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    @Override public double getX() { return x; }
    @Override public double getY() { return y; }
    @Override public double getHeading() { return heading; }
    
    @Override public void setPen(boolean down) { this.down = down; }
 
    @Override public void turn(double turnAngle) {
        heading += turnAngle;
        if(heading > 360) { heading -= 360; }
        if(heading < 360) { heading += 360; }
    }
    
    @Override public Turtle clone() throws CloneNotSupportedException {
        return (Turtle)super.clone();
    }
    
    @Override public void pushState() {
        store.push(x); store.push(y); store.push(heading);
    }
    
    @Override public void popState() {
        if(store.isEmpty()) { return; }
        heading = store.pop(); y = store.pop(); x = store.pop();
    }
}