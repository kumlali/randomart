package kokkarinen.ilkka;

import java.util.*;

public class SoftTranslate extends Node {
    private static final double TRANS = 0.95;
    public int getArity() { return 3; }
    public double evaluate(double x, double y) {
        double newX = linearInt(x, children[0].evaluate(x, y), TRANS);
        double newY = linearInt(y, children[1].evaluate(x, y), TRANS);
        return boundsCheck(children[2].evaluate(newX, newY));
    }
}