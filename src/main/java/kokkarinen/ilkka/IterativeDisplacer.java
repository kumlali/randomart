package kokkarinen.ilkka;

import java.util.*;

public class IterativeDisplacer extends Node {
    private static final int ROUNDS = 3;
    private static final double DISPLACEMENT = 0.005;
    public int getArity() { return 3; }
    public double evaluate(double x, double y) {
        for(int round = 0; round < ROUNDS; round++) {
            double disp = DISPLACEMENT;
            if(1 - x < disp) { disp = 1 - x; }
            if(x + 1 < disp) { disp = x + 1; }
            if(1 - y < disp) { disp = 1 - y; }
            if(y + 1 < disp) { disp = y + 1; }
            double newX = boundsCheck(x + children[0].evaluate(x, y) * disp);
            double newY = boundsCheck(y + children[1].evaluate(x, y) * disp);
            x = newX;
            y = newY;
        }
        return boundsCheck(children[2].evaluate(x, y));
    }
}