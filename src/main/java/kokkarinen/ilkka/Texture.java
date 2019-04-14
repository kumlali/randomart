package kokkarinen.ilkka;

import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class Texture extends Node {

    public static class ImageFileFilter implements FileFilter {
        public boolean accept(File f) {
            if(f.isDirectory()) { return true; }
            String name = f.getName().toLowerCase();
            return name.endsWith("png") ||
            name.endsWith("jpg") ||
            name.endsWith("gif") ||
            name.endsWith("jpeg");
        }
    }
    private static JPanel jc = new JPanel();
    public static ArrayList<BufferedImage> images = new ArrayList<>();
    public static ArrayList<String> imageNames = new ArrayList<>();
    public static void addImagesFromFolder(File folder) {    
        try {
            MediaTracker mt = new MediaTracker(jc);
            File[] files;
            if(folder.isDirectory()) {
                System.out.println("Reading folder " + folder.getName());
                files = folder.listFiles(new ImageFileFilter());
            }
            else {
                files = new File[1];
                files[0] = folder;
            }
            Image img;
            for(File f: files) {
                try {
                    if(f.isDirectory()) {
                        addImagesFromFolder(f);
                    }
                    else {
                        System.out.println("Reading image: " + f.getName());
                        img = Toolkit.getDefaultToolkit().getImage(f.getCanonicalPath());
                        mt.addImage(img, 0);
                        try { mt.waitForAll(); } catch(InterruptedException e) { }
                        BufferedImage bimg = new BufferedImage(
                            img.getWidth(jc), img.getHeight(jc), BufferedImage.TYPE_INT_RGB
                        );
                        bimg.getGraphics().drawImage(img, 0, 0, jc);
                        images.add(bimg);
                        imageNames.add(f.getName());
                    }    
                }
                catch(Exception e) { System.out.println("Error: " + e); }
            }
        }
        catch(Exception e) {
            System.out.println("Error: " + e);
        }
    }
    
    private BufferedImage img;
    protected String imgName;
    private static final double DISPLACEMENT = 0.03;
    public int getArity() { return 0; }
    protected double wr, wg, wb;
    public Texture() { }
    public Texture(Random rng) {
        if(images.size() == 0) { img = null; }
        else {
            int idx = rng.nextInt(images.size());
            img = images.get(idx);
            imgName = imageNames.get(idx);
        }
        wr = rng.nextDouble() * 0.6;
        wg = rng.nextDouble() * (1 - wr);
        wb = 1 - wr - wg;
    }
    public double evaluate(double x, double y) {
        if(img == null) {
            if(imgName != null) {
                for(int i = 0; i < imageNames.size(); i++) {
                    if(imgName.equals(imageNames.get(i))) {
                        img = images.get(i); break;
                    }
                }
            }
            if(img == null) {
                //return 0.5 * (Math.sin(x) * Math.cos(y) + Math.cos(x * x) * Math.sin(y * y));
                return wr * Math.cos(x) + (1 - wr) * Math.sin(x);
            }
        }
        x = scaleToUnity(x);
        y = scaleToUnity(y);
        int rgb = img.getRGB((int)(x * (img.getWidth() - 1)), (int)(y * (img.getHeight() - 1)));
        int r = (rgb >> 16) & 0xff;
        int g = (rgb >> 8) & 0xff;
        int b = rgb & 0xff;
        return boundsCheck((wr * r + wg * g + wb * b) / 128.0 - 1);
    }
}