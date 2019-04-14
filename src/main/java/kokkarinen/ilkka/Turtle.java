package kokkarinen.ilkka;

import java.awt.*;

public interface Turtle {
    
    // The Graphics2D object that this turtle draws on.
    public void setGraphics2D(Graphics2D g);
    
    // The methods to set and get the absolute position of the turtle.
    // Usually you call these once in the beginning, then do the
    // rest of the drawing with the relative methods below.
    public void setPosition(double x, double y);
    public void setHeading(double angle);
    public double getX();
    public double getY();
    public double getHeading();
    
    // Settings for the pen of the turtle.
    public void setPen(boolean up);
    public void setColor(Color color);
    public void setStroke(Stroke stroke);
    
    // The turtle has an internal stack in which it can push its
    // current position and pop it back later, which is useful for
    // implementing various branching effects.
    public void pushState();
    public void popState();
    
    // The relative geometry methods.
    public void move(double dist);
    public void turn(double turnAngle);
    
}