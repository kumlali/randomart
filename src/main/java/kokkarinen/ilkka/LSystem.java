package kokkarinen.ilkka;

import java.util.*;
import java.awt.geom.*;

// A class to compute a Lindenmayer System program given as set of rules,
// where each character is interpreted as an instruction for the turtle to
// do something clever.

public class LSystem {

    protected double stepSize;
    protected double turnAngle;
    // The LSystem class doesn't concern itself with the actual rendering,
    // which is done with the Turtle given to it as a strategy object.
    // The LSystem just generates the instructions for the Turtle to draw.
    protected Turtle turtle;
    
    public LSystem(Turtle turtle, double stepSize, double turnAngle) {
        this.turtle = turtle;
        this.stepSize = stepSize;
        this.turnAngle = turnAngle;
    }
    
    // To simplify subclass definitions
    public LSystem() { }

    // Recursive execution of an L-system is pretty straightforward. Instead
    // of expanding the program string completely and then executing it in order,
    // we "expand" the program locally with recursive calls as needed.
    public void execute(Map<Character, String> rules, String program, int depth) {
        if(program == null || depth < 0) { return; }
        for(int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            if(depth > 0 && rules.containsKey(c)) {
                execute(rules, rules.get(c), depth - 1);
            }
            else {
                handleChar(c, depth);
            }
        }
    }  

    // The characters that are treated as instructions for the Turtle.
    // A subclass may override this method to do something more special.
    public void handleChar(char c, int depth) {
        if(c == '>') { turtle.move(stepSize); }
        else if(c == '+') { turtle.turn(turnAngle); }
        else if(c == '-') { turtle.turn(-turnAngle); }
        else if(c == '[') { turtle.pushState(); }
        else if(c == ']') { turtle.popState(); }    
    }
}