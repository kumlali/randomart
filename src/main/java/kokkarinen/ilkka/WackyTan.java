package kokkarinen.ilkka;

public class WackyTan extends Node {
    public int getArity() { return 1; }
    public double evaluate(double x, double y) {
        double a = scaleToAngle(children[0].evaluate(x, y));
        return boundsCheck((Math.tan(Math.sin(a)) - Math.sin(Math.tan(a))) / 3);
    }
}