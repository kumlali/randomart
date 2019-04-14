package kokkarinen.ilkka;

import java.util.*;

public class Apply extends Node {
    public int getArity() { return 3; }
    public double evaluate(double x, double y) {
        double newX = boundsCheck(children[0].evaluate(x, y));
        double newY = boundsCheck(children[1].evaluate(x, y));
        return boundsCheck(children[2].evaluate(newX, newY));
        
    }
}