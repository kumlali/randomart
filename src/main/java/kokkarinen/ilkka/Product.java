package kokkarinen.ilkka;

import java.util.*;

public class Product extends Node {
    public int getArity() { return 2; }
    protected int sign;
    public Product() { }
    public Product(Random rng) {
        sign = rng.nextBoolean()? +1: -1;
    }
    public double evaluate(double x, double y) {
        return boundsCheck(sign * children[0].evaluate(x, y) * children[1].evaluate(x, y));
    }
}
