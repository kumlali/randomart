package kokkarinen.ilkka;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;

public class RandomArtCli {

    private int depth, mode;
    private long seed;
    private BufferedImage img = null;
    private Random rng;
    private Semaphore busySem; // The semaphore used to signal that rendering is done
    private Node[] nodes = new Node[3];
    private RandomArt ra = new RandomArt();
    
    public void setMode(int mode) { this.mode = mode; }
    public int getMode() { return this.mode; }
    public void setDepth(int depth) { this.depth = depth; }
    public int getDepth() { return this.depth; }
    
    
    // The Node subclasses that we use to construct the random trees, given as
    // pairs of { classname, acceptance probability }.
    protected static Object[][] nodeTypes = {
        // Binary and ternary nodes
        { Product.class, 80 },
        { SoftMax.class, 80 },
        { Interpolate.class, 100 },
        { Fade.class, 50 },
        { SuperEllipse.class, 50 },
        { WeightedSum.class, 100 },
        { VoronoiTile.class, 100 },
        
        // Unary nodes
        { Sigmoid.class, 70 },
        { Serpentine.class, 40 },
        //{ WackyTan.class, 10 },
        { HarmonicSum.class, 60 },
        { SineSum.class, 100 },
        { ValueGrid.class, 100 },
        { Magnet.class, 100 },

        // Leaf nodes
        { Coordinate.class, 50 },
        //{ Clelie.class, 30 },
        { PointLight.class, 60 },
        { SuperEllipseLeaf.class, 60 },
        { SuperFormula.class, 100 },
        { Texture.class, 80 },
        { Stripes.class, 100 },

        // Nodes that modify (x, y) going down
        { Tiles.class, 50 },
        { SineTile.class, 50 },
        { RadialTile.class, 50 },
        { Flip.class, 60 },
        { Vortex.class, 80 },
        { SoftTranslate.class, 50 },
        { SineDisplacer.class, 50 },
        //{ IterativeDisplacer.class, 50 },
        { Apply.class, 30 }
    };
    
    public RandomArtCli(int width, int height, int depth, int mode) {
        this.depth = depth;
        this.seed = System.currentTimeMillis();
        this.rng = new Random(seed);
        this.mode = mode;
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        createTrees(depth, rng);
    }

    private void createTrees(int depth, Random rng) {
        for(int i = 0; i < 3; i++) {
            try {
                Sigmoid root = new Sigmoid();
                root.scale = 7;
                Node[] actualTree = { createTree(depth, rng) };
                nodes[i] = root;
                nodes[i].setChildren(actualTree);
            }
            catch(Exception e) { return; }
        }    
    }
    
    private static Node createNode(int depth, Random rng)
    throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Node n;
        do {
            int idx;
            do {
                idx = rng.nextInt(nodeTypes.length);
            } while(rng.nextInt(100) >= (Integer)nodeTypes[idx][1]);
            Class c = (Class) nodeTypes[idx][0];
            Constructor defCon = null; // default constructor
            Constructor ranCon = null; // Random constructor
            for(Constructor con : c.getConstructors()) {
                Class[] params = con.getParameterTypes();
                if(params.length == 0) { defCon = con; }
                if(params.length == 1 && params[0] == Random.class) { ranCon = con; }
            }
            // Use Random constructor if found, otherwise use default constructor.
            if(ranCon != null) { n = (Node)(ranCon.newInstance(rng)); }
            else { n = (Node)(defCon.newInstance()); }
        } while((depth < 2 && n.getArity() > 0) || (depth > 5 && n.getArity() == 0));
        return n;
    }

    // Create a tree of given depth, using the given rng.
    public static Node createTree(int depth, Random rng)
    throws InstantiationException, IllegalAccessException, InvocationTargetException {
        Node n = createNode(depth, rng);
        // Create the children recursively.
        Node[] children = new Node[n.getArity()];
        for(int i = 0; i < n.getArity(); i++) {
            children[i] = createTree(depth - 1, rng);
        }
        n.setChildren(children);
        return n;
    }
    
    public static java.util.List<Node> createDAG(int count, Random rng)
    throws InstantiationException, IllegalAccessException, InvocationTargetException {
        java.util.List<Node> nodes = new ArrayList<Node>();
        for(int c = 0; c < count; c++) {
            Node n = createNode(c / 10 + 1, rng);
            Node[] children = new Node[n.getArity()];
            for(int i = 0; i < n.getArity(); i++) {
                children[i] = nodes.get(rng.nextInt(nodes.size()));
            }
            n.setChildren(children);
            nodes.add(n);
        }
        return nodes;
    }
    
    // Render the image using the current depth, mode and rng settings.
    private void render(final boolean reuseColours) {
        long startTime = System.currentTimeMillis();
        System.out.println("# Starting new " + RandomArt.modes[mode] + " render of depth " + depth);
        ra.render(img, nodes, mode, busySem);
        long endTime = System.currentTimeMillis();
        System.out.println("# Finished rendering in " + (endTime - startTime) + " ms.");
    }

    private void save(String out) throws IOException {
    	ImageIO.write(img, "jpg", new File(out));
        System.out.println("# " + out + " has been created.");
    }

    public static void main(String[] args) throws IOException {        
    	if (args.length > 0) {
	        final String out = args[0];
	    	final RandomArtCli rap = new RandomArtCli(800, 800, 7, new Random().nextInt(4));
            rap.render(false);
        	rap.save(out);
    	} else {
            System.out.println("Usage: java -jar randomart-0.0.1-SNAPSHOT.jar path/file.jpg");
    	}
    	System.exit(0);
    }
}