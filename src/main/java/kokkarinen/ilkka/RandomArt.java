package kokkarinen.ilkka;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class RandomArt {

    // The colour modes available for rendering.
    public static final int RGB = 0;
    public static final int GRAY = 1;
    public static final int HSB = 2;
    public static final int HILBERT = 3;
    public static final String[] modes = {"RGB", "GRAY", "HSB", "HILBERT"};

    private static PointListTurtle plt = new PointListTurtle(1 << 20);
    private static double txMax = -1000, tyMax = -1000, txMin = 1000, tyMin = 1000;
    static {
        Map<Character, String> hilbertRules = new HashMap<Character, String>();
        hilbertRules.put('L', "+R>-L>L->R+");
        hilbertRules.put('R', "-L>+R>R+>L-");
        plt.setPosition(0, 0);
        plt.setHeading(180);
        new LSystem(plt, 1, 90).execute(hilbertRules, "L", 10);
        for(int i = 0; i < plt.getSize(); i++) {
            if(plt.getX(i) > txMax) { txMax = plt.getX(i); }
            if(plt.getX(i) > tyMax) { tyMax = plt.getY(i); }
            if(plt.getX(i) < txMin) { txMin = plt.getX(i); }
            if(plt.getY(i) < tyMin) { tyMin = plt.getY(i); }
        }

    }

    // The Executor that handles the concurrent rendering of tiles.
    private static ExecutorService es = Executors.newCachedThreadPool();
    // The size of an individual tile in pixels.
    private static final int TILECUTOFF = 40000;
    
    // Render the image into the given BufferedImage If the semaphore done is not
    // null, this method will release it when all pixels have been rendered.
    public BufferedImage render(final BufferedImage img, final Node[] nodes,
    final int mode, final Semaphore allDone) {  
        final int width = img.getWidth(null);
        final int height = img.getHeight(null);

        class ImageFiller implements Runnable {
            private int sx, sy, ex, ey;
            private Semaphore done;
            private boolean semReleased = false;
            // Fill the part of image from top (sx,sy) up to (ex, ey).
            public ImageFiller(int sx, int sy, int ex, int ey, Semaphore done) {
                this.sx = sx; this.sy = sy; this.ex = ex; this.ey = ey;
                this.done = done;
            }

            public void run () {
                try {
                    int tiles = (ex - sx) * (ey - sy);
                    if(tiles > TILECUTOFF) { // split recursively into four tiles
                        int midx = (ex + sx) / 2, midy = (ey + sy) / 2;
                        Semaphore subTaskSem = new Semaphore(-3);
                        es.submit(new ImageFiller(sx, midy, midx, ey, subTaskSem));
                        es.submit(new ImageFiller(midx, sy, ex, midy, subTaskSem));
                        es.submit(new ImageFiller(midx, midy, ex, ey, subTaskSem));
                        es.submit(new ImageFiller(sx, sy, midx, midy, subTaskSem));
                        try { subTaskSem.acquire(); } catch(InterruptedException e) { }
                    }
                    else {
                        int idx = 0; // The 1d index of the pixel that we are currently filling
                        for(int i = 0; i < tiles; i++) {
                            int x = sx + idx / (ex-sx);
                            int y = sy + idx % (ex-sx);
                            idx = (idx + 3389) % tiles;
                            if(x >= width || y >= height) { continue; }
                            double px = -1.0 + x * 2.0 / (width + 1);
                            double py = -1.0 + y * 2.0 / (height + 1);
                            if(mode == HILBERT) {
                                double turtle = Node.scaleToUnity(Node.boundsCheck(nodes[0].evaluate(px, py), "Hilbert 1"));
                                turtle = Math.floor(plt.getSize() * turtle);
                                double r = plt.getX((int)turtle);
                                r = (r - txMin) / (txMax - txMin);
                                double g = plt.getY((int)turtle);
                                g = (g - tyMin) / (tyMax - tyMin);
                                double b = Node.scaleToUnity(Node.boundsCheck(nodes[1].evaluate(px, py)));
                                r = 255 * r;
                                g = 255 * g;
                                b = 255 * b;

                                img.setRGB(x, y, (((int)r) << 16) | (((int)g) << 8) | ((int)b));
                            }
                            else {
                                double r = Node.scaleToUnity(Node.boundsCheck(nodes[0].evaluate(px, py)));
                                double g = Node.scaleToUnity(Node.boundsCheck(nodes[1].evaluate(px, py)));
                                double b = Node.scaleToUnity(Node.boundsCheck(nodes[2].evaluate(px, py)));
                                if(mode == GRAY) {
                                    b = 255 * b * Math.pow(0.8 + 0.2 * r, 1.0 / (0.2 + 0.8 * g));
                                    img.setRGB(x, y, (((int)b) << 16) | (((int)b) << 8) | ((int)b));
                                }
                                else if(mode == HSB) {
                                    // Use b as brightness, g as saturation, r as hue
                                    img.setRGB(x, y, Color.HSBtoRGB((float)r, (float)g, (float)b));
                                }
                                else {
                                    r = 255 * r;
                                    g = 255 * g;
                                    b = 255 * b;
                                    img.setRGB(x, y, (((int)r) << 16) | (((int)g) << 8) | ((int)b));
                                }
                            }
                        }
                    }
                }
                catch(Exception e) {
                    try { Thread.sleep(10); } catch(InterruptedException ie) { }
                    run();
                }
                finally {
                    if(!semReleased) {
                        done.release(); // inform the caller that this tile is now complete
                        semReleased = true;
                    }
                }
            }
        }
        Semaphore firstDone = new Semaphore(0);
        es.submit(new ImageFiller(0, 0, width, height, firstDone));
        try { firstDone.acquire(); } catch(InterruptedException e) { }
        if(allDone != null) { allDone.release(); } // signal the caller that we are done
        return img;
    }    
}