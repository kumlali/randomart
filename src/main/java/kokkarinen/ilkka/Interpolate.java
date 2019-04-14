package kokkarinen.ilkka;

import java.util.*;

public class Interpolate extends Node {
    public int getArity() { return 3; }
    public double evaluate(double x, double y) {
        double v0 = boundsCheck(children[0].evaluate(x, y));
        double v1 = boundsCheck(children[1].evaluate(x, y));
        double t = scaleToUnity(boundsCheck(children[2].evaluate(x, y)));
        return boundsCheck(linearInt(v0, v1, t));
    }
}