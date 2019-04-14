package kokkarinen.ilkka;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class RandomArtPanel extends JPanel {

    private int width, height, depth, mode;
    private long seed;
    private BufferedImage img = null;
    private javax.swing.Timer timer;
    private Random rng;
    private JFileChooser jfc = new JFileChooser();
    private boolean busy = false; // Is rendering currently taking place?
    private Semaphore busySem; // The semaphore used to signal that rendering is done
    private Node[] nodes = new Node[3];
    private File currFile = null;
    private RandomArt ra = new RandomArt();
    
    public void setMode(int mode) { this.mode = mode; }
    public int getMode() { return this.mode; }
    public void setDepth(int depth) { this.depth = depth; }
    public int getDepth() { return this.depth; }
    
    private static ExecutorService es = Executors.newCachedThreadPool();
    
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
    
    public RandomArtPanel(int width, int height, int depth) {
        this.setPreferredSize(new Dimension(width, height));
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.seed = System.currentTimeMillis();
        this.rng = new Random(seed);
        this.mode = RandomArt.HSB;
        this.img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.timer = new javax.swing.Timer(40, new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if(busy) { repaint(); }
            }
        });
        timer.start();
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                if(busy) { return; }
                // Left button -> increment seed.
                if(me.getButton() == MouseEvent.BUTTON1) {
                    rng = new Random(++RandomArtPanel.this.seed);
                    createTrees(RandomArtPanel.this.depth, rng);
                    render(false);
                }
                // Middle button -> switch rendering mode.
                else if(me.getButton() == MouseEvent.BUTTON2) {
                    if(mode == RandomArt.HILBERT) { mode = RandomArt.RGB; }
                    else if(mode == RandomArt.RGB) { mode = RandomArt.GRAY; }
                    else if(mode == RandomArt.GRAY) { mode = RandomArt.HSB; }
                    else { mode = RandomArt.HILBERT; }
                    render(true);
                }
                else { // Right button -> save source.
                    if(currFile != null) { jfc.setSelectedFile(currFile); }
                    int returnVal = jfc.showSaveDialog(RandomArtPanel.this);
                    if(returnVal == JFileChooser.APPROVE_OPTION) {
                        PrintWriter pw = null;
                        try {
                            currFile = jfc.getSelectedFile();
                            pw = new PrintWriter(new FileWriter(currFile));
                            pw.println(nodes[0].toString());
                            pw.println(nodes[1].toString());
                            pw.println(nodes[2].toString());
                        }
                        catch (Exception e) {
                            System.out.println("Error saving file: " + e);
                        }
                        finally {
                            try { pw.close(); } catch(Exception e) { }
                        }
                    }
                }
            }
        });
        createTrees(this.depth, rng);
        render(false);
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
        busy = true; 
        busySem = new Semaphore(0);
        es.submit(new Runnable() { // The task to render the image
            public void run() {
                long startTime = System.currentTimeMillis();
                System.out.println("# Starting new " + RandomArt.modes[mode] + " render of depth " + depth);
                ra.render(img, nodes, mode, busySem);
                long endTime = System.currentTimeMillis();
                System.out.println("# Finished rendering in " + (endTime - startTime) + " ms.");
            }
        });
        es.submit(new Runnable() { // The task to wait for image to complete
            public void run() {
                try {
                    busySem.acquire();
                } catch(InterruptedException e) { }
                busy = false;
                repaint(); // one more for the road, just to be safe
            }
        });
    }
    
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(img != null) { g.drawImage(img, 0, 0, this); }
    }
    
    public void finalize() { timer.stop(); }
    
    private JPanel createRowPanel(JSlider slider, JLabel label) {
        JPanel row = new JPanel();
        row.setPreferredSize(new Dimension(290, 30));
        row.setLayout(new GridLayout(1, 2));
        row.add(label);
        row.add(slider);
        return row;
    }

    public JPanel createControlPanel() {
        final JPanel result = new JPanel();
        result.setPreferredSize(new Dimension(300, (nodeTypes.length + 1) * 35));
        result.setLayout(new BoxLayout(result, BoxLayout.PAGE_AXIS));
        JButton loadTextures = new JButton("Load Textures");
        result.add(loadTextures);
        loadTextures.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if(currFile != null) { jfc.setSelectedFile(currFile); }
                int returnVal = jfc.showOpenDialog(result);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    currFile = jfc.getSelectedFile();
                    Texture.addImagesFromFolder(currFile);
                    System.out.println("Finished reading the image files.");
                }
            }
        });
        JButton loadChannels = new JButton("Load Source");
        result.add(loadChannels);
        loadChannels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = jfc.showOpenDialog(result);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    BufferedReader br = null;
                    try {
                        br = new BufferedReader(new FileReader(jfc.getSelectedFile()));
                        ChannelParser cp = new ChannelParser(br);
                        nodes = cp.parseChannels();
                    }
                    catch(ParseException e) {
                        System.out.println("Parse error in line " + e.getErrorOffset() + ": " + e);
                    }
                    catch(Exception e) {
                        System.out.println("Error reading file: " + e);
                    }
                    finally {
                        try { if(br != null) { br.close(); } } catch(Exception e) { }
                    }
                }
                render(false);
            }
        });

        final JSlider depthSlider = new JSlider(1, 13, getDepth());
        depthSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent ce) {
                    setDepth(depthSlider.getValue());
                }
            });
        result.add(createRowPanel(depthSlider, new JLabel("Tree depth")));
        result.add(Box.createRigidArea(new Dimension(0,5)));

        for(int i = 0; i < nodeTypes.length; i++) {
            JSlider probSlider = new JSlider(0, 100, (Integer)nodeTypes[i][1]);
            probSlider.setMajorTickSpacing(10);
            probSlider.setPaintTicks(true);
            probSlider.addChangeListener(new MyChangeListener(i));
            result.add(createRowPanel(probSlider, new JLabel(nodeTypes[i][0].toString().substring(23))));
        }
        return result;
    }

    private static class MyChangeListener implements ChangeListener {
        private int row;
        public MyChangeListener(int row) { this.row = row; }

        public void stateChanged(ChangeEvent ce) {
            nodeTypes[row][1] = ((JSlider)ce.getSource()).getValue();
        }
    }
    
    // For demonstration purposes.
    public static void launch() {
        final JFrame f = new JFrame("Random Art!");
        final RandomArtPanel rap = new RandomArtPanel(800, 800, 7);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                rap.finalize();
                f.dispose();
            }
        });
        f.setLayout(new FlowLayout());
        f.add(rap);
        f.pack();
        f.setVisible(true);
        JFrame fc = new JFrame("Random Art Control Panel");
        fc.setLayout(new FlowLayout());
        fc.add(rap.createControlPanel());
        fc.pack();
        fc.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fc.setLocation(820, 10);
        fc.setVisible(true); 
    }    
    
    public static void main(String[] args) {
        launch();
    }
}